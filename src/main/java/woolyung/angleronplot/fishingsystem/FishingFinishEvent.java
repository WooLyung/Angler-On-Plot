package woolyung.angleronplot.fishingsystem;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import woolyung.angleronplot.datas.CaughtFishData;

public class FishingFinishEvent extends Event {
    public enum Result {
        FAIL, SUCCESS, DEBUG
    }

    private Player player;
    private CaughtFishData fish;
    private Result result;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public FishingFinishEvent(Player player, CaughtFishData fish, Result result){
        this.player = player;
        this.fish = fish;
        this.result = result;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public CaughtFishData getFish() {
        return fish;
    }

    public Result getResult() {
        return result;
    }
}