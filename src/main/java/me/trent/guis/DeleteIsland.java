package me.trent.guis;

import me.trent.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeleteIsland implements Listener {

    public static void openDelete(Player p){
        Inventory i = Bukkit.createInventory(null, Main.getInstance().getConfig().getInt("delete-island.rows") * 9, Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("delete-island.name")));

        i.setItem(Main.getInstance().getConfig().getInt("delete-island.confirm.slot") -1 , Main.getInstance().getUtils().createItem(Main.getInstance().getConfig().getString("delete-island.confirm.item-id"), 0, Main.getInstance().getConfig().getString("delete-island.confirm.item-name"), Main.getInstance().getConfig().getStringList("delete-island.confirm.item-lore"), 1));
        i.setItem(Main.getInstance().getConfig().getInt("delete-island.deny.slot") -1 , Main.getInstance().getUtils().createItem(Main.getInstance().getConfig().getString("delete-island.deny.item-id"), 0, Main.getInstance().getConfig().getString("delete-island.deny.item-name"), Main.getInstance().getConfig().getStringList("delete-island.deny.item-lore"), 1));
        p.openInventory(i);
    }

    @EventHandler
    public void click(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        if (e.getClickedInventory() != null){
            Inventory i = e.getClickedInventory();
            if (i.getName().equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("delete-island.name")))){
                e.setCancelled(true);
                if (e.getCurrentItem() != null){
                    ItemStack item = e.getCurrentItem();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("delete-island.confirm.item-name")))){
                            if (Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).isDeleting()){
                                Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).delete(); // delete island
                                p.closeInventory();
                            }else{
                                p.closeInventory();
                            }
                        }
                        if (name.equalsIgnoreCase(Main.getInstance().getUtils().color(Main.getInstance().getConfig().getString("delete-island.deny.item-name")))){
                            if (Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).isDeleting()){
                                //cancel deleting
                                Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setDeleting(false);
                                p.sendMessage(Main.getInstance().getUtils().getMessage("cancelDeleting"));
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