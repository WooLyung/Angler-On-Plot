package woolyung.angleronplot.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.datas.CaughtFishData;
import woolyung.angleronplot.fishingsystem.FishingFinishEvent;
import woolyung.angleronplot.fishingsystem.FishingThread;

import java.util.HashMap;

public class FishingInteractEvent implements Listener {
    @EventHandler
    public void onPlayerFishEvent(FishingFinishEvent event) {
        Player player = event.getPlayer();
        CaughtFishData fish = event.getFish();

        player.sendMessage("길이:" + fish.length + ", 수컷:" + fish.isMale + ", 이름:" + fish.name + ", 등급:" + fish.rank + ", 결과:" + event.getResult());
    }

    @EventHandler
    public void onClickEvent(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            HashMap<Player, FishingThread> threads = AnglerOnPlot.getInstance().getPlayerThread();
            if (threads.containsKey(event.getPlayer())) {
                FishingThread thread = threads.get(event.getPlayer());

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    if (thread.dir == 1) thread.gage++;
                    else {
                        if (thread.ok >= 1) thread.ok--;
                        else thread.gage--;
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    if (thread.dir == 0) thread.gage++;
                    else {
                        if (thread.ok >= 1) thread.ok--;
                        else thread.gage--;
                    }
                }
            }
        }
    }
}