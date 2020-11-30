package woolyung.angleronplot.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.FishDatabase;
import woolyung.angleronplot.FishingManager;
import woolyung.angleronplot.datas.FishData;
import woolyung.angleronplot.datas.FishDataEx;

import java.util.ArrayList;
import java.util.Arrays;

public class PediaGUI implements Listener {
    public static void openPediaGUI(int page, String name, String uuid, Player player) {
        FishingManager manager = AnglerOnPlot.getInstance().getManager();
        FishDatabase database = AnglerOnPlot.getInstance().getFishDatabase();
        Inventory inv = Bukkit.createInventory(null, 54, name + "님의 낚시 도감 " + page + "p");
        ArrayList<FishData> datas = database.getAllFish();

        int index = 0;
        for (FishData data : datas) {
            if (index >= page * 54 - 54 && index <= page * 54 - 1) {
                if (database.isExistPedia(data.name, uuid)) { // 낚은 어류
                    if (data instanceof FishDataEx) {
                        FishDataEx exData = (FishDataEx) data;
                        inv.addItem(createGuiItem(manager.getFishMaterial(data.rank), manager.getRankColor(data.rank) + data.name, String.format("§b크기 §f%.2f~%.2fcm", data.min_size, data.max_size), "§b아종 §f" + exData.species));
                    }
                    else {
                        inv.addItem(createGuiItem(manager.getFishMaterial(data.rank), manager.getRankColor(data.rank) + data.name, String.format("§b크기 §f%.2f~%.2fcm", data.min_size, data.max_size)));
                    }
                }
                else { // 낚은 적이 없는 어류
                    inv.addItem(createGuiItem(Material.GUNPOWDER, "§8" + data.name, "§7낚아보지 못한 어류입니다."));
                }
            }

            index++;
        }

        player.openInventory(inv);
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("낚시 도감")) {
            event.setCancelled(true);
        }
    }
}
