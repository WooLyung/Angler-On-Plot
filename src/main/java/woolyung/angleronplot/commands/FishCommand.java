package woolyung.angleronplot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.datas.PlotData;
import woolyung.angleronplot.gui.PediaGUI;
import woolyung.main.MineplanetPlot;
import woolyung.main.plot.Data.PlayerDataEx;
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
                player.sendMessage("§b[Fish] ─────────────────────────");
                player.sendMessage("§b · §7/fish help §f: 낚시 명령어를 봅니다");
                player.sendMessage("§b · §7/fish plot §f: 플롯의 환경을 확인합니다");
                player.sendMessage("§b · §7/fish pedia [page] [player] §f: 낚시 도감을 봅니다");
                player.sendMessage("§b · §7/fish op §f: 관리자 명령어를 확인합니다 §c[OP]");
            }
            else if (args[0].compareTo("plot") == 0) {
                arg_plot(sender, command, label, args, player);
            }
            else if (args[0].compareTo("pedia") == 0) {
                arg_pedia(sender, command, label, args, player);
            }
            else {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.wrong_command")); // 알 수 없는 명령어
            }
        }
         return true;
    }

    private void arg_pedia(CommandSender sender, Command command, String label, String[] args, Player player) {
        int page;
        String uuid;
        String name;

        if (args.length == 1) {
            page = 1;
            uuid = player.getUniqueId().toString();
            name = player.getName();
        }
        else if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            }
            catch (Exception e) {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.pedia.wrong_page"));
                return;
            }
            if (page < 1) {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.pedia.small_page"));
                return;
            }

            uuid = player.getUniqueId().toString();
            name = player.getName();
        }
        else {
            try {
                page = Integer.parseInt(args[1]);
            }
            catch (Exception e) {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.pedia.wrong_page"));
                return;
            }
            if (page < 1) {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.pedia.small_page"));
                return;
            }

            PlayerDataEx playerDataEx;
            playerDataEx = MineplanetPlot.instance.getPlotDatabase().getPlayerDataExByName(args[2]);

            if (playerDataEx == null) {
                player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.command.pedia.no_player")); // 알 수 없는 플레이어
                return;
            }

            uuid = playerDataEx.uuid;
            name = playerDataEx.name;
        }

        PediaGUI.openPediaGUI(page, name, uuid, player);
    }

    private void arg_plot(CommandSender sender, Command command, String label, String[] args, Player player) {
        if (!player.getWorld().equals(MineplanetPlot.instance.getPlotWorld().getWorld())) {
            player.sendMessage(AnglerOnPlot.getInstance().getConfig().getString("message.plot.not_plot_world")); // 플롯 월드가 아님
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
