package woolyung.angleronplot.fishingsystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.datas.CaughtFishData;
import woolyung.angleronplot.datas.FishData;

import java.util.Random;

public class FishingThread extends Thread {
    private AnglerOnPlot plugin;
    private boolean isFinish = false;
    private Player player;
    private FishData fish;
    private CaughtFishData caughtFishData;
    public int gage = 0;
    public int dir = 0;
    public int ok = 6;
    private double time = 0;
    private double nextTime = 0;
    private double powerTerm = 0;

    public void setIsFinish(boolean flag) {
        isFinish = flag;
    }

    public FishingThread(Player player, FishData fish) {
        this.player = player;
        this.fish = fish;

        plugin = AnglerOnPlot.getInstance();
        powerTerm = getPowerTerm(fish.power);
        nextTime = new Random().nextDouble() + 1;
        CaughtFishInit();
    }

    private void CaughtFishInit() {
        caughtFishData = new CaughtFishData();
        caughtFishData.isMale = new Random().nextFloat() >= 0.5;
        caughtFishData.name = fish.name;
        caughtFishData.rank = fish.rank;

        float min = fish.min_size;
        float max = fish.max_size;
        float bet = max - min;
        caughtFishData.length = (new Random().nextFloat() * bet) + min;
    }

    @Override
    public void run() {
        while (!isFinish) {
            try {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // 값  변경
                    gage--;
                    time += powerTerm;

                    // 방향 전환
                    if (time >= nextTime) {
                        time = 0;
                        dir = dir == 1 ? 0 : 1;
                        nextTime = new Random().nextDouble() + 1;
                        ok = 6;
                    }

                    // 성공
                    if (gage >= 20) {
                        Bukkit.getPluginManager().callEvent(new FishingFinishEvent(player, caughtFishData, FishingFinishEvent.Result.SUCCESS));
                        isFinish = true;
                    }
                    else if (gage <= -20) { // 실패
                        Bukkit.getPluginManager().callEvent(new FishingFinishEvent(player, caughtFishData, FishingFinishEvent.Result.FAIL));
                        isFinish = true;
                    }

                    if (dir == 1)
                        player.sendMessage("오른쪽" + ", 게이지" + gage);
                    else
                        player.sendMessage("왼쪽" + ", 게이지" + gage);
                });

                // 슬립
                Thread.sleep((int)(powerTerm * 1000));
            }
            catch (Exception e) {
                e.printStackTrace();
                isFinish = true;
            }
        }

        if (plugin.getPlayerThread().containsKey(player))
            plugin.getPlayerThread().remove(player);
    }

    private static double getPowerTerm(int power) {
        if (power == 1) return 0.6;
        if (power == 2) return 0.5;
        if (power == 3) return 0.45;
        if (power == 4) return 0.4;
        if (power == 5) return 0.35;
        if (power == 6) return 0.3;
        if (power == 7) return 0.25;
        if (power == 8) return 0.2;
        if (power == 9) return 0.15;
        if (power == 10) return 0.1;
        return 0.5;
    }
}
