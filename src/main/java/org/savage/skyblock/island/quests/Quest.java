package org.savage.skyblock.island.quests;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.savage.skyblock.SkyBlock;

import java.util.ArrayList;
import java.util.List;

public class Quest {

    public enum QuestType{
        FOREVER,
        DAILY,
        WEEKLY,
        MONTHLY
    }

    private Enum questType;
    private int id;
    private String name;
    private List<Requirement> requirements = new ArrayList<>();

    public Quest(Enum questType, int id, String name){
        this.questType = questType;
        this.id = id;
        this.name = name;

        SkyBlock.getInstance().getQuests().questList.add(this);
    }

    public void addRequirement(Requirement requirement){
        this.requirements.add(requirement);
    }

    public Enum getQuestType() {
        return questType;
    }

    public void setQuestType(Enum questType) {
        this.questType = questType;
    }

    public List<String> getRewardCommands(){
        return SkyBlock.getInstance().getQuests().getConfig(getQuestType()).getStringList("quests."+getId()+".reward-commands");
    }

    public ItemStack getQuestItem(Player p){
        ItemStack item = null;
        String itemID = "";
        int itemData = 0;
        String itemName = "";
        List<String> itemLore = new ArrayList<>();
        FileConfiguration f = SkyBlock.getInstance().getQuests().getConfig(getQuestType());

        if (getQuestType().equals(QuestType.DAILY)){
            itemID = f.getString("quests."+getId()+".item-id");
            itemData = f.getInt("quests."+getId()+".item-data");
            itemName = f.getString("quests."+getId()+".item-name");
            itemLore = SkyBlock.getInstance().getQuests().convertQuestPlaceholders(f.getStringList("quests."+getId()+".item-lore"), p, QuestType.DAILY, getId());
        }
        if (getQuestType().equals(QuestType.FOREVER)){
            itemID = f.getString("quests."+getId()+".item-id");
            itemData = f.getInt("quests."+getId()+".item-data");
            itemName = f.getString("quests."+getId()+".item-name");
            itemLore = SkyBlock.getInstance().getQuests().convertQuestPlaceholders(f.getStringList("quests."+getId()+".item-lore"), p, QuestType.FOREVER, getId());
        }
        if (getQuestType().equals(QuestType.WEEKLY)){
            itemID = f.getString("quests."+getId()+".item-id");
            itemData = f.getInt("quests."+getId()+".item-data");
            itemName = f.getString("quests."+getId()+".item-name");
            itemLore = SkyBlock.getInstance().getQuests().convertQuestPlaceholders(f.getStringList("quests."+getId()+".item-lore"), p, QuestType.WEEKLY, getId());
        }
        if (getQuestType().equals(QuestType.MONTHLY)){
            itemID = f.getString("quests."+getId()+".item-id");
            itemData = f.getInt("quests."+getId()+".item-data");
            itemName = f.getString("quests."+getId()+".item-name");
            itemLore = SkyBlock.getInstance().getQuests().convertQuestPlaceholders(f.getStringList("quests."+getId()+".item-lore"), p, QuestType.MONTHLY, getId());
        }

        item = SkyBlock.getInstance().getUtils().createItem(itemID, itemData, itemName, itemLore, 1);
        return item;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
}