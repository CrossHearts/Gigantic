package com.github.unchama.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.unchama.event.MinuteEvent;
import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.UserManager;
import com.github.unchama.yml.Debug;
import com.github.unchama.yml.Debug.DebugEnum;

public class MinuteListener implements Listener{
	private Gigantic plugin = Gigantic.plugin;
	private Debug debug = Gigantic.debug;





	/**MineBoost
	 *
	 * @param event
	 */
	@EventHandler
	public void MineBoostEvent(MinuteEvent event){

		Boolean debugflag = debug.getFlag(DebugEnum.MINEBOOST);
		//run process one by one
		for(Player p : plugin.getServer().getOnlinePlayers()){
			UserManager.getGiganticPlayer(p).getMineBoostManager().updataMinuteMine();
			if(debugflag)p.sendMessage("updata MinuteMine for player:" + p.getName());
		}
	}
}
