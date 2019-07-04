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

public class Panel implements Listener {

    public static void openPanel(Player p) {
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("panel.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("panel.name")));

        int m = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("panel.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String materialID = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("panel.items." + a + ".item-id");
            int amount = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("panel.items." + a + ".amount");
            int slot = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("panel.items." + a + ".slot");
            String name = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("panel.items." + a + ".item-name"));
            List<String> lore = SkyBlock.getInstance().getUtils().colorList(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getStringList("panel.items." + a + ".item-lore"));

            i.setItem(slot - 1, SkyBlock.getInstance().getUtils().createItem(materialID, 0, name, lore, amount));
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();
        if (i != null) {
            if (i.getName().equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("panel.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        String name = meta.getDisplayName();

                        int m = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("panel.items").getKeys(false).size();
                        for (int a = 1; a <= m; a++) {
                            String name2 = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("panel.items." + a + ".item-name"));

                            if (name.equalsIgnoreCase(name2)) {
                                //run the commands in the config
                                List<String> commands = SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getStringList("panel.items." + a + ".commands");
                                for (String s : commands) {
                                    Bukkit.dispatchCommand(p, s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}