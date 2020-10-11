package woolyung.angleronplot;

import org.bukkit.Bukkit;
import woolyung.main.MineplanetPlot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
        path = MineplanetPlot.instance.getDataFolder() + "/datas/";
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
                statement.execute("CREATE TABLE fish (name TEXT PRIMARY KEY, power INTEGER, rank TEXT, min_size REAL, max_size REAL, min_temp REAL, max_temp REAL, min_cureent REAL, max_current REAL, min_poll REAL, max_poll REAL)");

            // 아종 데이터 데이터베이스
            if (statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE Name = 'subspecies'").getInt(1) == 0)
                statement.execute("CREATE TABLE subspecies (species TEXT, name TEXT, biome TEXT, other TEXT, FOREIGN KEY(species) REFERENCES fish(name) ON DELETE CASCADE)");
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("데이터베이스 예외가 발생했습니다. 플러그인을 비활성화합니다.");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }
    }
}
