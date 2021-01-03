package woolyung.angleronplot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import woolyung.angleronplot.datas.CaughtFishData;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.PlotData;
import woolyung.main.MineplanetPlot;

import java.util.ArrayList;
import java.util.Random;

public class FishingManager {

    private FishDatabase database;
    private boolean isGold = false;

    public FishingManager(FishDatabase database) {
        this.database = database;
    }

    public void setIsGold(boolean isGold) {
        this.isGold = isGold;
    }

    public boolean getIsGold() {
        return isGold;
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

    public FishData.Rank getRandomRank(int luck) {
        int rank_random = new Random().nextInt(1000);
        FishData.Rank rank;

        if (luck == 0) {
            if (rank_random <= 1) { // 0.1%, 전설적인
                rank = FishData.Rank.LEGENDARY;
            }
            else if (rank_random <= 40) { // 3.9%, 희귀한
                rank = FishData.Rank.RARE;
            }
            else if (rank_random <= 200) { // 16%, 특별한
                rank = FishData.Rank.SPECIAL;
            }
            else if (rank_random <= 900) { // 70%, 평범한
                rank = FishData.Rank.COMMON;
            }
            else { // 10%, 무가치한
                rank = FishData.Rank.VALUELESS;
            }
        }
        else if (luck == 1) {
            if (rank_random <= 1) { // 0.1%, 전설적인
                rank = FishData.Rank.LEGENDARY;
            }
            else if (rank_random <= 45) { // 4.4%, 희귀한
                rank = FishData.Rank.RARE;
            }
            else if (rank_random <= 220) { // 17.5%, 특별한
                rank = FishData.Rank.SPECIAL;
            }
            else if (rank_random <= 920) { // 70%, 평범한
                rank = FishData.Rank.COMMON;
            }
            else { // 8%, 무가치한
                rank = FishData.Rank.VALUELESS;
            }
        }
        else if (luck == 2) {
            if (rank_random <= 1) { // 0.1%, 전설적인
                rank = FishData.Rank.LEGENDARY;
            }
            else if (rank_random <= 50) { // 4.9%, 희귀한
                rank = FishData.Rank.RARE;
            }
            else if (rank_random <= 240) { // 19%, 특별한
                rank = FishData.Rank.SPECIAL;
            }
            else if (rank_random <= 940) { // 70%, 평범한
                rank = FishData.Rank.COMMON;
            }
            else { // 6%, 무가치한
                rank = FishData.Rank.VALUELESS;
            }
        }
        else {
            if (rank_random <= 1) { // 0.1%, 전설적인
                rank = FishData.Rank.LEGENDARY;
            }
            else if (rank_random <= 55) { // 5.4%, 희귀한
                rank = FishData.Rank.RARE;
            }
            else if (rank_random <= 260) { // 20.5%, 특별한
                rank = FishData.Rank.SPECIAL;
            }
            else if (rank_random <= 960) { // 70%, 평범한
                rank = FishData.Rank.COMMON;
            }
            else { // 4%, 무가치한
                rank = FishData.Rank.VALUELESS;
            }
        }

        return rank;
    }

    public String getRandomRankString(int luck) {
        FishData.Rank rank = getRandomRank(luck);

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

    public String getRankString(FishData.Rank rank) {
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
            return 0.2f;
        else if (rank.compareTo("special") == 0)
            return 0.6f;
        else if (rank.compareTo("common") == 0)
            return 0.95f;
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
            player.sendTitle("§1§l<§9§l<§b§l< §f왼쪽 §b§l<§9§l<§1§l<", "§3§l|" + bar + "§3§l|", 0, 15, 5);
        else
            player.sendTitle("§1§l>§9§l>§b§l> §f오른쪽 §b§l>§9§l>§1§l>", "§3§l|" + bar + "§3§l|", 0, 15, 5);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, gage * 0.075f + 0.5f);
    }

    public void changeDir(Player player, int dir) {
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 0.3f, 0.8f);
    }

    public Material getFishMaterial(FishData.Rank rank) {
        Material mat;
        if (rank == FishData.Rank.LEGENDARY) mat = Material.TROPICAL_FISH;
        else if (rank == FishData.Rank.RARE) mat = Material.SALMON;
        else if (rank == FishData.Rank.SPECIAL) mat = Material.COOKED_SALMON;
        else if (rank == FishData.Rank.COMMON) mat = Material.COD;
        else mat = Material.COOKED_COD;

        return mat;
    }

    public ItemStack getFishItem(Player player, CaughtFishData fish) {
        ItemStack item;
        if (fish.rank == FishData.Rank.LEGENDARY) item = new ItemStack(Material.TROPICAL_FISH);
        else if (fish.rank == FishData.Rank.RARE) item = new ItemStack(Material.SALMON);
        else if (fish.rank == FishData.Rank.SPECIAL) item = new ItemStack(Material.COOKED_SALMON);
        else if (fish.rank == FishData.Rank.COMMON) item = new ItemStack(Material.COD);
        else item = new ItemStack(Material.COOKED_COD);
        item.setAmount(1);

        ItemMeta meta = item.getItemMeta();
        if (fish.isMale)
            meta.setDisplayName(getRankColor(fish.rank) + fish.name + " §9♂");
        else
            meta.setDisplayName(getRankColor(fish.rank) + fish.name + " §d♀");

        ArrayList<String> lore = new ArrayList<>();
        lore.add(String.format("§b길이 §f%.2fcm", fish.length));
        lore.add("§b낚은이 §f" + player.getName());
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public int getRankPrice(FishData.Rank rank) {
        if (rank == FishData.Rank.LEGENDARY) {
            return 200000000;
        }
        else if (rank == FishData.Rank.RARE) {
            return 500000;
        }
        else if (rank == FishData.Rank.SPECIAL) {
            return 60000;
        }
        else if (rank == FishData.Rank.COMMON) {
            return 5000;
        }
        else {
            return 100;
        }
    }

    public int getPrice(ItemStack item) {
        if (item == null) return 0;
        if (item.getType() == Material.AIR) return 0;
        ItemMeta meta = item.getItemMeta();

        if (!(meta.getDisplayName().contains("") || meta.getDisplayName().contains(""))) return 0; // 어류가 아님

        String name = meta.getDisplayName();
        name = name.replace(" §9♂", "");
        name = name.replace(" §d♀", "");
        name = name.replace("§8", "");
        name = name.replace("§f", "");
        name = name.replace("§e", "");
        name = name.replace("§b", "");
        name = name.replace("§5", "");
        FishData fishData = database.getSubspeciesData(name);
        if (fishData == null) fishData = database.getFishData(name);
        if (fishData == null) return 0; // 데이터가 없음

        String length = meta.getLore().get(0);
        length = length.replace("§b길이 §f", "");
        length = length.replace("cm", "");

        float lengthValue = 0;
        try {
            lengthValue = Float.parseFloat(length);
        }
        catch (Exception e) {
            return 0; // 플로트 변환 실패
        }

        float min_length = fishData.min_size;
        float max_length = fishData.max_size;
        float avg_length = (min_length + max_length) * 0.5f;
        float weight = lengthValue / avg_length;
        float price = weight * getRankPrice(fishData.rank);
        if (meta.getDisplayName().contains("♀")) price *= 1.2f;
        price *= item.getAmount();

        return (int) Math.round(price);
    }
}