package woolyung.angleronplot;

import org.bukkit.plugin.java.JavaPlugin;
import woolyung.angleronplot.commands.FishCommand;

import java.io.File;

public final class AnglerOnPlot extends JavaPlugin {

    private static AnglerOnPlot instance;
    private FishDatabase fishDatabase;
    private FishingManager manager;

    public static AnglerOnPlot getInstance() {
        return instance;
    }

    public FishingManager getManager() {
        return manager;
    }

    public FishDatabase getFishDatabase() {
        return fishDatabase;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (createPluginDirectory());{
            createDataDirectory();
        }

        createConfig();
        init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void init() {
        fishDatabase = new FishDatabase(this);
        manager = new FishingManager(fishDatabase);

        getCommand("fish").setExecutor(new FishCommand(this));
    }

    private boolean createPluginDirectory() {
        File folder = getDataFolder();

        if (!folder.exists()) {
            getLogger().info("플러그인 폴더가 존재하지 않습니다. 생성을 시도합니다.");

            try {
                folder.mkdir();
                getLogger().info("플러그인 폴더 생성을 성공했습니다.");
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                getLogger().info("플러그인 폴더 생성에 실패했습니다. 플러그인을 비활성화합니다.");
                getPluginLoader().disablePlugin(this);
                return false;
            }
        }
        else {
            return true;
        }
    }

    private boolean createDataDirectory() {
        File folder = new File(getDataFolder().getPath() + "/datas");

        if (!folder.exists()) {
            getLogger().info("데이터 폴더가 존재하지 않습니다. 생성을 시도합니다.");

            try {
                folder.mkdir();
                getLogger().info("데이터 폴더 생성을 성공했습니다.");
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                getLogger().info("데이터 폴더 생성에 실패했습니다. 플러그인을 비활성화합니다.");
                getPluginLoader().disablePlugin(this);
                return false;
            }
        }
        else {
            return true;
        }
    }
}