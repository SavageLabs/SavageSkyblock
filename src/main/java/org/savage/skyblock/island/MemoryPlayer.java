package org.savage.skyblock.island;

import org.bukkit.entity.Player;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.challenges.Challenge;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryPlayer {

    //we cache our player shit here
    private UUID uuid;
    private Player player;
    private Island island;
    private boolean inspectMode = false;
    private HashMap<String, Integer> permissionMap = new HashMap<>();

    private List<Integer> completedChallenges;
    private List<Integer> startedChallenges;

    //TODO; make a map of blocks they have broken, and make that load/save from file

    private int resets;

    public MemoryPlayer(UUID uuid){
        this.uuid = uuid;
        Storage.memoryPlayerList.add(this);
    }

    public boolean hasPermission(String permissionBase){
        return getPermissionMap().get(permissionBase) != null;
    }

    public List<Integer> getCompletedChallenges() {
        return completedChallenges;
    }

    public List<Integer> getStartedChallenges() {
        return startedChallenges;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public Island getIsland() {
        return island;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isInspectMode() {
        return inspectMode;
    }

    public void setInspectMode(boolean inspectMode) {
        this.inspectMode = inspectMode;
    }

    public void removePermission(String permissionBase){
        if (hasPermission(permissionBase)){
            this.permissionMap.remove(permissionBase);
        }
    }

    public void addPermission(String permissionBase, int value){
        //permssionBase is the permission node without the value at the end
        //example: skyblock.block.anvil
        //example with value: skyblock.block.anvil.100
        if (!hasPermission(permissionBase)){
            this.permissionMap.put(permissionBase, value);
        }
    }

    public void updatePermission(String permissionBase, int newValue){
        if (hasPermission(permissionBase)) {
            this.permissionMap.remove(permissionBase);
            this.permissionMap.put(permissionBase, newValue);
        }
    }

    public int getPermissionValue(String permission){
        if (hasPermission(permission)){
            return getPermissionMap().get(permission);
        }
        return 0;
    }

    public int getResets() {
        return resets;
    }

    public void setResets(int resets) {
        this.resets = resets;
    }

    public HashMap<String, Integer> getPermissionMap() {
        return permissionMap;
    }

    public UUID getUuid() {
        return uuid;
    }
}
