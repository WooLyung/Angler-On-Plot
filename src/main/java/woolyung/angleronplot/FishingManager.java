package woolyung.angleronplot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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

    public String getRankColor(String rank) {
        if (rank.compareTo("legendary") == 0)
            return "§5";
        else if (rank.compareTo("rare") == 0)
            return "§b";
        else if (rank.compareTo("special") == 0)
            return "§e";
        else if (rank.compareTo("common") == 0)
            return "§f";
        else
            return "§7";
    }

    public String getRankColor(FishData.Rank rank) {
        if (rank == FishData.Rank.LEGENDARY)
            return "§5";
        else if (rank == FishData.Rank.RARE)
            return "§b";
        else if (rank == FishData.Rank.SPECIAL)
            return "§e";
        else if (rank == FishData.Rank.COMMON)
            return "§f";
        else
            return "§7";
    }

    public float getFishingChance(String rank) {
        if (rank.compareTo("legendary") == 0)
            return 0.01f;
        else if (rank.compareTo("rare") == 0)
            return 0.1f;
        else if (rank.compareTo("special") == 0)
            return 0.4f;
        else if (rank.compareTo("common") == 0)
            return 0.9f;
        else
            return 1;
    }

    public void sendFishingTitle(Player player, int dir, int gage) {
        gage = (gage / 2) + 10;
        if (gage <= 0) gage = 0;
        if (gage >= 20) gage = 20;

        String bar = "§b";
        for (int i = 0; i < gage; i++)
            bar += "|";
        bar += "§f";
        for (int i = 0; i < 20 - gage; i++)
            bar += "|";

        if (dir == 0)
            player.sendTitle("§1§l<§9§l<§b§l< §f왼쪽! §b§l<§9§l<§1§l<", "§3§l|" + bar + "§3§l|", 0, 15, 5);
        else
            player.sendTitle("§1§l>§9§l>§b§l> §f오른쪽! §b§l>§9§l>§1§l>", "§3§l|" + bar + "§3§l|", 0, 15, 5);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, gage * 0.075f + 0.5f);
    }

    public void changeDir(Player player, int dir) {
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 0.5f, 0.8f);
    }
}
