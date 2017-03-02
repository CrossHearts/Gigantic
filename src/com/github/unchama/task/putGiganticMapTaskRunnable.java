package com.github.unchama.task;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.yml.ConfigManager;

public class putGiganticMapTaskRunnable extends BukkitRunnable {
	Gigantic plugin = Gigantic.plugin;
	ConfigManager config = Gigantic.yml.getManager(ConfigManager.class);
	HashMap<UUID, GiganticPlayer> gmap = PlayerManager.gmap;
	HashMap<UUID, GiganticPlayer> tmpmap;

	// 試行回数
	int attempt;
	// 最大試行回数
	int max_attempt;

	public putGiganticMapTaskRunnable(HashMap<UUID, GiganticPlayer> tmpmap) {
		this.tmpmap = tmpmap;
		this.attempt = -1;
		this.max_attempt = config.getMaxAttempt();
	}

	@Override
	public void run() {
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (tmpmap.isEmpty()) {
					cancel();
					return;
				}
				// 一人ずつinitを実行
				for (GiganticPlayer gp : tmpmap.values()) {
					if (!gp.isloaded())
						continue;
					gp.init();
					gmap.put(gp.uuid, gp);
					tmpmap.remove(gp.uuid);
				}
				// 試行回数
				if (attempt++ >= max_attempt) {
					cancel();
					return;
				}
			}
		});
	}

}
