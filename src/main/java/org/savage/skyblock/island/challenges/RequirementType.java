package org.savage.skyblock.island.challenges;

public enum RequirementType {

    SPEND_CURRENCY(1, "spend_currency"),
    HAVE_CURRENCY(2, "have_currency"),

    BREAK_BLOCK(3, "break_block"),
    PLACE_BLOCK(4, "place_block");

    private int id;
    private String name;

    RequirementType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
