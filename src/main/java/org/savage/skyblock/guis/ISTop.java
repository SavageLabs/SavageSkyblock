package org.savage.skyblock.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

import java.util.List;

public class ISTop implements Listener {

    public static void openISTop(Player p) {
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("istop.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("istop.name")));

        int m = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getConfigurationSection("istop.items").getKeys(false).size();
        for (int a = 1; a <= m; a++) {
            String id = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("istop.items." + a + ".item-id");
            String name = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("istop.items." + a + ".item-name");
            int slot = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("istop.items." + a + ".slot");
            int data = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("istop.items." + a + ".item-data");

            List<String> lore;

            boolean placement = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getBoolean("istop.items." + a + ".is-placement");

            if (placement) {
                lore = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("istop.placement-item.item-lore");
                int placementNum = Placeholder.getIslandTopPlacement(name);
                Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromPlacement(placementNum);

                lore = Placeholder.convertPlaceholders(lore, island);

                if (island != null) {
                    name = name.replace("%top-" + placementNum + "%", island.getName());
                } else {
                    name = name.replace("%top-" + placementNum + "%", SkyBlock.getInstance().getUtils().getSettingString("invalid-island-top-name-placeholders"));
                }

            } else {
                lore = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("istop.items." + a + ".item-lore");
            }


            ItemStack item = SkyBlock.getInstance().getUtils().createItem(id, data, name, lore, 1);
            i.setItem(slot - 1, item);
        }

        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory top = p.getOpenInventory().getTopInventory();
        Inventory bottom = p.getOpenInventory().getBottomInventory();

        String iName = e.getView().getTitle();
        if (iName == null) return;
        if (top != null && iName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("istop.name")))) {
            e.setCancelled(true);
        }
    }
}