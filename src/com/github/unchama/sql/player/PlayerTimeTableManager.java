package com.github.unchama.sql.player;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.time.PlayerTimeManager;
import com.github.unchama.seichi.sql.PlayerDataTableManager;
import com.github.unchama.sql.Sql;
import com.github.unchama.sql.moduler.PlayerFromSeichiTableManager;

/**
 * @author tar0ss
 *
 */
public class PlayerTimeTableManager extends PlayerFromSeichiTableManager {
	public PlayerTimeTableManager(Sql sql) {
		super(sql);
	}

	@Override
	protected String addColumnCommand() {
		String command = "";
		command += "add column if not exists playtick int default 0,"
				+ "add column if not exists totalidletick int default 0,";
		return command;
	}

	@Override
	public void loadPlayer(GiganticPlayer gp, ResultSet rs) throws SQLException {
		PlayerTimeManager m = gp.getManager(PlayerTimeManager.class);
		m.setPlaytick(rs.getInt("playtick"));
		m.setTotalIdletick(rs.getInt("totalidletick"));
		m.reloadSevertick();
	}

	@Override
	protected String saveCommand(GiganticPlayer gp) {
		PlayerTimeManager m = gp.getManager(PlayerTimeManager.class);
		String command = "";
		command += "playtick = '" + m.getPlaytick() + "',"
				+ "totalidletick = '" + m.getTotalIdletick() + "',";
		return command;
	}

	@Override
	protected void takeoverPlayer(GiganticPlayer gp, PlayerDataTableManager tm) {
		PlayerTimeManager m = gp.getManager(PlayerTimeManager.class);
		m.setPlaytick(tm.getPlayTick(gp));
	}

	@Override
	protected void firstjoinPlayer(GiganticPlayer gp) {
		PlayerTimeManager m = gp.getManager(PlayerTimeManager.class);
		m.setPlaytick(0);
		m.setLocation(null);
		m.setIdletime(0);
		m.setTotalIdletick(0);
	}
}
