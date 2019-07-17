package org.savage.skyblock.island.quests;


import org.bukkit.Material;
import org.savage.skyblock.island.upgrades.Upgrade;

public class Requirement {

    //requirement for a Quest
    private Quest quest;

    private Enum requirementType;
    private Enum timeType;

    private Material requirementMaterial;
    private int requirementMaterialData;
    private boolean requirementMaterialSpawner;

    private Upgrade upgradeType;

    private double targetAmount; // target Amount can be a lot of things.... Blocks Broken, Blocks Placed, QuestID, Money Spent, Current Balance, Monster Kills, Player Kills
    // can also be Spawners In Island, Hoppers In Island, ETC anykind of BLOCk!!!

    public Requirement (Quest quest, Enum requirementType, double targetAmount){
        this.quest = quest;
        this.requirementType = requirementType;
        this.targetAmount = targetAmount;
    }

    public Upgrade getUpgradeType() {
        return upgradeType;
    }

    public Enum getTimeType() {
        return timeType;
    }

    public void setTimeType(Enum timeType) {
        this.timeType = timeType;
    }

    public int getRequirementMaterialData() {
        return requirementMaterialData;
    }

    public boolean isRequirementMaterialSpawner() {
        return requirementMaterialSpawner;
    }

    public void setRequirementMaterialSpawner(boolean requirementMaterialSpawner) {
        this.requirementMaterialSpawner = requirementMaterialSpawner;
    }

    public void setRequirementMaterialData(int requirementMaterialData) {
        this.requirementMaterialData = requirementMaterialData;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
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