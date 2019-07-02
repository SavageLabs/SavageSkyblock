package org.savage.skyblock.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.Main;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.island.Island;

import java.util.List;

public class ISTop implements Listener {

    public static void openISTop(Player p) {
        Inventory i = Bukkit.createInventory(null, Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("istop.rows") * 9, Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("istop.name")));

        int m = Main.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("istop.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String id = Main.getInstance().getFileManager().guiFile.getFileConfig().getString("istop.items." + a + ".item-id");
            String name = Main.getInstance().getFileManager().guiFile.getFileConfig().getString("istop.items." + a + ".item-name");
            List<String> lore = Main.getInstance().getFileManager().guiFile.getFileConfig().getStringList("istop.items." + a + ".item-lore");
            int slot = Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("istop.items." + a + ".slot");
            int data = Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("istop.items." + a + ".item-data");

            int islandTopNumber;

            islandTopNumber = Placeholder.getIslandTopPlacement(name);

            if (islandTopNumber > 0) {
                //Bukkit.broadcastMessage("TOP: " + islandTopNumber);
                if (Main.getInstance().getIslandUtils().getIslandFromPlacement(islandTopNumber) != null) {
                    Island island = Main.getInstance().getIslandUtils().getIslandFromPlacement(islandTopNumber);
                    name = name.replace("%top-" + islandTopNumber + "%", island.getName());
                    lore = Placeholder.convertPlaceholders(lore, island);
                } else {
                    //  Bukkit.broadcastMessage("no");
                }
            }

            ItemStack item = Main.getInstance().getUtils().createItem(id, data, name, lore, 1);
            i.setItem(slot - 1, item);
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();
        Inventory bottom = p.getOpenInventory().getBottomInventory();

        if (top != null && top.getName().equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("istop.name")))) {
            e.setCancelled(true);
        }
    }
}