package org.savage.skyblock.island.warps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.upgrades.UpgradesUI;

import java.util.ArrayList;
import java.util.List;

public class WarpUI implements Listener {

    @EventHandler
    public void clickWarp(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();

        String name = e.getView().getTitle();

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island == null) return;
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();
        if (top != null && name != null){
            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("island-warps.name").replace("%warps%", island.getIslandWarps().size()+"")))){
                e.setCancelled(true);

                ItemStack clicked = e.getCurrentItem();
                if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

                String itemName = clicked.getItemMeta().getDisplayName();

                //check for a warp's name

                for (IslandWarp islandWarp : island.getIslandWarps()){
                    if (ChatColor.stripColor(itemName).contains(islandWarp.getName())){
                        //teleport to this warp

                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-teleported").replace("%name%", islandWarp.getName()));
                        p.closeInventory();
                        p.teleport(islandWarp.getLocation());
                    }
                }
            }
        }
    }


    public static void openWarpUI(Player p){
        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();
        Inventory i = Bukkit.createInventory(null, f.getInt("island-warps.rows")*9, SkyBlock.getInstance().getUtils().color(f.getString("island-warps.name").replace("%warps%", island.getIslandWarps().size()+"")));

        for (String faded : f.getString("island-warps.faded-slots").split(",")){
            int slot = Integer.parseInt(faded);
            i.setItem(slot - 1, UpgradesUI.createFadedItem());
        }

        //add in the warp items now
        for (IslandWarp islandWarp : island.getIslandWarps()){
            String name = islandWarp.getName();
            int x = islandWarp.getLocation().getBlockX();
            int y = islandWarp.getLocation().getBlockY();
            int z = islandWarp.getLocation().getBlockZ();

            String itemID = f.getString("island-warps.warp-item.item-id");
            int data = f.getInt("island-warps.warp-item.item-data");
            String itemName = f.getString("island-warps.warp-item.name").replace("%name%", name);
            List<String> itemLore = new ArrayList<>();

            for (String s : f.getStringList("island-warps.warp-item.lore")){
                s = s.replace("%name%", name);
                s = s.replace("%x%", x+"");
                s = s.replace("%y%", y+"");
                s = s.replace("%z%", z+"");

                itemLore.add(s);
            }

            ItemStack item = SkyBlock.getInstance().getUtils().createItem(itemID, data, itemName, itemLore, 1);

            i.addItem(item);
        }
        p.openInventory(i);
    }
}