package com.github.unchama.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;

public class PlayerJoinListener implements Listener {
	Gigantic plugin = Gigantic.plugin;

	@EventHandler(priority = EventPriority.NORMAL)
	public void addGiganticPlayerEvent(PlayerJoinEvent event){
		PlayerManager.join(event.getPlayer());
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void updataGiganticPlayer(PlayerJoinEvent event){
		for(Player p : plugin.getServer().getOnlinePlayers()){
			PlayerManager.getGiganticPlayer(p).getMineBoostManager().updataNumberOfPeople();
		}
	}


}