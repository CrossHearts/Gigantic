package com.github.unchama.player.mineblock;

import java.util.HashMap;

import org.bukkit.Material;

import com.github.unchama.player.DataManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.UsingSql;
import com.github.unchama.sql.MineBlockTableManager;

public class MineBlockManager extends DataManager implements UsingSql{

	public HashMap<BlockType,MineBlock> datamap = new HashMap<BlockType,MineBlock>();
	public long allmineblock;
	MineBlockTableManager tm;



	//new Player Instance
	public MineBlockManager(GiganticPlayer gp){
		super(gp);
		this.tm = sql.getMineBlockTableManager();
	}


	public void increase(Material material){
		this.increase(material,1);
	}
	/**破壊した数を引数に整地量を加算
	 *
	 * @param material
	 * @param breaknum
	 */
	public void increase(Material material, int breaknum) {
		double ratio = BlockType.getIncreaseRatio(material);
		BlockType bt = BlockType.getmaterialMap().get(material);
		datamap.get(bt).increase(breaknum * ratio);
	}


	@Override
	public void save() {
		tm.save(gp);
	}

	@Override
	public void load(){
		tm.load(gp);
	}







}
