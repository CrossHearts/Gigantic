package com.github.unchama.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.github.unchama.gigantic.Gigantic;

public class PlayerClickListener  implements Listener{
	Gigantic plugin = Gigantic.plugin;

	@EventHandler
	public void onPlayerOpenMenuEvent(PlayerInteractEvent event){
		//プレイヤーを取得
		Player player = event.getPlayer();
		//プレイヤーが起こしたアクションを取得
		Action action = event.getAction();
		//アクションを起こした手を取得
		EquipmentSlot equipmentslot = event.getHand();

		if(!player.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
			return;
		}

		if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)){
			return;
		}

		if(equipmentslot.equals(EquipmentSlot.OFF_HAND)){
			return;
		}

		event.setCancelled(true);

		//開く音を再生
		player.playSound(player.getLocation(), Sound.BLOCK_FENCE_GATE_OPEN, 1, (float) 0.1);
		/*Inventory inv = MenuInventoryData.getMenuData(player);
		if(inv == null){
			return;
		}
		player.openInventory(inv);*/
	}
}
