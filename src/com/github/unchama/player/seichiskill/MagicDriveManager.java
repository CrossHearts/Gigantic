package com.github.unchama.player.seichiskill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.unchama.listener.GeneralBreakListener;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.mana.ManaManager;
import com.github.unchama.player.mineblock.MineBlockManager;
import com.github.unchama.player.minestack.MineStackManager;
import com.github.unchama.player.seichilevel.SeichiLevelManager;
import com.github.unchama.player.seichiskill.moduler.Coordinate;
import com.github.unchama.player.seichiskill.moduler.SkillManager;
import com.github.unchama.player.seichiskill.moduler.Volume;
import com.github.unchama.player.sidebar.SideBarManager;
import com.github.unchama.player.sidebar.SideBarManager.Information;
import com.github.unchama.sql.MagicDriveTableManager;
import com.github.unchama.task.CoolDownTaskRunnable;
import com.github.unchama.util.breakblock.BreakUtil;
import com.github.unchama.yml.DebugManager.DebugEnum;

public class MagicDriveManager extends SkillManager{

	MagicDriveTableManager tm;

	boolean preflag;

	public MagicDriveManager(GiganticPlayer gp) {
		super(gp);
		tm = sql.getManager(MagicDriveTableManager.class);
		preflag = false;
	}

