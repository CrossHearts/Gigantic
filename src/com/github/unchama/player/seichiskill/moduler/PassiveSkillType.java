package com.github.unchama.player.seichiskill.moduler;

import com.github.unchama.player.seichiskill.passive.manarecovery.ManaRecoveryManager;
import com.github.unchama.player.seichiskill.passive.mineboost.MineBoostManager;
import com.github.unchama.player.seichiskill.passive.securebreak.SecureBreakManager;

/**
 * @author tar0ss
 *
 */
public enum PassiveSkillType {
	MINEBOOST(MineBoostManager.class),MANARECOVERY(ManaRecoveryManager.class),SECUREBREAK(SecureBreakManager.class)
	;
	private Class<? extends PassiveSkillManager> skillClass;

	PassiveSkillType(Class<? extends PassiveSkillManager> skillClass) {
		this.skillClass = skillClass;
	}

	/**
	 * スキルを管理するクラスを取得します．
	 *
	 * @return
	 */
	public Class<? extends PassiveSkillManager> getSkillClass() {
		return skillClass;
	}
}
