package org.savage.skyblock.island.permissions;

public class Perms {

    private Perm perm;
    private boolean allow;

    public Perms(Perm perm, boolean allow){
        this.perm = perm;
        this.allow = allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }

    public Perm getPerm() {
        return perm;
    }
}