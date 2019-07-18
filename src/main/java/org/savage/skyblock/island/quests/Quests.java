package org.savage.skyblock.island.quests;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.savage.skyblock.Materials;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.Utils;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.upgrades.Upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quests {

    public List<Quest> questList = new ArrayList<>();

    public List<Quest> getQuests(Enum type){
        List<Quest> quests = new ArrayList<>();
        for (Quest quest : questList){
            if (quest.getQuestType().equals(type)){
                quests.add(quest);
            }
        }
        return quests;
    }

    public List<Quest> getQuestsForPlayer(UUID uuid, Enum type){
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);
        if (memoryPlayer == null) return new ArrayList<>();

        List<Quest> questList = getQuests(type);
        //check if the player hasn't already gotten these
        questList.removeAll(memoryPlayer.getCompletedQuests()); // remove all of the player's completed quests from the list we want...
        return questList;
    }

    public boolean hasRequirement(UUID uuid, Requirement requirement){
        //check the individual quest's requirement if the player has completed it or not
        Enum type = requirement.getRequirementType();
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);

        double targetValue = requirement.getTargetAmount();
        double currentValue = 0;

        if (type.equals(RequirementType.PLACE_BLOCK)){
            Material material = requirement.getRequirementMaterial();
            int materialData = requirement.getRequirementMaterialData();
            boolean isSpawner = requirement.isRequirementMaterialSpawner();
            int placed = memoryPlayer.getBlocksPlaced(material, materialData, isSpawner);

            currentValue = placed;
        }
        if (type.equals(RequirementType.BREAK_BLOCK)){
            Material material = requirement.getRequirementMaterial();
            int materialData = requirement.getRequirementMaterialData();
            boolean isSpawner = requirement.isRequirementMaterialSpawner();
            int broken = memoryPlayer.getBlocksBroken(material, materialData, isSpawner);

            currentValue = broken;
        }
        if (type.equals(RequirementType.PLAY_TIME)) {
            int currentSeconds = memoryPlayer.getPlayTime();
            Enum timeType = requirement.getTimeType();
            double currentTime = SkyBlock.getInstance().getUtils().convertSeconds(currentSeconds, timeType);

            currentValue = currentTime;
        }

        if (type.equals(RequirementType.SPEND_CURRENCY)){
            double spent = memoryPlayer.getMoneySpent();
            currentValue = spent;

        }
        if (type.equals(RequirementType.HAVE_CURRENCY)){
            double has = SkyBlock.getInstance().getUtils().getBalance(uuid);

            currentValue = has;
        }

        if (type.equals(RequirementType.HAVE_QUEST)){
            int requiredQuestID = (int)requirement.getTargetAmount();
            int yes = 0;
            if (memoryPlayer.hasCompletedQuest(requirement.getQuest().getQuestType(), requiredQuestID)){
                yes = requiredQuestID;
            }
            currentValue = yes;
        }

        if (type.equals(RequirementType.VOTE_AMOUNT)) {
            int voteAmount = 0; //todo do this
            currentValue = voteAmount;
        }
        if (type.equals(RequirementType.KILL_MOB)) {
            int mobKills = memoryPlayer.getMobKills();
            currentValue = mobKills;
        }
        if (type.equals(RequirementType.KILL_PLAYER)) {
            int playerKills = memoryPlayer.getPlayerKills();
            currentValue = playerKills;
        }
        if (type.equals(RequirementType.DIE)) {
            int deaths = memoryPlayer.getDeaths();
            currentValue = deaths;
        }
        if (type.equals(RequirementType.IS_TOP)) {
            int isTop = Storage.currentTop+1;// auto set to the last is top there is...
            if (memoryPlayer.getIsland() != null){
                isTop = memoryPlayer.getIsland().getTopPlace();
            }
            currentValue = isTop;
        }
        if (type.equals(RequirementType.IS_UPGRADE_TIER)){
            Upgrade upgradeType = requirement.getUpgradeType();
            int upgradeTier = 0;
            if (memoryPlayer.getIsland() != null){
                upgradeTier = memoryPlayer.getIsland().getUpgradeTier(upgradeType);
            }
            currentValue = upgradeTier;
        }


        return currentValue >= targetValue;
    }

    public List<String> convertQuestPlaceholders(List<String> lore, Player p, Enum questType, int questID){
        List<String> newLore = new ArrayList<>();
        List<Requirement> requirements = getQuest(questID, questType).getRequirements();
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());

        int progressBarAmount = SkyBlock.getInstance().getFileManager().getQuestFile().getFileConfig().getInt("placeholder.progressBar-count");

        for (String s : SkyBlock.getInstance().getUtils().color(lore)){
            if (s.contains("%") && (s.contains("_progressBar%") || s.contains("_progressNumFirst%") || s.contains("_progressNumLast%") || s.contains("_progressPercent%"))){

                Integer[] digits = SkyBlock.getInstance().getUtils().getDigits(SkyBlock.getInstance().getUtils().getIntegersFromString(ChatColor.stripColor(s)));

                int requirementID = digits[0];
               // Bukkit.broadcastMessage("ID: "+requirementID);
                if (requirements.size() >= requirementID){
                    String bar = "";
                    double numFirst = 0;
                    double numLast = 0;
                    double percent;

                    Requirement requirement = requirements.get(requirementID - 1); // since we're in an index, minus 1.
                    Enum type = requirement.getRequirementType();
                    //we can use the type to check what kind of player requirements we need
                    if (type.equals(RequirementType.PLACE_BLOCK)){
                        Material material = requirement.getRequirementMaterial();
                        int materialData = requirement.getRequirementMaterialData();
                        boolean isSpawner = requirement.isRequirementMaterialSpawner();
                        int placed = memoryPlayer.getBlocksPlaced(material, materialData, isSpawner);

                        numFirst = placed;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.BREAK_BLOCK)){
                        Material material = requirement.getRequirementMaterial();
                        int materialData = requirement.getRequirementMaterialData();
                        boolean isSpawner = requirement.isRequirementMaterialSpawner();
                        int broken = memoryPlayer.getBlocksBroken(material, materialData, isSpawner);

                        numFirst = broken;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.PLAY_TIME)) {
                        int currentSeconds = memoryPlayer.getPlayTime();
                        Enum timeType = requirement.getTimeType();

                        double currentTime = SkyBlock.getInstance().getUtils().convertSeconds(currentSeconds, timeType);
                        double timeNeeded = requirement.getTargetAmount();

                        numFirst = currentTime;
                        numLast = timeNeeded;
                    }

                    if (type.equals(RequirementType.SPEND_CURRENCY)){
                        double spent = memoryPlayer.getMoneySpent();

                        numFirst = spent;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.HAVE_CURRENCY)){
                        double has = SkyBlock.getInstance().getUtils().getBalance(p.getUniqueId());

                        numFirst = has;
                        numLast = requirement.getTargetAmount();
                    }

                    if (type.equals(RequirementType.HAVE_QUEST)){
                        int requiredQuestID = (int)requirement.getTargetAmount();
                        int yes = 0;
                        if (memoryPlayer.hasCompletedQuest(questType, requiredQuestID)){
                            yes = 1;
                        }
                        numFirst = yes;
                        numLast = 1;
                    }

                    if (type.equals(RequirementType.VOTE_AMOUNT)) {
                        int voteAmount = 0; //todo do this
                        numFirst = voteAmount;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.KILL_MOB)) {
                        int mobKills = memoryPlayer.getMobKills();
                        numFirst = mobKills;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.KILL_PLAYER)) {
                        int playerKills = memoryPlayer.getPlayerKills();
                        numFirst = playerKills;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.DIE)) {
                        int deaths = memoryPlayer.getDeaths();
                        numFirst = deaths;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.IS_TOP)) {
                        int isTop = Storage.currentTop+1;// auto set to the last is top there is...
                        if (memoryPlayer.getIsland() != null){
                            isTop = memoryPlayer.getIsland().getTopPlace();
                        }
                        numFirst = isTop;
                        numLast = requirement.getTargetAmount();
                    }
                    if (type.equals(RequirementType.IS_UPGRADE_TIER)){
                        Upgrade upgradeType = requirement.getUpgradeType();
                        int upgradeTier = 0;
                        if (memoryPlayer.getIsland() != null){
                            upgradeTier = memoryPlayer.getIsland().getUpgradeTier(upgradeType);
                        }
                        numFirst = upgradeTier;
                        numLast = requirement.getTargetAmount();
                    }

                    bar = SkyBlock.getInstance().getUtils().createBar(progressBarAmount, (int)numFirst, (int)numLast);

                    percent = (numFirst / numLast) * 100;
                    if (percent > 100){
                        percent = 100;
                    }

                    s = s.replace("%"+requirementID+"_progressBar%", bar);
                    s = s.replace("%"+requirementID+"_progressNumFirst%", SkyBlock.getInstance().getUtils().formatNumber(numFirst+""));
                    s = s.replace("%"+requirementID+"_progressNumLast%", SkyBlock.getInstance().getUtils().formatNumber(numLast+""));
                    s = s.replace("%"+requirementID+"_progressPercent%", SkyBlock.getInstance().getUtils().formatNumber(percent+""));

                }
            }
            newLore.add(SkyBlock.getInstance().getUtils().color(s));
        }
        return newLore;
    }


    public void loadQuests(){
        //load all of the quests into memory
        boolean debug = SkyBlock.getInstance().getUtils().getSettingBool("debug");
        for (Enum type : Quest.QuestType.values()){
            FileConfiguration f = getConfig(type);
            if (f == null) continue;

            int max = f.getConfigurationSection("quests").getKeys(false).size();

            for (int a = 1; a<= max; a++) {
                int questID = a;

                if (f.getBoolean("quests." + a + ".enabled")) {
                    String questName = f.getString("quests." + a + ".questName");

                    //build the requirements now...

                    Quest quest = new Quest(type, questID, questName); // created the quest instance

                    int requirementMax = f.getConfigurationSection("quests." + a + ".requirements").getKeys(false).size();

                    for (int b = 1; b <= requirementMax; b++) {
                        //we're in the requirement list now...
                        Enum requirementType = RequirementType.valueOf(f.getString("quests." + a + ".requirements." + b + ".type").toUpperCase());

                        if (requirementType == null) continue;

                        int targetAmount = f.getInt("quests." + a + ".requirements." + b + ".target-amount");

                        Requirement requirement = new Requirement(quest, requirementType, targetAmount);

                        //now we check for block type, or any other arguments we need

                        if (requirementType.equals(RequirementType.BREAK_BLOCK) || requirementType.equals(RequirementType.PLACE_BLOCK)) {
                            //either break, or place block, check for block-type data
                            String materialName = f.getString("quests." + a + ".requirements." + b + ".block-type");
                            boolean isSpawner = f.getBoolean("quests." + a + ".requirements." + b + ".isSpawner");
                            int blockData = f.getInt("quests." + a + ".requirements." + b + ".block-data");
                            Material material;
                            if (Material.getMaterial(materialName) != null) {
                                //got a valid material id
                                material = Material.getMaterial(materialName);
                            } else {
                                material = Materials.requestXMaterial(materialName, (byte) blockData).parseMaterial();
                            }
                            requirement.setRequirementMaterial(material);
                            requirement.setRequirementMaterialSpawner(isSpawner);
                        }

                        if (requirementType.equals(RequirementType.PLAY_TIME)) {
                            //get the time-type
                            Enum timeType = Utils.time.valueOf(f.getString("quests." + a + ".requirements." + b + ".time-type"));
                            requirement.setTimeType(timeType);
                        }

                        if (requirementType.equals(RequirementType.IS_UPGRADE_TIER)) {
                            //get the upgrade type we want to it
                            Upgrade upgradeType = Upgrade.Upgrades.getUpgrade(f.getString("quests." + a + ".requirements." + b + ".upgrade"));
                            requirement.setUpgradeType(upgradeType);
                        }
                        //add requirement to the quest now...
                        quest.addRequirement(requirement);
                    }
                    if (debug){
                        SkyBlock.getInstance().getUtils().log("Loaded Quest: " + questName + ", " + questID + " : Requirements = " + quest.getRequirements().toString());
                    }
                }
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
