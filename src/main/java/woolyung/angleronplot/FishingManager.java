package woolyung.angleronplot;

import org.bukkit.Location;
import org.bukkit.Material;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.PlotData;
import woolyung.main.MineplanetPlot;

import java.util.Random;

public class FishingManager {

    private FishDatabase database;

    public FishingManager(FishDatabase database) {
        this.database = database;
    }

    public PlotData getPlotData(int x, int z) {
        float radius = (float) MineplanetPlot.instance.getConfig().getInt("radius");

        PlotData data = new PlotData();
        data.x = x;
        data.z = z;
        data.temp = (Math.abs(x) / radius) * 34 - 2; // (-2 ~ 32)
        data.temp = 30 - data.temp; // (32 ~ -2)
        data.temp = Math.round(data.temp * 100) * 0.01f;
        data.current = (Math.abs(z) / radius) * 7 + 1; // (1 ~ 8)
        data.current = 9 - data.current; // (8 ~ 1)
        data.current = Math.round(data.current * 100) * 0.01f;
        data.pollution = database.getPollution(x, z);

        return data;
    }

    public int getDepth(Location loc) {
        int depth = 0;

        while (loc.getBlockY() > 0 && loc.getBlock().getType() == Material.WATER) {
            depth++;
            loc = loc.add(0, -1, 0);
        }

        return depth;
    }

    public FishData.Rank getRandomRank() {
        int rank_random = new Random().nextInt(1000);
        FishData.Rank rank;
        if (rank_random <= 1) { // 0.1%, 전설적인
            rank = FishData.Rank.LEGENDARY;
        }
        else if (rank_random <= 60) { // 5.9%, 희귀한
            rank = FishData.Rank.RARE;
        }
        else if (rank_random <= 200) { // 14%, 특별한
            rank = FishData.Rank.SPECIAL;
        }
        else if (rank_random <= 900) { // 70%, 평범한
            rank = FishData.Rank.COMMON;
        }
        else { // 10%, 무가치한
            rank = FishData.Rank.VALUELESS;
        }

        return rank;
    }

    public String getRandomRankString() {
        FishData.Rank rank = getRandomRank();

        if (rank == FishData.Rank.LEGENDARY)
            return "legendary";
        else if (rank == FishData.Rank.RARE)
            return "rare";
        else if (rank == FishData.Rank.SPECIAL)
            return "special";
        else if (rank == FishData.Rank.COMMON)
            return "common";
        else
            return "valueless";
    }
}
