package com.github.unchama.listener;


import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.build.BuildData;
import com.github.unchama.player.build.BuildManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceEventListener implements Listener {
    Gigantic plugin = Gigantic.plugin;

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event){

        //Playerを取得
        Player player = event.getPlayer();
        //カウント対象かどうか
        if(BuildData.isBlockCount(player) == false){
            player.sendMessage("このワールドでは建築量は増えません");//TODO:Debug用
            return;
        }

        Block b = event.getBlock();
        GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
        if(gp == null){
            plugin.getLogger().warning(ChatColor.RED + "BlockPlaceEventのプレイヤーデータがnull");
            plugin.getLogger().warning(ChatColor.RED + "開発者に報告してください");
            return;
        }

        gp.getManager(BuildManager.class).addBuild_Num_1min(1);
    }
}
