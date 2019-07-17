package org.savage.skyblock.island.quests;

import com.sun.jna.Memory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.MemoryPlayer;

import java.util.List;

public class QuestUI implements Listener {


    @EventHandler
    public void menuClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();

        String name = e.getView().getTitle();

        if (top != null){
            FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();
            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.name")))){
                e.setCancelled(true);

                ItemStack click = e.getCurrentItem();
                if (click == null || !click.hasItemMeta() || !click.getItemMeta().hasDisplayName()) return;

                String clickName = click.getItemMeta().getDisplayName();

                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questMenuUI.daily.item-name")))){
                    //open the daily quest menu
                    openQuestUI(p, Quest.QuestType.DAILY);
                }
            }
            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questUI.daily.name")))) {
                e.setCancelled(true);
                ItemStack click = e.getCurrentItem();
                if (click == null || !click.hasItemMeta() || !click.getItemMeta().hasDisplayName()) return;
                MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());

                for (Quest quest : SkyBlock.getInstance().getQuests().getQuestsForPlayer(p.getUniqueId(), Quest.QuestType.DAILY)){
                    ItemStack questItem = quest.getQuestItem(p);
                    if (click.equals(questItem)) {
                        //complete the quest
                        if (Storage.completedQuestMessageQueue.get(p.getUniqueId()) != null && Storage.completedQuestMessageQueue.get(p.getUniqueId()).contains(quest)) {
                            p.closeInventory();
                            memoryPlayer.addCompletedQuest(quest);
                            for (String command : quest.getRewardCommands()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
                            }
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-quest-complete").replace("%quest%", SkyBlock.getInstance().getUtils().color(quest.getName())));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-quest-not-complete"));
                        }
                    }
                }
            }
        }
    }

    public static void openQuestUI(Player p, Enum questType){
        //open the player's current Daily Quests
        if (questType.equals(Quest.QuestType.DAILY)) {
            List<Quest> dailyQuests = SkyBlock.getInstance().getQuests().getQuestsForPlayer(p.getUniqueId(), Quest.QuestType.DAILY);
            FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();
            Inventory i = Bukkit.createInventory(null, f.getInt("questUI.daily.rows")*9, SkyBlock.getInstance().getUtils().color(f.getString("questUI.daily.name")));
            //loop through the quests and create itemstacks for them...
            for (Quest quest : dailyQuests) {
                ItemStack questItem = quest.getQuestItem(p);
                i.addItem(questItem);
            }

            p.openInventory(i);
        }

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
