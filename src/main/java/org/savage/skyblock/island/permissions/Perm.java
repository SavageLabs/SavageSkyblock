package org.savage.skyblock.island.permissions;

public enum Perm {

    BUILD,
    DESTROY,
    ACCESS_CONTAINERS,
    INTERACT,
    MOB_INTERACT,
    MOB_ATTACK,
    VEHICLE_USE,
    ITEM_PICKUP,
    ITEM_DROP,
    MANAGE_PERMISSIONS, // ( co owner, owner )
    PROMOTE_MEMBER, // member -> officer ( coowner )
    PROMOTE_OFFICER, // officer -> coOwner ( owner )
    DEMOTE_OFFICER, // officer -> member ( co owner and owners )
    DEMOTE_COOWNER, // coowner -> officer ( owner )
    KICK,
    INVITE,
    SET_WARP,
    DELETE_WARP,
    BANK_WITHDRAW,
    BANK_VIEW;

    public static Perm getPerm(String name){
        for (Perm perm : Perm.values()){
            if (perm.name().toUpperCase().equalsIgnoreCase(name)){
                return perm;
            }
        }
        return null;
    }
}