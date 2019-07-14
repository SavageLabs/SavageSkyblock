package org.savage.skyblock.island.challenges;

import java.util.ArrayList;
import java.util.List;

public class Challenges {

    public List<Challenge> challengeList = new ArrayList<>();
    public int currentChallengeID = 1;

    public void createChallenge(String name, List<Requirement> requirements, List<String> rewardCommands){
        new Challenge(currentChallengeID, name, requirements, rewardCommands);
        currentChallengeID = currentChallengeID + 1;
    }

    public Challenge getChallenge(int id){
        for (Challenge challenge : challengeList){
            if (challenge.getId() == id){
                return challenge;
            }
        }
        return null;
    }

    public Challenge getChallenge(String name){
        for (Challenge challenge : challengeList){
            if (challenge.getName().equalsIgnoreCase(name)){
                return challenge;
            }
        }
        return null;
    }
}
