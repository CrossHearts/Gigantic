package com.github.unchama.player.seichilevel;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;

import com.github.unchama.event.SeichiLevelUpEvent;
import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.mineblock.MineBlock.TimeType;
import com.github.unchama.player.mineblock.MineBlockManager;
import com.github.unchama.player.moduler.DataManager;
import com.github.unchama.player.moduler.Initializable;
import com.github.unchama.player.skill.moduler.SkillManager;
import com.github.unchama.player.skill.moduler.SkillType;
import com.github.unchama.yml.ConfigManager;

public class SeichiLevelManager extends DataManager implements Initializable {

	// 各レベルのデータ値を格納します．
	public static LinkedHashMap<Integer, SeichiLevel> levelmap;

	// 整地レベル
	private int level;

	public SeichiLevelManager(GiganticPlayer gp) {
		super(gp);
	}

	@Override
	public void init() {
		this.calcLevel();
	}
	/**
	 * LevelMapをセットします． Enable時に一度だけ処理してください．
	 *
	 */
	public static void setLevelMap() {
		levelmap = new LinkedHashMap<Integer, SeichiLevel>();
		long sum_ap = 0;
		long get_ap = 1;
		for (int level = 1; level <= Gigantic.yml
				.getManager(ConfigManager.class).getMaxSeichiLevel(); level++) {
			sum_ap += get_ap;
			levelmap.put(level, new SeichiLevel(level, get_ap, sum_ap));
			if (level % 10 == 0 && level < 80) {
				get_ap *= 2;
			}
		}
	}
	
	/**与えられたapをプレイヤが所持しているか取得
	 * 
	 * @param ap
	 * @return
	 */
	public boolean hasAP(long ap){
		SeichiLevel sl = levelmap.get(this.level);
		long sumap = sl.getSumAp();
		long useap = 0;
		for(SkillType st : SkillType.values()){
			SkillManager s = (SkillManager)gp.getManager(st.getSkillClass());
			if(s.isunlocked()){
				useap += s.getUnlockAP();
			}
		}
		
		long dif = sumap - useap;
		return dif < ap ? false : true;
	}
	/**
	 * レベルアップ可能かどうか調べるメソッドです．
	 *
	 * @param level
	 *            レベル
	 * @param d
	 *            mineblock量
	 * @return
	 */
	private boolean canLevelup() {
		double d = gp.getManager(MineBlockManager.class).all
				.getNum(TimeType.UNLIMITED);
		return ((double)levelmap.get(level).getNextMineBlock() <= d && level < config
				.getMaxSeichiLevel()) ? true : false;
	}

	/**
	 * 初期処理でプレイヤーのレベルを取得します．
	 *
	 */
	private void calcLevel() {
		this.level = 1;
		while (this.canLevelup()) {
			this.level++;
		}
	}

	/**
	 * レベルが上がるまでレベルデータを更新します．
	 *
	 * @return １でも上がった場合trueとなる．
	 */
	public boolean updateLevel() {
		boolean changeflag = false;
		while (this.canLevelup()) {
			Bukkit.getServer().getPluginManager()
					.callEvent(new SeichiLevelUpEvent(gp, level + 1));
			level++;
			changeflag = true;
		}
		return changeflag;
	}

	/**
	 * レベルアップまでに必要な整地量を調べます．
	 * @return
	 */
	public double getRemainingBlock() {
		double d = gp.getManager(MineBlockManager.class).all
				.getNum(TimeType.UNLIMITED);

		return this.level < config.getMaxSeichiLevel() ? (double)levelmap.get(this.level)
				.getNextMineBlock() - d : 0.0;
	}
	/**現在のプレイヤーの整地レベルを取得します．
	 *
	 * @return
	 */
	public int getLevel(){
		return this.level;
	}

	/**プレイヤーのレベルを一時的に設定します．
	 * このレベルはプレイヤーがログアウトすると戻ります．
	 *
	 * @param level
	 */
	public void setLevel(int level) {
		MineBlockManager m = gp.getManager(MineBlockManager.class);
		if(m.debugblock != 0){
			m.all.increase(TimeType.UNLIMITED,-m.debugblock);
		}
		double after = levelmap.get(level).getNeedMineBlock();
		double before = m.all.getNum(TimeType.UNLIMITED);
		//所望レベルまでの必要整地量を計算
		double dif = after - before;
		m.all.increase(TimeType.UNLIMITED,dif);
		m.debugblock = dif;
		this.level = level;
	}
}
