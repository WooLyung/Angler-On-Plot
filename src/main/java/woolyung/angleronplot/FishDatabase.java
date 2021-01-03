package woolyung.angleronplot;

import org.bukkit.Bukkit;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.FishDataEx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FishDatabase {
    private Connection connection;
    private Statement statement;
    private String path = "";
    private String file = "";
    private AnglerOnPlot plugin;

    public FishDatabase(AnglerOnPlot plugin) {
        this.plugin = plugin;
        sqliteSetup();
    }

    private void sqliteSetup() {
        path = plugin.getDataFolder() + "/datas/";
        file = "fish.db";

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path + file);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("jdbc 클래스를 찾을 수 없습니다. 플러그인을 비활성화합니다.");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("데이터베이스 예외가 발생했습니다. 플러그인을 비활성화합니다.");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        createTable();
    }

    private void createTable() {
        try {
            statement.execute("PRAGMA foreign_keys = ON");

            // 플롯 오염도 데이터베이스
            if (statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE Name = 'plot_pollution'").getInt(1) == 0)
                statement.execute("CREATE TABLE plot_pollution (plot TEXT PRIMARY KEY, pollution REAL)");

            // 어류 데이터 데이터베이스
            if (statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE Name = 'fish'").getInt(1) == 0)
                statement.execute("CREATE TABLE fish (name TEXT PRIMARY KEY, power INTEGER, rank TEXT, min_size REAL, max_size REAL, min_temp REAL, max_temp REAL, min_current REAL, max_current REAL, min_poll REAL, max_poll REAL)");

            // 아종 데이터 데이터베이스
            if (statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE Name = 'subspecies'").getInt(1) == 0)
                statement.execute("CREATE TABLE subspecies (species TEXT, name TEXT, biome TEXT, other TEXT, FOREIGN KEY(species) REFERENCES fish(name) ON DELETE CASCADE)");

            // 도감 데이터베이스
            if (statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE Name = 'pedia'").getInt(1) == 0)
                statement.execute("CREATE TABLE pedia (species TEXT, player TEXT)");
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("데이터베이스 예외가 발생했습니다. 플러그인을 비활성화합니다.");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
    }

    public boolean isExistPedia(String species, String uuid) {
        try {
            if (statement.executeQuery("SELECT count(*) FROM pedia WHERE species = '" + species + "' AND player = '" + uuid + "'").getInt(1) == 0) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    public HashMap<String, Boolean> getCaughtFishes(String uuid) {
        HashMap<String, Boolean> map = new HashMap<>();

        try {
            ResultSet result = statement.executeQuery("SELECT * FROM pedia WHERE player = '" + uuid + "'");
            String species = result.getString("species");
            map.put(species, true);
        } catch (Exception e) {
            return new HashMap<>();
        }

        return map;
    }

    public boolean addPediaData(String species, String uuid) {
        try {
            if (statement.executeQuery("SELECT count(*) FROM pedia WHERE species = '" + species + "' AND player = '" + uuid + "'").getInt(1) == 0) {
                statement.execute("INSERT INTO pedia VALUES('" + species + "', '" + uuid + "')");
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    public float getPollution(int x, int z) {
        try {
            if (statement.executeQuery("SELECT count(*) FROM plot_pollution WHERE plot = '" + x + ":" + z + "'").getInt(1) == 0) {
                float pollution = Math.round(new Random().nextFloat() * 10000) * 0.01f;
                statement.execute("INSERT INTO plot_pollution VALUES('" + x + ":" + z + "', " + pollution + ")");
                return pollution;
            }
            else {
                ResultSet result = statement.executeQuery("SELECT * FROM plot_pollution WHERE plot = '" + x + ":" + z + "'");
                return result.getInt("pollution");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 100;
        }
    }

    public ArrayList<FishData> getFishingables(float temp, float current, float pollution, int depth, String biome, String other) {
        ArrayList<FishData> fishingables = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        try {
            ResultSet result = statement.executeQuery("SELECT name FROM fish WHERE (" + temp + " >= min_temp AND " + temp + " <= max_temp AND " + pollution + " >= min_poll AND " + pollution + " <= max_poll AND " + current + " >= min_current AND " + current + " <= max_current AND " + (depth * 20) + " >= max_size)");
            while (result.next()) {
                names.add(result.getString("name"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (String name : names) {
            fishingables.add(getFishData(name));
            FishDataEx subData = getSubspeciesData(name, biome, other);
            if (subData != null) {
                fishingables.add(subData);
            }
        }

        return fishingables;
    }

    public ArrayList<FishData> getFishingables(float temp, float current, float pollution, int depth, String rank, String biome, String other) {
        ArrayList<FishData> fishingables = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        try {
            ResultSet result = statement.executeQuery("SELECT name FROM fish WHERE (" + temp + " >= min_temp AND " + temp + " <= max_temp AND " + pollution + " >= min_poll AND " + pollution + " <= max_poll AND " + current + " >= min_current AND " + current + " <= max_current AND " + (depth * 10) + " >= max_size AND rank = '" + rank + "')");
            while (result.next()) {
                names.add(result.getString("name"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (String name : names) {
            fishingables.add(getFishData(name));
            FishDataEx subData = getSubspeciesData(name, biome, other);
            if (subData != null) {
                fishingables.add(subData);
            }
        }

        return fishingables;
    }

    public ArrayList<FishData> getAllFish() {
        ArrayList<FishData> fishes = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        try {
            ResultSet result = statement.executeQuery("SELECT name FROM fish");
            while (result.next()) {
                names.add(result.getString("name"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        names.sort((o1, o2) -> o1.compareTo(o2));

        for (String name : names) {
            fishes.add(getFishData(name));
            for (FishDataEx dataEx : getSubspecieses(name)) {
                fishes.add(dataEx);
            }
        }

        return fishes;
    }

    public ArrayList<FishDataEx> getSubspecieses(String species) {
        ArrayList<FishDataEx> subspecieses = new ArrayList<>();
        ArrayList<String> subspeciesNames = new ArrayList<>();

        try {
            ResultSet result = statement.executeQuery("SELECT * FROM subspecies WHERE (species = '" + species + "')");
            while (result.next()) {
                subspeciesNames.add(result.getString("name"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (String name : subspeciesNames) {
            subspecieses.add(getSubspeciesData(name));
        }

        return subspecieses;
    }

    public FishDataEx getSubspeciesData(String species, String biome, String other) {
        try {
            FishDataEx data = new FishDataEx();

            if (statement.executeQuery("SELECT count(*) FROM subspecies WHERE (species = '" + species + "' AND biome = '" + biome + "' AND other = '" + other + "')").getInt(1) == 0)
                return null;

            ResultSet result = statement.executeQuery("SELECT * FROM subspecies WHERE (species = '" + species + "' AND biome = '" + biome + "' AND other = '" + other + "')");
            data.name = result.getString("name");
            FishData data2 = getFishData(result.getString("species"));

            data.max_current = data2.max_current;
            data.min_current = data2.min_current;
            data.max_poll = data2.max_poll;
            data.min_poll = data2.min_poll;
            data.max_temp = data2.max_temp;
            data.min_temp = data2.min_temp;
            data.max_size = data2.max_size;
            data.min_size = data2.min_size;
            data.power = data2.power;
            data.rank = data2.rank;
            data.species = species;
            data.biome = biome;
            data.other = other;

            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FishDataEx getSubspeciesData(String name) {
        try {
            FishDataEx data = new FishDataEx();

            if (statement.executeQuery("SELECT count(*) FROM subspecies WHERE (name = '" + name + "')").getInt(1) == 0)
                return null;

            ResultSet result = statement.executeQuery("SELECT * FROM subspecies WHERE (name = '" + name + "')");
            data.biome = result.getString("biome");
            data.other = result.getString("other");

            FishData data2 = getFishData(result.getString("species"));

            data.max_current = data2.max_current;
            data.min_current = data2.min_current;
            data.max_poll = data2.max_poll;
            data.min_poll = data2.min_poll;
            data.max_temp = data2.max_temp;
            data.min_temp = data2.min_temp;
            data.max_size = data2.max_size;
            data.min_size = data2.min_size;
            data.rank = data2.rank;
            data.species = data2.name;
            data.name = name;
            data.power = data2.power;

            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FishData getFishData(String name) {
        try {
            FishData data = new FishData();

            if (statement.executeQuery("SELECT count(*) FROM fish WHERE (name = '" + name + "')").getInt(1) == 0)
                return null;

            ResultSet result = statement.executeQuery("SELECT * FROM fish WHERE (name = '" + name + "')");
            data.max_current = result.getFloat("max_current");
            data.min_current = result.getFloat("min_current");
            data.max_poll = result.getFloat("max_poll");
            data.min_poll = result.getFloat("min_poll");
            data.max_temp = result.getFloat("max_temp");
            data.min_temp = result.getFloat("min_temp");
            data.max_size = result.getFloat("max_size");
            data.min_size = result.getFloat("min_size");
            data.power = result.getInt("power");
            data.name = name;
            if (result.getString("rank").compareTo("common") == 0) {
                data.rank = FishData.Rank.COMMON;
            }
            else if (result.getString("rank").compareTo("special") == 0) {
                data.rank = FishData.Rank.SPECIAL;
            }
            else if (result.getString("rank").compareTo("rare") == 0) {
                data.rank = FishData.Rank.RARE;
            }
            else if (result.getString("rank").compareTo("legendary") == 0) {
                data.rank = FishData.Rank.LEGENDARY;
            }
            else {
                data.rank = FishData.Rank.VALUELESS;
            }

            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
