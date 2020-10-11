package woolyung.angleronplot;

import woolyung.angleronplot.datas.PlotData;
import woolyung.main.MineplanetPlot;

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
}
