package org.savage.skyblock.island.quests;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.savage.skyblock.Materials;
import org.savage.skyblock.SkyBlock;

import java.util.ArrayList;
import java.util.List;

public class Quests {

    public List<Quest> questList = new ArrayList<>();
    public int currentChallengeID = 1;

    public List<Quest> getQuests(Enum type){
        List<Quest> quests = new ArrayList<>();
        for (Quest quest : questList){
            if (quest.getQuestType().equals(type)){
                quests.add(quest);
            }
        }
        return quests;
    }

    public void loadQuests(){
        //load all of the quests into memory

        //load from each file...

        for (Enum type : Quest.QuestType.values()){
            FileConfiguration f = getConfig(type);
            if (f == null) continue;

            if (!type.equals(Quest.QuestType.DAILY)){
                continue;
            }

            int max = f.getConfigurationSection("quests").getKeys(false).size();

            for (int a = 1; a<= max; a++){
                int questID = a;
                String questName = f.getString("quests."+a+".questName");

                //build the requirements now...

                Quest quest = new Quest(type, questID, questName); // created the quest instance

                int requirementMax = f.getConfigurationSection("quests."+a+".requirements").getKeys(false).size();

                for (int b = 1; b<= requirementMax; b++){
                    //we're in the requirement list now...
                    Enum requirementType = RequirementType.valueOf(f.getString("quests."+a+".requirements."+b+".type").toUpperCase());

                    if (requirementType == null) continue;

                    int targetAmount = f.getInt("quests."+a+".requirements."+b+".target-amount");

                    Requirement requirement = new Requirement(quest, requirementType, targetAmount);

                    //now we check for block type, or any other arguments we need

                    if (requirementType.equals(RequirementType.BREAK_BLOCK) || requirementType.equals(RequirementType.PLACE_BLOCK)){
                        //either break, or place block, check for block-type data
                        String materialName = f.getString("quests."+a+".requirements."+b+".block-type");
                        int blockData = f.getInt("quests."+a+".requirements."+b+".block-data");
                        Material material;
                        if (Material.getMaterial(materialName) != null){
                            //got a valid material id
                            material = Material.getMaterial(materialName);
                        }else{
                            material = Materials.requestXMaterial(materialName, (byte)blockData).parseMaterial();
                        }
                        requirement.setRequirementMaterial(material);
                    }

                    //add requirement to the quest now...
                    quest.addRequirement(requirement);

                }
                SkyBlock.getInstance().getUtils().log("Loaded Quest: "+questName+", "+questID+" : Requirements = "+quest.getRequirements().toString());
            }
        }
    }

    public FileConfiguration getConfig(Enum questType){
        if (questType.equals(Quest.QuestType.DAILY)) {
            return SkyBlock.getInstance().getFileManager().getDailyQuestFile().getFileConfig();
        }
        if (questType.equals(Quest.QuestType.FOREVER)) {
            return SkyBlock.getInstance().getFileManager().getForeverQuestFile().getFileConfig();
        }
        if (questType.equals(Quest.QuestType.WEEKLY)) {
            return SkyBlock.getInstance().getFileManager().getWeeklyQuestFile().getFileConfig();
        }
        if (questType.equals(Quest.QuestType.MONTHLY)) {
            return SkyBlock.getInstance().getFileManager().getMonthlyQuestFile().getFileConfig();
        }
        return null;
    }

    public void createQuest(Enum questType, int id, String name){
        new Quest(questType, id, name);
    }


    public Quest getQuest(int id, Enum questType){
        for (Quest quest : getQuests(questType)){
            if (quest.getId() == id){
                return quest;
            }
        }
        return null;
    }

    public Quest getQuest(String name, Enum questType){
        for (Quest quest : getQuests(questType)){
            if (quest.getName().equalsIgnoreCase(name)){
                return quest;
            }
        }
        return null;
    }
}