	@Override
	public boolean run(Player player, ItemStack tool, Block block) {

		// エフェクト用に壊されるブロック全てのリストデータ
		List<Block> breaklist = new ArrayList<Block>();

		// 壊される液体のリストデータ
		List<Block> liquidlist = new ArrayList<Block>();

		// プレイヤーの向いている方角の破壊ブロック座標リストを取得
		List<Coordinate> breakcoord = this.getRange().getBreakCoordList(player);

		// まず破壊するブロックの総数を計算
		breakcoord.forEach(c -> {
			Block rb = block.getRelative(c.getX(), c.getY(), c.getZ());
			Material m = rb.getType();
			// マテリアルを確認
				if (SkillManager.canBreak(m)) {
					// worldguardを確認Skilledflagを確認
					if (Wg.canBuild(player, rb.getLocation())
							&& !rb.hasMetadata("Skilled")) {
						if (canBelowBreak(player, block, rb)) {
							if (SkillManager.isLiquid(m)) {
								liquidlist.add(rb);
							} else {
								breaklist.add(rb);
							}
						}
					}
				}
			});

		if (breaklist.isEmpty()) {
			player.sendMessage(this.getJPName() + ChatColor.RED
					+ ":発動できるブロックがありません．自分より下のブロックはしゃがみながら破壊できます．");
			return false;
		}

		// ツールの耐久を確認

		short durability = tool.getDurability();
		boolean unbreakable = tool.getItemMeta().spigot().isUnbreakable();
		//使用する耐久値
		short useDurability = 0;

		if (!unbreakable) {
			if(durability > tool.getType().getMaxDurability()){
				player.sendMessage(this.getJPName() + ChatColor.RED
						+ ":ツールの耐久値が不正です．");
				return false;
			}
			useDurability = (short) (BreakUtil.calcDurability(
				tool.getEnchantmentLevel(Enchantment.DURABILITY),
				breaklist.size() + liquidlist.size()));
				//ツールの耐久が足りない時
			if(tool.getType().getMaxDurability() <= (durability + useDurability)) {
				//入れ替え可能
				if(Pm.replace(player,useDurability,tool)){
					durability = tool.getDurability();
					unbreakable = tool.getItemMeta().spigot().isUnbreakable();
					if(unbreakable)useDurability = 0;
				}else{
					player.sendMessage(this.getJPName() + ChatColor.RED
							+ ":発動に必要なツールの耐久値が足りません");
					return false;
				}
			}
		}
		// マナを確認
		double usemana = this.getMana(breaklist.size() + liquidlist.size() * 2);

		if (!Mm.hasMana(usemana)) {
			player.sendMessage(this.getJPName() + ChatColor.RED
					+ ":発動に必要なマナが足りません");
			return false;
		}

		// break直前の処理
		List<ItemStack> droplist = new ArrayList<ItemStack>();
		breaklist
				.forEach((b) -> {
					// ドロップアイテムをリストに追加
					droplist.addAll(BreakUtil.getDrops(b, tool));
					// MineBlockに追加
					gp.getManager(MineBlockManager.class).increase(b.getType(),
							1);
					debug.sendMessage(player, DebugEnum.SKILL, b.getType()
							.name()
							+ " is increment("
							+ 1
							+ ")for player:"
							+ player.getName());
					// スキルで使用するブロックに設定
					b.setMetadata("Skilled", new FixedMetadataValue(plugin,
							true));
					// アイテムが出現するのを検知させる
					Location droploc = GeneralBreakListener.getDropLocation(b);
					GeneralBreakListener.breakmap.put(droploc,
							player.getUniqueId());
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							GeneralBreakListener.breakmap.remove(droploc);
						}
					}, 1);
				});

		liquidlist.forEach(b -> {
			// スキルで使用するブロックに設定
				b.setMetadata("Skilled", new FixedMetadataValue(plugin, true));
			});

		// MineStackに追加
		MineStackManager m = gp.getManager(MineStackManager.class);
		droplist.forEach((dropitem) -> {
			if (m.add(dropitem)) {
				debug.sendMessage(player, DebugEnum.SKILL,
						"your item is added in minestack");
			} else {
				player.getInventory().addItem(dropitem);
				debug.sendMessage(player, DebugEnum.SKILL,
						"your item is added in inventory");
			}
		});

		// 最初のブロックのみコアプロテクトに保存する．
		SkillManager.logRemoval(player, block);

		// breakの処理
		liquidlist.forEach(b -> {
			b.setType(Material.AIR);
		});
		breaklist.forEach(b -> {
			if (SkillManager.canBreak(b.getType())) {
				// 通常エフェクトの表示
				/*
				 * if (!b.equals(block)) w.playEffect(b.getLocation(),
				 * Effect.STEP_SOUND, b.getType());
				 */
				// ブロックを削除
				b.setType(Material.AIR);
			}
		});

		// break後の処理
		liquidlist.forEach(b -> {
			b.removeMetadata("Skilled", plugin);
		});
		breaklist.forEach(b -> {
			b.removeMetadata("Skilled", plugin);
		});

		// レベルを更新
		if (gp.getManager(SeichiLevelManager.class).updateLevel()) {
			int level = gp.getManager(SeichiLevelManager.class).getLevel();
			gp.getManager(ManaManager.class).Levelup();
			gp.getManager(SideBarManager.class).updateInfo(
					Information.SEICHI_LEVEL, level);
		}
		double rb = gp.getManager(SeichiLevelManager.class).getRemainingBlock();
		gp.getManager(SideBarManager.class).updateInfo(Information.MINE_BLOCK,
				rb);
		gp.getManager(SideBarManager.class).refresh();

		int cooltime = this.getCoolTime(breaklist.size());

		Mm.decrease(usemana);
		tool.setDurability((short) (durability + useDurability));
		if (cooltime > 5)
			new CoolDownTaskRunnable(gp, cooltime, st)
					.runTaskTimerAsynchronously(plugin, 0, 1);
		return true;
	}
	@Override
	public void save(Boolean loginflag) {
		tm.save(gp, loginflag);
	}


	@Override
	protected ItemStack getItemStackonLocked() {
		return new ItemStack(Material.STAINED_GLASS, 1, (short) 11);
	}

	@Override
	public String getJPName() {
		return "" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD
				+ "マジックドライブ" + ChatColor.RESET;
	}

	@Override
	public Material getMenuMaterial() {
		return Material.LAPIS_ORE;
	}

	@Override
	public int getUnlockLevel() {
		return 50;
	}

	@Override
	public long getUnlockAP() {
		return 20;
	}

	@Override
	public double getMana(int breaknum) {
		return breaknum / (Math.pow(breaknum, 0.1666667)) - 1;
	}

	@Override
	public int getCoolTime(int breaknum) {
		return (int) ((Math.pow(breaknum, 0.23255814)) - 1) * 20;
	}

	@Override
	public long getSpendAP(int breaknum) {
		return (long) breaknum * 1;
	}

	@Override
	public int getMaxBreakNum() {
		return 2000;
	}

	@Override
	public int getMaxHeight() {
		return 55;
	}

	@Override
	public int getMaxWidth() {
		return 35;
	}

	@Override
	public int getMaxDepth() {
		return 20;
	}

	@Override
	protected boolean canBelowBreak(Player player, Block block, Block rb) {
		int playerlocy = player.getLocation().getBlockY() - 1;
		//int blocky = block.getY();
		int rblocy = rb.getY();
		int zeroy = this.getRange().getZeropoint().getY();
		int voly = this.getRange().getVolume().getHeight() - 1;

		// 破壊する高さが起点の高さと同じ場合は無関係に破壊する
		if (zeroy == voly) {
			return true;
			// それ以外の場合は自分の高さ以上のブロックのみ破壊する
		} else if (playerlocy < rblocy || player.isSneaking()) {
			return true;
		} else {
			return false;
		}
		/*

		// プレイヤーの足元以下のブロックを起点に破壊していた場合はtrue
		if (playerlocy >= blocky) {
			return true;
			// 破壊する高さが2以下の場合はプレイヤーより上のブロックのみ破壊する
		} else if (voly <= 1) {
			if (playerlocy < rblocy) {
				return true;
			} else {
				return false;
			}
			// 破壊する高さが起点の高さと同じ場合は無関係に破壊する
		} else if (zeroy == voly) {
			return true;
			// それ以外の場合は自分の高さ以上のブロックのみ破壊する
		} else if (playerlocy < rblocy) {
			return true;
		} else {
			return false;
		}
		*/
	}

	/**
	 * preflagを取得します。
	 * @return preflag
	 */
	public boolean getPreflag() {
	    return preflag;
	}

	/**
	 * preflagを設定します。
	 * @param preflag preflag
	 */
	public void setPreflag(boolean preflag) {
	    this.preflag = preflag;
	}

	@Override
	public long getUsedAp() {
		Volume v = this.getRange().getVolume();
		return this.getSpendAP(v.getVolume() - getDefaultVolume().getVolume());
	}
	@Override
	public Volume getDefaultVolume() {
		return new Volume(1,1,1);
	}
}
