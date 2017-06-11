package com.github.unchama.player.seichiskill.effect;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.github.unchama.player.seichiskill.EffectCategory;

/**
 * @author tar0ss
 *
 */
public enum EffectType {
	NORMAL(0,ChatColor.GREEN + "通常","エフェクトなし",0,Material.GRASS),
	DYNAMITE(1, ChatColor.RED + "ダイナマイト", "単純な爆発", 50, Material.TNT),
	BLIZZARD(2, ChatColor.AQUA + "ブリザード", "凍らせる", 70, Material.PACKED_ICE),
	METEO(3, ChatColor.DARK_RED + "メテオ", "隕石を落とす", 100, Material.FIREBALL), ;

	//識別番号
	private final int id;
	//エフェクト名
	private final String name;
	//説明文
	private final String lore;
	//使用する投票ポイント
	private final int useVotePoint;
	//メニューで使用するマテリアル
	private final Material menuMaterial;

	/**
	 * @param name
	 * @param lore
	 * @param useVotePoint
	 * @param menuMaterial
	 */
	private EffectType(int id, String name, String lore, int useVotePoint, Material menuMaterial) {
		this.id = EffectCategory.NORMAL.getEffectID(id);
		this.name = name;
		this.lore = lore;
		this.useVotePoint = useVotePoint;
		this.menuMaterial = menuMaterial;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return lore
	 */
	public String getLore() {
		return lore;
	}

	/**
	 * @return useVotePoint
	 */
	public int getUseVotePoint() {
		return useVotePoint;
	}

	/**
	 * @return menuMaterial
	 */
	public Material getMenuMaterial() {
		return menuMaterial;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

}
