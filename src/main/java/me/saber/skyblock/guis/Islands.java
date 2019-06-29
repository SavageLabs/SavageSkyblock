package me.saber.skyblock.guis;

import me.saber.skyblock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Islands implements Listener {

    public static void openIslands(Player p) {
        Inventory i = Bukkit.createInventory(null, Main.getInstance().getConfig().getInt("islands.rows") * 9, Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("islands.name")));

        int m = Main.getInstance().getConfig().getConfigurationSection("islands.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String materialID = Main.getInstance().getConfig().getString("islands.items." + a + ".item-id");
            int amount = Main.getInstance().getConfig().getInt("islands.items." + a + ".amount");
            int slot = Main.getInstance().getConfig().getInt("islands.items." + a + ".slot");
            String name = Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("islands.items." + a + ".item-name"));
            List<String> lore = Main.getInstance().getUtils().colorList(Main.getInstance().getConfig().getStringList("islands.items." + a + ".item-lore"));

            i.setItem(slot - 1, Main.getInstance().getUtils().createItem(materialID, 0, name, lore, amount));
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();
        if (i != null) {
            if (i.getName().equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("islands.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        String name = meta.getDisplayName();

                        int m = Main.getInstance().getConfig().getConfigurationSection("islands.items").getKeys(false).size();
                        for (int a = 1; a <= m; a++) {
                            String name2 = Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("islands.items." + a + ".item-name"));
                            String schematic = Main.getInstance().getConfig().getString("islands.items."+a+".schematic");
                            String perm = Main.getInstance().getConfig().getString("islands.items."+a+".permission");

                            if (name.equalsIgnoreCase(name2)) {
                                //create island
                                Main.getInstance().getIslandUtils().createIsland(p, perm, schematic);
                            }
                        }
                    }
                }
            }
        }
    }
}