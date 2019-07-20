package org.savage.skyblock.island.rules;

public enum Rule {

    ISLAND_LOCK,
    MOB_SPAWNING,
    PVP;


    public static Rule getRule(String name){
        for (Rule rule : Rule.values()){
            if (rule.name().toUpperCase().equalsIgnoreCase(name)){
                return rule;
            }
        }
        return null;
    }
}