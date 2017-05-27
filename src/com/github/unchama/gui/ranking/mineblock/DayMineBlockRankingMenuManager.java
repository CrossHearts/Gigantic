package com.github.unchama.gui.ranking.mineblock;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.github.unchama.gui.ranking.RankingMenuManager;
import com.github.unchama.sql.moduler.RankingTableManager.TimeType;
import com.github.unchama.util.TimeUtil;
import com.github.unchama.util.Util;

public class DayMineBlockRankingMenuManager extends RankingMenuManager {

	@Override
	protected String getLore(double value) {
		return "総整地量:" + Util.Decimal(value);
	}

	@Override
	public String getInventoryName(Player player) {
		String date = TimeUtil.getDateTimeName(TimeType.DAY, 0);
		return ChatColor.BLUE + "日間整地量ﾗﾝｷﾝｸﾞ(" + date + "~)";
	}

}
