package org.savage.skyblock.island.quests;

public enum RequirementType {

    SPEND_CURRENCY("spend_currency"),
    HAVE_CURRENCY("have_currency"),

    BREAK_BLOCK("break_block"),
    PLACE_BLOCK("place_block"),

    HAVE_QUEST("have_quest"),

    PLAY_TIME("play_time"),

    VOTE_AMOUNT("vote_amount"),

    KILL_MOB("kill_mob"),
    KILL_PLAYER("kill_player"),

    DIE("die"),

    IS_TOP("is_top"),

    IS_UPGRADE_TIER("is_upgrade_tier");


    private String name;

    RequirementType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}