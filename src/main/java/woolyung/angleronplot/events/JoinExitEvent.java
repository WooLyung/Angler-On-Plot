package woolyung.angleronplot.events;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.fishingsystem.FishingThread;

import java.util.HashMap;
import java.util.UUID;

public class JoinExitEvent implements Listener {
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        AnglerOnPlot plugin = AnglerOnPlot.getInstance();
        HashMap<Player, FishingThread> threads = plugin.getPlayerThread();
        HashMap<UUID, Boolean> isRilled = plugin.getIsRilled();

        if (threads.containsKey(event.getPlayer())) {
            threads.remove(event.getPlayer());
        }

        if (isRilled.containsKey(event.getPlayer().getUniqueId())) {
            isRilled.remove(event.getPlayer().getUniqueId());
        }
    }
}