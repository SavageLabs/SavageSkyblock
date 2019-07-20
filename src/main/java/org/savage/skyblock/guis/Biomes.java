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

import static org.savage.skyblock.island.upgrades.UpgradesUI.createFadedItem;

public class Biomes implements Listener {

    public static void openBiome(Player p) {
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("biomes.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.name")));

        for (String s : SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        int m = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getConfigurationSection("biomes.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String materialID = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.items." + a + ".item-id");
            int amount = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("biomes.items." + a + ".amount");
            int slot = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("biomes.items." + a + ".slot");
            String name = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.items." + a + ".item-name"));
            List<String> lore = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("biomes.items." + a + ".item-lore"));

            i.setItem(slot - 1, SkyBlock.getInstance().getUtils().createItem(materialID, 0, name, lore, amount));
        }
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getClickedInventory();
        if (i != null) {
            String iName = e.getView().getTitle();
            if (iName == null) return;
            if (iName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack clicked = e.getCurrentItem();
                    if (clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        String name = meta.getDisplayName();

                        int m = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getConfigurationSection("biomes.items").getKeys(false).size();
                        for (int a = 1; a <= m; a++) {
                            String name2 = SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.items." + a + ".item-name"));
                            String perm = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("biomes.items." + a + ".permission");

                            if (name.equalsIgnoreCase(name2)) {
                                //create island
                                if (p.hasPermission(perm)){
                                    //run the commands
                                    List<String> commands = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("biomes.items." + a + ".commands");
                                    for (String s : commands) {
                                        Bukkit.dispatchCommand(p, s);
                                    }
                                    p.closeInventory();
                                }else{
                                    //no permission
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionBiome"));
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