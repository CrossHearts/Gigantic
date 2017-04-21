package com.github.unchama.player.toolpouch;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.moduler.DataManager;
import com.github.unchama.player.moduler.UsingSql;
import com.github.unchama.player.seichilevel.SeichiLevelManager;
import com.github.unchama.player.seichiskill.moduler.SkillManager;
import com.github.unchama.sql.ToolPouchTableManager;

public class ToolPouchManager extends DataManager implements UsingSql {
	ToolPouchTableManager tm;

	private Inventory pouch;

	public ToolPouchManager(GiganticPlayer gp) {
		super(gp);
		tm = sql.getManager(ToolPouchTableManager.class);
	}

	@Override
	public void save(Boolean loginflag) {
		tm.save(gp, loginflag);
	}

	/**
	 * @return pouch
	 */
	public Inventory getPouch() {
		return pouch;
	}

	/**
	 * @param pouch
	 *            セットする pouch
	 */
	public void setPouch(Inventory pouch) {
		this.pouch = pouch;
	}

	/**
	 * ポーチのサイズを取得
	 *
	 * @return
	 */
	public int getSize() {
		int level = gp.getManager(SeichiLevelManager.class).getLevel();
		int row = (int) level / 15 + 1;
		return row > 6 ? 9 * 6 : 9 * row;
	}

	/**
	 * ポーチの名前を取得
	 *
	 * @return
	 */
	public String getName() {
		return "" + ChatColor.AQUA + ChatColor.BOLD + "ツールポーチ";
	}

	public void open(Player player) {
		resize();
		player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 0.4F);
		player.openInventory(getPouch());
	}

	// サイズの更新処理
	public void resize() {
		if (pouch.getSize() != this.getSize()) {
			Inventory n_pouch = Bukkit.getServer().createInventory(null, this.getSize(),
					this.getName());
			if(pouch.getSize() <= this.getSize()){
				n_pouch.setContents(pouch.getContents());
			}
			pouch = n_pouch;
		}
	}

	public boolean replace(Player player,short useDurability,ItemStack handtool) {
		for(int i = 0; i < pouch.getSize() ; i++){
			ItemStack pouchtool = pouch.getItem(i);
			if(pouchtool == null)continue;
			if(SkillManager.canBreak(pouchtool)){
				if(pouchtool.getType().getMaxDurability() > pouchtool.getDurability() + useDurability){
					pouch.setItem(i, handtool);
					player.getInventory().setItemInMainHand(pouchtool);
					player.sendMessage(ChatColor.YELLOW + "ツールポーチからツールを引き出しました．");
					player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0F, 1.7F);
					return true;
				}
			}
		}
		return false;
	}

}
