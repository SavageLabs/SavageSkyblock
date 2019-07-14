package org.savage.skyblock.island.challenges;

import org.savage.skyblock.SkyBlock;

import java.util.List;

public class Challenge {

    private int id;
    private String name;
    private List<Requirement> requirements;
    private List<String> rewardCommands;

    public Challenge(int id, String name, List<Requirement> requirements, List<String> rewardCommands){
        this.id = id;
        this.name = name;
        this.requirements = requirements;
        this.rewardCommands = rewardCommands;

        SkyBlock.getInstance().getChallenges().challengeList.add(this);
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

    public List<String> getRewardCommands() {
        return rewardCommands;
    }

    public void setRewardCommands(List<String> rewardCommands) {
        this.rewardCommands = rewardCommands;
    }
}
