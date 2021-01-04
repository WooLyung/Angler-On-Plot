package woolyung.angleronplot.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.FishingManager;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.PlotData;
import woolyung.angleronplot.fishingsystem.FishingThread;
import woolyung.main.MineplanetPlot;
import woolyung.main.plot.Data.PlotDataEx;
import woolyung.main.plot.Data.PlotLocData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

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

            if (player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD)
                return;

            PlotLocData locData = MineplanetPlot.instance.getPlotWorld().getPlotLocData(hook.getLocation().getBlockX(), hook.getLocation().getBlockZ());
            PlotDataEx plotDataEx = MineplanetPlot.instance.getPlotDatabase().getPlotDataEx(locData.plotLocX, locData.plotLocZ);
            PlotData plotData = manager.getPlotData(locData.plotLocX, locData.plotLocZ);
            int depth = manager.getDepth(hook.getLocation());
            if (depth >= 120) depth = 1000;
            String rank = manager.getRandomRankString(player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK));
            int durability = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY);

            ArrayList<FishData> fishingables;
            if (plugin.getManager().getIsGold() && Math.abs(locData.plotLocX) <= 2 && Math.abs(locData.plotLocZ) <= 2 ) {
                fishingables = new ArrayList<>();
                for (FishData fish : plugin.getAllFishDatas()) {
                    if (rank.compareTo(plugin.getManager().getRankString(fish.rank)) == 0) {
                        fishingables.add(fish);
                    }
                }
            }
            else {
                fishingables = plugin.getFishDatabase().getFishingables(plotData.temp, plotData.current, plotData.pollution, depth, rank, plotDataEx.biome, "none");
            }

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

                        if (event.getCaught() != null) {
                            event.getCaught().remove();
                            event.setCancelled(false);
                        }

                        HashMap<UUID, Boolean> isRilled = plugin.getIsRilled();
                        if (!isRilled.containsKey(player.getUniqueId())) {
                            isRilled.put(player.getUniqueId(), true);
                        }

                        plugin.getPlayerThread().put(player, thread);
                        thread.start();
                    }
                }
            }
        }
    }
}
