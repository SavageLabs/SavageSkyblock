package org.savage.skyblock.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.savage.skyblock.SkyBlock;

import java.util.List;

public class Islands implements Listener {

    public static void openIslands(Player p) {
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("islands.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.name")));

        int m = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("islands.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String materialID = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.items." + a + ".item-id");
            int amount = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("islands.items." + a + ".amount");
            int slot = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("islands.items." + a + ".slot");
            String name = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.items." + a + ".item-name"));
            List<String> lore = SkyBlock.getInstance().getUtils().colorList(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getStringList("islands.items." + a + ".item-lore"));

            i.setItem(slot - 1, SkyBlock.getInstance().getUtils().createItem(materialID, 0, name, lore, amount));
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();
        if (i != null) {
            if (i.getName().equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        String name = meta.getDisplayName();

                        int m = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("islands.items").getKeys(false).size();
                        for (int a = 1; a <= m; a++) {
                            String name2 = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.items." + a + ".item-name"));
                            String schematic = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.items." + a + ".schematic");
                            String perm = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("islands.items." + a + ".permission");

                            if (name.equalsIgnoreCase(name2)) {
                                //create island
                                SkyBlock.getInstance().getIslandUtils().createIsland(p, perm, schematic);
                            }
                        }
                    }
                }
            }
        }
    }
}