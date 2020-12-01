package woolyung.angleronplot.gui;

import com.earth2me.essentials.api.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import woolyung.angleronplot.AnglerOnPlot;
import woolyung.angleronplot.FishingManager;

import java.math.BigDecimal;
import java.util.Arrays;

public class ShopGUI implements Listener {
    public static void openShopGUI(Player player) {
        FishingManager manager = AnglerOnPlot.getInstance().getManager();
        Inventory inv = Bukkit.createInventory(null, 54, "낚시 상점");

        inv.setItem(45, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(46, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(47, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(48, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(50, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(51, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(52, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(53, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, "§f"));
        inv.setItem(49, createGuiItem(Material.GOLD_INGOT, "§e어류 판매", "§fGUI 안의 모든 어류를 판매합니다."));

        player.openInventory(inv);
    }

    public static ItemStack createGuiItem(final Material material, final String name) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
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
        if (event.getView().getTitle().contains("낚시 상점")) {
            if (event.getRawSlot() == 45
            || event.getRawSlot() == 46
            || event.getRawSlot() == 47
            || event.getRawSlot() == 48
            || event.getRawSlot() == 50
            || event.getRawSlot() == 51
            || event.getRawSlot() == 52
            || event.getRawSlot() == 53)
            event.setCancelled(true);

            if (event.getRawSlot() == 49) {
                event.setCancelled(true);
                sellFish(event.getClickedInventory(), (Player) event.getView().getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("낚시 상점")) {
            sellFish(event.getInventory(), (Player) event.getView().getPlayer(), false);
        }
    }

    private static void sellFish(Inventory inventory, Player player, boolean msg) {
        AnglerOnPlot plugin = AnglerOnPlot.getInstance();
        FishingManager manager = plugin.getManager();

        int sumPrice = 0;
        int count = 0;

        for (int slot = 0; slot < 45; slot++) {
            ItemStack item = inventory.getItem(slot);
            int price = manager.getPrice(item);
            if (price != 0) {
                inventory.setItem(slot, new ItemStack(Material.AIR));
                sumPrice += price;
                count++;
            }
        }

        if (sumPrice == 0) {
            if (msg)
                player.sendMessage(plugin.getConfig().getString("message.shop.no_fish"));
        }
        else {
            player.sendMessage(String.format(plugin.getConfig().getString("message.shop.sell_fish"), count, sumPrice));
            try {
                Economy.add(player.getUniqueId(), new BigDecimal(sumPrice));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
