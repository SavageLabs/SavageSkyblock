package org.savage.skyblock.island.quests;


import org.bukkit.Material;

public class Requirement {

    //requirement for a Quest
    private Quest quest;

    private Enum requirementType;

    private Material requirementMaterial;

    private int targetAmount; // target Amount can be a lot of things.... Blocks Broken, Blocks Placed, QuestID, Money Spent, Current Balance, Monster Kills, Player Kills
    // can also be Spawners In Island, Hoppers In Island, ETC anykind of BLOCk!!!

    public Requirement (Quest quest, Enum requirementType, int targetAmount){
        this.quest = quest;
        this.requirementType = requirementType;
        this.targetAmount = targetAmount;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(int targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Quest getQuest() {
        return quest;
    }

    public Material getRequirementMaterial() {
        return requirementMaterial;
    }

    public Enum getRequirementType() {
        return requirementType;
    }

    public void setRequirementMaterial(Material requirementMaterial) {
        this.requirementMaterial = requirementMaterial;
    }

    public void setRequirementType(Enum requirementType) {
        this.requirementType = requirementType;
    }
}