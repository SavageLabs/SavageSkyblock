package org.savage.skyblock.island.rules;

public class Rules {

    private Rule rule;
    private boolean allow;

    public Rules(Rule rule, boolean allow){
        this.rule = rule;
        this.allow = allow;
    }

    public Rule getRule() {
        return rule;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }
}
