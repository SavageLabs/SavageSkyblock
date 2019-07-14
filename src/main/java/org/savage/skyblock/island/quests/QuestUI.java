package org.savage.skyblock.island.quests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.MemoryPlayer;

import java.util.List;

public class QuestUI implements Listener {


    @EventHandler
    public void menuClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();
        Inventory bottom = p.getOpenInventory().getBottomInventory();

        String name = e.getView().getTitle();

        if (top != null){
            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.name")))){
                e.setCancelled(true);

                ItemStack click = e.getCurrentItem();
                if (click == null || !click.hasItemMeta() || !click.getItemMeta().hasDisplayName()) return;

                String clickName = click.getItemMeta().getDisplayName();

                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.daily.item-name")))){
                    //open the daily quest menu
                    openDailyUI(p);
                }
            }
        }
    }

    public static void openDailyUI(Player p){

    }

    public static void openQuestMenuUI(Player p){
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
        if (memoryPlayer == null) return;

        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getInt("questMenuUI.rows")*9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.name")));

        //display the quests that the player CAN DO...

        FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();

        int dailySlot = f.getInt("questMenuUI.daily.slot");
        String itemID = f.getString("questMenuUI.daily.item-id");
        int itemData = f.getInt("questMenuUI.daily.item-data");
        String itemName = f.getString("questMenuUI.daily.item-name");
        List<String> itemLore = f.getStringList("questMenuUI.daily.item-lore");

        ItemStack dailyItem = SkyBlock.getInstance().getUtils().createItem(itemID, itemData, itemName, itemLore, 1);

        i.setItem(dailySlot - 1, dailyItem);

        p.openInventory(i);

    }
}
