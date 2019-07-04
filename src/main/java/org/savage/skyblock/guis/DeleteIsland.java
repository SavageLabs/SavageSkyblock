package org.savage.skyblock.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;

public class DeleteIsland implements Listener {

    public static void openDelete(Player p){
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("delete-island.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.name")));

        i.setItem(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("delete-island.confirm.slot") - 1, SkyBlock.getInstance().getUtils().createItem(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.confirm.item-id"), 0, SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.confirm.item-name"), SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getStringList("delete-island.confirm.item-lore"), 1));
        i.setItem(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getInt("delete-island.deny.slot") - 1, SkyBlock.getInstance().getUtils().createItem(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.deny.item-id"), 0, SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.deny.item-name"), SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getStringList("delete-island.deny.item-lore"), 1));
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        if (e.getClickedInventory() != null){
            Inventory i = e.getClickedInventory();
            if (i.getName().equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.name")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null){
                    ItemStack item = e.getCurrentItem();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.confirm.item-name")))) {
                            if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).isDeleting()) {
                                SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).delete(); // delete island
                                p.closeInventory();
                            }else{
                                p.closeInventory();
                            }
                        }
                        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().guiFile.getFileConfig().getString("delete-island.deny.item-name")))) {
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