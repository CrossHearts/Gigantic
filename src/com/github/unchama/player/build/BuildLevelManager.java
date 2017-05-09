package com.github.unchama.player.build;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;

import com.github.unchama.event.BuildLevelUpEvent;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.moduler.DataManager;
import com.github.unchama.player.moduler.Initializable;

public class BuildLevelManager extends DataManager implements Initializable{

	//建築レベル
	private int buildlevel;
	// 各レベルのデータ値を格納します．
	public static LinkedHashMap<Integer, BuildLevelData> buildlevelmap = new LinkedHashMap<Integer, BuildLevelData>(){
		{
			for(int level = 1; level<= 100; level++){
				put(level, new BuildLevelData(level));
			}
		}
	};

	public BuildLevelManager(GiganticPlayer gp) {
		super(gp);
	}
	@Override
	public void init() {
		this.calcLevel();
	}

	/**レベルアップ可能かを調べるメソッドです
	 * @param buildnum:建築量
	 * @param buildlevel:建築レベル
	 * @return
	 */
	public boolean canLevelup() {
		double buildnum = gp.getManager(BuildManager.class).getTotalbuildnum();
		return (buildlevelmap.get(buildlevel + 1).getNeed_buildnum() <= buildnum) ? true : false;
	}

	/**初期処理でプレイヤーのレベルを取得します
	 *
	 */
	public void calcLevel() {
		while(canLevelup()) {
			buildlevel++;
		}
	}

	/**レベルが上がるまで、レベルデータを更新します
	 *
	 * @return 1でも上がったらtrue
	 */
	public boolean updateLevel(){
		boolean changeflag = false;
		while(this.canLevelup()){
			Bukkit.getServer().getPluginManager().callEvent(new BuildLevelUpEvent(gp, buildlevel + 1));
			buildlevel++;
			changeflag = true;
		}
		return changeflag;
	}

	/**レベルアップまでに必要な建築量を取得します
	 *
	 * @return
	 */
	public double getRemainingBuildBlock(){
		double buildnum = gp.getManager(BuildManager.class).getTotalbuildnum();
		return this.buildlevel < 100 ? (double)buildlevelmap.get(this.buildlevel + 1).getNeed_buildnum() - buildnum: 0.0;
	}

	/**現在のプレイヤーの建築レベルを取得します
	 *
	 * @return
	 */
	public int getBuildLevel(){
		return this.buildlevel;
	}
}
