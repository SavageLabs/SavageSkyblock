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
import org.savage.skyblock.Main;

import java.util.List;

public class Biomes implements Listener {

    public static void openBiome(Player p) {
        Inventory i = Bukkit.createInventory(null, Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("biomes.rows") * 9, Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.name")));

        int m = Main.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("biomes.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String materialID = Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.items." + a + ".item-id");
            int amount = Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("biomes.items." + a + ".amount");
            int slot = Main.getInstance().getFileManager().guiFile.getFileConfig().getInt("biomes.items." + a + ".slot");
            String name = Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.items." + a + ".item-name"));
            List<String> lore = Main.getInstance().getUtils().colorList(Main.getInstance().getFileManager().guiFile.getFileConfig().getStringList("biomes.items." + a + ".item-lore"));

            i.setItem(slot - 1, Main.getInstance().getUtils().createItem(materialID, 0, name, lore, amount));
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();
        if (i != null) {
            if (i.getName().equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        String name = meta.getDisplayName();

                        int m = Main.getInstance().getFileManager().guiFile.getFileConfig().getConfigurationSection("biomes.items").getKeys(false).size();
                        for (int a = 1; a <= m; a++) {
                            String name2 = Main.getInstance().getUtils().color(Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.items." + a + ".item-name"));
                            String perm = Main.getInstance().getFileManager().guiFile.getFileConfig().getString("biomes.items." + a + ".permission");

                            if (name.equalsIgnoreCase(name2)) {
                                //create island
                                if (p.hasPermission(perm)){
                                    //run the commands
                                    List<String> commands = Main.getInstance().getFileManager().guiFile.getFileConfig().getStringList("biomes.items." + a + ".commands");
                                    for (String s : commands) {
                                        Bukkit.dispatchCommand(p, s);
                                    }
                                    p.closeInventory();
                                }else{
                                    //no permission
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("noPermissionBiome"));
                                    p.closeInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}