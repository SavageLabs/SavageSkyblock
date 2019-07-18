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
import org.savage.skyblock.island.upgrades.UpgradesUI;

import java.util.List;

public class QuestUI implements Listener {


    @EventHandler
    public void menuClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();

        String name = e.getView().getTitle();

        if (top != null) {
            FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();
            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.name")))) {
                e.setCancelled(true);

                ItemStack click = e.getCurrentItem();
                if (click == null || !click.hasItemMeta() || !click.getItemMeta().hasDisplayName()) return;

                String clickName = click.getItemMeta().getDisplayName();

                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questMenuUI.forever.item-name")))) {
                    openQuestUI(p, Quest.QuestType.FOREVER);
                }
                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questMenuUI.daily.item-name")))) {
                    openQuestUI(p, Quest.QuestType.DAILY);
                }
                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questMenuUI.weekly.item-name")))) {
                    openQuestUI(p, Quest.QuestType.WEEKLY);
                }
                if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("questMenuUI.monthly.item-name")))) {
                    openQuestUI(p, Quest.QuestType.MONTHLY);
                }
            }

            String forever = SkyBlock.getInstance().getUtils().color(f.getString("questUI.forever.name"));
            String daily = SkyBlock.getInstance().getUtils().color(f.getString("questUI.daily.name"));
            String weekly = SkyBlock.getInstance().getUtils().color(f.getString("questUI.weekly.name"));
            String monthly = SkyBlock.getInstance().getUtils().color(f.getString("questUI.monthly.name"));

            if (name.equalsIgnoreCase(forever) || name.equalsIgnoreCase(daily) || name.equalsIgnoreCase(weekly) || name.equalsIgnoreCase(monthly)) {
                e.setCancelled(true);

                Quest.QuestType questType = null;

                if (name.equalsIgnoreCase(forever))
                    questType = Quest.QuestType.FOREVER;
                if (name.equalsIgnoreCase(daily))
                    questType = Quest.QuestType.DAILY;
                if (name.equalsIgnoreCase(weekly))
                    questType = Quest.QuestType.WEEKLY;
                if (name.equalsIgnoreCase(monthly))
                    questType = Quest.QuestType.MONTHLY;


                if (questType == null) return;

                ItemStack click = e.getCurrentItem();
                if (click == null || !click.hasItemMeta() || !click.getItemMeta().hasDisplayName()) return;
                MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());

                for (Quest quest : SkyBlock.getInstance().getQuests().getQuestsForPlayer(p.getUniqueId(), questType)) {
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
                        } else {
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-quest-not-complete"));
                        }
                    }
                }
            }
        }
    }

    public static void openQuestUI(Player p, Quest.QuestType questType) {
        String id = questType.name().toLowerCase();
        List<Quest> dailyQuests = SkyBlock.getInstance().getQuests().getQuestsForPlayer(p.getUniqueId(), questType);
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();
        Inventory i = Bukkit.createInventory(null, f.getInt("questUI." + id + ".rows") * 9, SkyBlock.getInstance().getUtils().color(f.getString("questUI." + id + ".name")));
        for (String s : SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questUI."+id+".faded-slots").split(",")) {
            int slot = Integer.parseInt(s);
            i.setItem(slot - 1, UpgradesUI.createFadedItem());
        }
        for (Quest quest : dailyQuests) {
            ItemStack questItem = quest.getQuestItem(p);
            i.addItem(questItem);
        }

        p.openInventory(i);

    }

    public static void openQuestMenuUI(Player p) {
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
        if (memoryPlayer == null) return;

        FileConfiguration f = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig();

        if (!f.getBoolean("enable-quests")){
            return;
        }

        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getInt("questMenuUI.rows") * 9, SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.name")));


        for (Quest.QuestType questType : Quest.QuestType.values()) {
            String id = questType.name().toLowerCase();
            int dailySlot = f.getInt("questMenuUI." + id + ".slot");
            String itemID = f.getString("questMenuUI." + id + ".item-id");
            int itemData = f.getInt("questMenuUI." + id + ".item-data");
            String itemName = f.getString("questMenuUI." + id + ".item-name");
            List<String> itemLore = f.getStringList("questMenuUI." + id + ".item-lore");

            ItemStack dailyItem = SkyBlock.getInstance().getUtils().createItem(itemID, itemData, itemName, itemLore, 1);

            i.setItem(dailySlot - 1, dailyItem);

            p.openInventory(i);
        }

        for (String s : SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getString("questMenuUI.faded-slots").split(",")) {
            int slot = Integer.parseInt(s);
            i.setItem(slot - 1, UpgradesUI.createFadedItem());
        }
    }
}