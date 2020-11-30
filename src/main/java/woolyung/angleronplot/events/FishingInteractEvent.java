package woolyung.angleronplot.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.FishingManager;
import woolyung.angleronplot.datas.CaughtFishData;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.fishingsystem.FishingFinishEvent;
import woolyung.angleronplot.fishingsystem.FishingThread;

import java.util.HashMap;

public class FishingInteractEvent implements Listener {
    FishingManager manager;

    public FishingInteractEvent() {
        manager = AnglerOnPlot.getInstance().getManager();
    }

    @EventHandler
    public void onPlayerFishEvent(FishingFinishEvent event) {
        AnglerOnPlot plugin = AnglerOnPlot.getInstance();

        Player player = event.getPlayer();
        CaughtFishData fish = event.getFish();

        HashMap<Player, FishingThread> threads = AnglerOnPlot.getInstance().getPlayerThread();
        if (threads.containsKey(player)) threads.remove(player);

        if (event.getResult() == FishingFinishEvent.Result.FAIL) {
            String name = manager.getRankColor(fish.rank) + fish.name;
            player.sendMessage(String.format(plugin.getConfig().getString("message.fishing.fail.player"), name));

            if (fish.rank == FishData.Rank.LEGENDARY || fish.rank == FishData.Rank.RARE)
                plugin.getServer().broadcastMessage(String.format(plugin.getConfig().getString("message.fishing.fail.broadcast"), player.getName(), name));
        }
        else if (event.getResult() == FishingFinishEvent.Result.SUCCESS) {
            // 아이템 지급
            // 도감 등록

            String name = manager.getRankColor(fish.rank) + fish.name;
            player.sendMessage(String.format(plugin.getConfig().getString("message.fishing.rill_in.success"), fish.length, name));

            if (fish.rank == FishData.Rank.LEGENDARY || fish.rank == FishData.Rank.RARE)
                plugin.getServer().broadcastMessage(String.format(plugin.getConfig().getString("message.fishing.rill_in.success_broadcast"), player.getName(), fish.length, name));
        }
    }

    @EventHandler
    public void onClickEvent(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            HashMap<Player, FishingThread> threads = AnglerOnPlot.getInstance().getPlayerThread();
            if (threads.containsKey(event.getPlayer())) {
                FishingThread thread = threads.get(event.getPlayer());

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    if (thread.dir == 1) {
                        thread.gage++;
                    }
                    else {
                        if (thread.ok >= 1) thread.ok--;
                        else thread.gage--;
                    }
                    manager.sendFishingTitle(event.getPlayer(), thread.dir, thread.gage);
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    if (thread.dir == 0) {
                        thread.gage++;
                    }
                    else {
                        if (thread.ok >= 1) thread.ok--;
                        else thread.gage--;
                    }
                    manager.sendFishingTitle(event.getPlayer(), thread.dir, thread.gage);
                }
            }
        }
    }
}