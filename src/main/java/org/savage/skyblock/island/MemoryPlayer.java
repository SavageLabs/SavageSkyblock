package org.savage.skyblock.island;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.quests.Quest;
import org.savage.skyblock.island.scoreboards.CScoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryPlayer {

    //we cache our player shit here
    private CScoreboard scoreboard;
    private UUID uuid;
    private Player player;
    private Island island;
    private boolean inspectMode = false;
    private HashMap<String, Integer> permissionMap = new HashMap<>();

    private List<Quest> completedQuests = new ArrayList<>();

    private HashMap<String, Integer> blocksPlaced = new HashMap<>();
    private HashMap<String, Integer> blocksBroke = new HashMap<>();

    private int playTime = 0; // PLAYTIME IS IN SECONDS
    private double moneySpent = 0;
    private int playerKills = 0;
    private int mobKills = 0;
    private int deaths = 0;
    private int votes = 0;

    private int resets;

    public MemoryPlayer(UUID uuid){
        this.uuid = uuid;
        Storage.memoryPlayerList.add(this);
    }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }

    public void setScoreboard(CScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public CScoreboard getScoreboard() {
        return scoreboard;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getMobKills() {
        return mobKills;
    }

    public void setMobKills(int mobKills) {
        this.mobKills = mobKills;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    public int getBlocksBroken(Material material, int data, boolean spawner){
        String materialString = material.name().toUpperCase()+"-"+data+"-"+spawner;
        if (getBlocksBroke().get(materialString) != null){
            return getBlocksBroke().get(materialString);
        }
        return 0;
    }
    public int getBlocksPlaced(Material material, int data, boolean spawner){
        String materialString = material.name().toUpperCase()+"-"+data+"-"+spawner;
        if (getBlocksPlaced().get(materialString) != null){
            return getBlocksPlaced().get(materialString);
        }
        return 0;
    }

    public void addBlocksBroken(Material material, int data, boolean spawner){
        String materialString = material.name().toUpperCase()+"-"+data+"-"+spawner;
        int current = getBlocksBroken(material, data, spawner);
        this.blocksBroke.remove(materialString);
        this.blocksBroke.put(materialString, current + 1);
    }

    public void addBlocksPlaced(Material material, int data, boolean spawner){
        String materialString = material.name().toUpperCase()+"-"+data+"-"+spawner;
        int current = getBlocksPlaced(material, data, spawner);
        this.blocksPlaced.remove(materialString);
        this.blocksPlaced.put(materialString, current + 1);
    }

    public boolean hasCompletedQuest(Enum questType, int questID){
        return getCompletedQuests().contains(SkyBlock.getInstance().getQuests().getQuest(questID, questType));
    }


    public double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public HashMap<String, Integer> getBlocksBroke() {
        return blocksBroke;
    }

    public HashMap<String, Integer> getBlocksPlaced() {
        return blocksPlaced;
    }

    public void addCompletedQuest(Quest quest){
        this.completedQuests.add(quest);
    }

    public void purgeQuests(){
        this.completedQuests.clear();
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setBlocksBroke(HashMap<String, Integer> blocksBroke) {
        this.blocksBroke = blocksBroke;
    }

    public void setBlocksPlaced(HashMap<String, Integer> blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }

    public void setCompletedQuests(List<Quest> completedQuests) {
        this.completedQuests = completedQuests;
    }

    public boolean hasPermission(String permissionBase){
        return getPermissionMap().get(permissionBase) != null;
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
