package woolyung.angleronplot.events;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import woolyung.angleronplot.AnglerOnPlot;
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
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (!player.getWorld().equals(MineplanetPlot.instance.getPlotWorld().getWorld())) // 월드가 다름
            return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) { // 물고기가 낚임
            event.setCancelled(true);
            event.getHook().remove();

            PlotLocData locData = MineplanetPlot.instance.getPlotWorld().getPlotLocData(hook.getLocation().getBlockX(), hook.getLocation().getBlockZ());
            PlotDataEx plotDataEx = MineplanetPlot.instance.getPlotDatabase().getPlotDataEx(locData.plotLocX, locData.plotLocZ);
            PlotData plotData = AnglerOnPlot.getInstance().getManager().getPlotData(locData.plotLocX, locData.plotLocZ);
            int depth = AnglerOnPlot.getInstance().getManager().getDepth(hook.getLocation());
            String rank = AnglerOnPlot.getInstance().getManager().getRandomRankString();

            ArrayList<FishData> fishingables = AnglerOnPlot.getInstance().getFishDatabase().getFishingables(plotData.temp, plotData.current, plotData.pollution, depth, rank, plotDataEx.biome, "none");
            if (fishingables != null) {
                if (fishingables.size() == 0) {
                    // 아무것도 낚이지 않음
                }
                else {
                    FishData fishData = fishingables.get(new Random().nextInt(fishingables.size()));
                    FishingThread thread = new FishingThread(player, fishData);
                    AnglerOnPlot.getInstance().getPlayerThread().put(player, thread);
                    thread.start();
                }
            }
        }
    }
}
