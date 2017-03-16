package com.github.unchama.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.mineboost.BoostType;
import com.github.unchama.player.mineboost.MineBoostManager;

public class PlayerQuitListener implements Listener {
	Gigantic plugin = Gigantic.plugin;

	@EventHandler(priority = EventPriority.NORMAL)
	public void removeGiganticPlayerEvent(PlayerQuitEvent event) {
		PlayerManager.quit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void updataGiganticPlayer(PlayerQuitEvent event) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (p.equals(event.getPlayer()))
				continue;
			GiganticPlayer gp = PlayerManager.getGiganticPlayer(p);
			if(gp == null)continue;
			gp.getManager(MineBoostManager.class).updata(BoostType.NUMBER_OF_PEOPLE);
			gp.getManager(MineBoostManager.class).refresh();
		}
	}

}
