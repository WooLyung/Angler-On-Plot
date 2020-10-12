package woolyung.angleronplot.events;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.PlotData;
import woolyung.main.MineplanetPlot;
import woolyung.main.plot.Data.PlotDataEx;
import woolyung.main.plot.Data.PlotLocData;

import java.util.ArrayList;

public class FishingEvent implements Listener {
    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (!player.getWorld().equals(MineplanetPlot.instance.getPlotWorld().getWorld())) // 월드가 다름
            return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) { // 물고기가 낚임
            // event.setCancelled(true);

            PlotLocData locData = MineplanetPlot.instance.getPlotWorld().getPlotLocData(hook.getLocation().getBlockX(), hook.getLocation().getBlockZ());
            PlotDataEx plotDataEx = MineplanetPlot.instance.getPlotDatabase().getPlotDataEx(locData.plotLocX, locData.plotLocZ);
            PlotData plotData = AnglerOnPlot.getInstance().getManager().getPlotData(locData.plotLocX, locData.plotLocZ);
            int depth = AnglerOnPlot.getInstance().getManager().getDepth(hook.getLocation());
            String rank = AnglerOnPlot.getInstance().getManager().getRandomRankString();

            player.sendMessage(depth + ", " + plotData.pollution + ", " + plotData.current + ", " + plotData.temp + ", " + rank);

            ArrayList<FishData> fishingables = AnglerOnPlot.getInstance().getFishDatabase().getFishingables(plotData.temp, plotData.current, plotData.pollution, depth, rank, plotDataEx.biome, "none");
            for (FishData data : fishingables) {
                player.sendMessage(data.name);
            }
        }
    }
}
