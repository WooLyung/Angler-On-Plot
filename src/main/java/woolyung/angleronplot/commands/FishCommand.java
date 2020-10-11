package woolyung.angleronplot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.datas.PlotData;
import woolyung.main.MineplanetPlot;
import woolyung.main.plot.Data.PlotLocData;

public class FishCommand implements CommandExecutor {
    private AnglerOnPlot plugin;

    public FishCommand(AnglerOnPlot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(plugin.getConfig().getString("message.command.no_arg"));
            }
            else if (args[0].compareTo("help") == 0 || args[0].compareTo("?") == 0) {
                // 도움말
            }
            else if (args[0].compareTo("plot") == 0) {
                arg_plot(sender, command, label, args, player);
            }
        }
         return true;
    }

    private void arg_plot(CommandSender sender, Command command, String label, String[] args, Player player) {
        if (!player.getWorld().equals(MineplanetPlot.instance.getPlotWorld().getWorld())) {
            player.sendMessage(MineplanetPlot.instance.getConfig().getString("message.plot.not_plot_world")); // 플롯 월드가 아님
            return;
        }

        int player_posX = player.getLocation().getBlockX();
        int player_posZ = player.getLocation().getBlockZ();
        PlotLocData plotLocData = MineplanetPlot.instance.getPlotWorld().getPlotLocData(player_posX, player_posZ);

        int x = plotLocData.plotLocX;
        int z = plotLocData.plotLocZ;
        PlotData data = AnglerOnPlot.getInstance().getManager().getPlotData(x, z);

        player.sendMessage("§b[Fish] ─────────────────────────");
        player.sendMessage("§b · §7플롯주소 §f: [" + x + ":" + z + "]");
        player.sendMessage("§b · §7수온 §f: " + data.temp + "℃");
        player.sendMessage("§b · §7해류 §f: " + data.current + "km/h");
        player.sendMessage("§b · §7오염도 §f: " + data.pollution + "%");
    }
}
