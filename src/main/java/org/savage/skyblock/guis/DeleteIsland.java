package org.savage.skyblock.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;

import static org.savage.skyblock.island.upgrades.UpgradesUI.createFadedItem;

public class DeleteIsland implements Listener {

    public static void openDelete(Player p){
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("delete-island.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.name")));

        for (String s : SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        i.setItem(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("delete-island.confirm.slot") - 1, SkyBlock.getInstance().getUtils().createItem(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.confirm.item-id"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("delete-island.confirm.item-data"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.confirm.item-name"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("delete-island.confirm.item-lore"), 1));
        i.setItem(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("delete-island.deny.slot") - 1, SkyBlock.getInstance().getUtils().createItem(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.deny.item-id"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getInt("delete-island.deny.item-data"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.deny.item-name"), SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getStringList("delete-island.deny.item-lore"), 1));
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        if (e.getClickedInventory() != null){
            String iName = e.getView().getTitle();
            if (iName == null) return;
            if (iName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null){
                    ItemStack item = e.getCurrentItem();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.confirm.item-name")))) {
                            if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).isDeleting()) {
                                SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).delete(); // delete island
                                p.closeInventory();
                            }else{
                                p.closeInventory();
                            }
                        }
                        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("delete-island.deny.item-name")))) {
                            if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).isDeleting()) {
                                //cancel deleting
                                SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setDeleting(false);
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("cancelDeleting"));
                                p.closeInventory();
                            }else{
                                p.closeInventory();
                            }
                        }
                    }
                }
            }
        }
    }
}