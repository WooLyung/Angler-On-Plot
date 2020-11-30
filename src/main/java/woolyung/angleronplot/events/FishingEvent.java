package woolyung.angleronplot.events;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.FishingManager;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.PlotData;
import woolyung.angleronplot.fishingsystem.FishingThread;
import woolyung.main.MineplanetPlot;
import woolyung.main.plot.Data.PlotDataEx;
import woolyung.main.plot.Data.PlotLocData;

import java.util.ArrayList;
import java.util.Random;

public class FishingEvent implements Listener {
    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        AnglerOnPlot plugin = AnglerOnPlot.getInstance();
        FishingManager manager = plugin.getManager();

        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (!player.getWorld().equals(MineplanetPlot.instance.getPlotWorld().getWorld())) // 월드가 다름
            return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) { // 물고기가 낚임
            event.setCancelled(true);
            event.getHook().remove();

            PlotLocData locData = MineplanetPlot.instance.getPlotWorld().getPlotLocData(hook.getLocation().getBlockX(), hook.getLocation().getBlockZ());
            PlotDataEx plotDataEx = MineplanetPlot.instance.getPlotDatabase().getPlotDataEx(locData.plotLocX, locData.plotLocZ);
            PlotData plotData = manager.getPlotData(locData.plotLocX, locData.plotLocZ);
            int depth = manager.getDepth(hook.getLocation());
            String rank = manager.getRandomRankString();

            ArrayList<FishData> fishingables = plugin.getFishDatabase().getFishingables(plotData.temp, plotData.current, plotData.pollution, depth, rank, plotDataEx.biome, "none");
            if (fishingables != null) {
                if (fishingables.size() == 0) {
                    player.sendMessage(plugin.getConfig().getString("message.fishing.caught.no_fish"));
                }
                else {
                    FishData fishData = fishingables.get(new Random().nextInt(fishingables.size()));
                    FishingThread thread = new FishingThread(player, fishData);

                    if (new Random().nextFloat() > manager.getFishingChance(rank)) {
                        String name = manager.getRankColor(rank) + fishData.name;
                        player.sendMessage(String.format(plugin.getConfig().getString("message.fishing.caught.miss"), name));

                        if (rank.compareTo("legendary") == 0 || rank.compareTo("rare") == 0)
                            plugin.getServer().broadcastMessage(String.format(plugin.getConfig().getString("message.fishing.caught.miss_broadcast"), player.getName(), name));
                    }
                    else {
                        if (rank.compareTo("legendary") == 0 || rank.compareTo("rare") == 0)
                            player.sendMessage(plugin.getConfig().getString("message.fishing.caught.rill_in_special"));
                        else if (rank.compareTo("special") == 0)
                            player.sendMessage(plugin.getConfig().getString("message.fishing.caught.rill_in_heavy"));
                        else
                            player.sendMessage(plugin.getConfig().getString("message.fishing.caught.rill_in_default"));

                        plugin.getPlayerThread().put(player, thread);
                        thread.start();
                    }
                }
            }
        }
    }
}
