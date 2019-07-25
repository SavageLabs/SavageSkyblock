package org.savage.skyblock;

import com.sun.jna.Memory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.commands.AdminCommands;
import org.savage.skyblock.commands.IslandCommands;
import org.savage.skyblock.events.*;
import org.savage.skyblock.filemanager.FileManager;
import org.savage.skyblock.generators.WorldGenerator;
import org.savage.skyblock.guis.*;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.IslandUtils;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.permissions.PermissionUI;
import org.savage.skyblock.island.quests.Quest;
import org.savage.skyblock.island.quests.QuestUI;
import org.savage.skyblock.island.quests.Quests;
import org.savage.skyblock.island.quests.Requirement;
import org.savage.skyblock.island.rules.RuleUI;
import org.savage.skyblock.island.scoreboards.IslandBoard;
import org.savage.skyblock.island.upgrades.Upgrade;
import org.savage.skyblock.island.upgrades.UpgradesUI;
import org.savage.skyblock.island.warps.WarpUI;
import org.savage.skyblock.nms.ReflectionManager;
import org.savage.skyblock.worldedit.WorldEditPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SkyBlock extends JavaPlugin {

    private static SkyBlock instance;
    private WorldGenerator worldGenerator;
    private Utils utils;
    private FileManager fileManager;
    private IslandUtils islandUtils;
    private ReflectionManager reflectionManager;
    private Quests quests;
    private IslandBoard islandBoard;

    public List<Player> safePlayers = new ArrayList<>();

    public static Economy getEcon() {
        return econ;
    }

    private static Economy econ = null;

    public static SkyBlock getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public IslandUtils getIslandUtils() {
        return islandUtils;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public Utils getUtils() {
        return utils;
    }

    public Quests getQuests() {
        return quests;
    }

    public ReflectionManager getReflectionManager() {
        return reflectionManager;
    }

    public IslandBoard getIslandBoard() {
        return islandBoard;
    }

    public void onEnable(){
        instance = this;
        worldGenerator = new WorldGenerator();
        utils = new Utils();
        fileManager = new FileManager();
        islandUtils = new IslandUtils();
        reflectionManager = new ReflectionManager();
        quests = new Quests();
        islandBoard = new IslandBoard();

        MultiMaterials.setupMultiversionMaterials();

        if (!setupEconomy()){
            Bukkit.getLogger().log(Level.SEVERE,  "          --- SavageSkyBlock ---");
            Bukkit.getLogger().log(Level.SEVERE, "Error: Could not find Vault...");
            Bukkit.getLogger().log(Level.SEVERE, "Disabling SavageSkyBlock");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        getCommand("is").setExecutor(new IslandCommands());
        getCommand("isa").setExecutor(new AdminCommands());

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new ProtectionEvents(), this);
        pm.registerEvents(new IslandEvents(), this);
        pm.registerEvents(new Panel(), this);
        pm.registerEvents(new Islands(), this);
        pm.registerEvents(new DeleteIsland(), this);
        pm.registerEvents(new Biomes(), this);
        pm.registerEvents(new PermissionUI(), this);
        pm.registerEvents(new RuleUI(), this);
        pm.registerEvents(new ISTop(), this);
        pm.registerEvents(new PlayerEvents(), this);
        pm.registerEvents(new UpgradesUI(), this);
        pm.registerEvents(new UpgradeEvents(), this);
        pm.registerEvents(new WarpUI(), this);
        pm.registerEvents(new QuestUI(), this);

        if (PluginHook.isEnabled("Votifier")){
            pm.registerEvents(new VoteEvents(), this);
        }
        if (PluginHook.isEnabled("WildStacker")){
            pm.registerEvents(new WildStackerEvents(), this);
        }

        WorldEditPersistence.worldEditVersion = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();

        getReflectionManager().setup(); // load NMS
        getFileManager().setup(); // load flat files
        getUtils().loadIslands(); // load Islands

        getQuests().loadQuests(); // load Quests

        islandBoard.updateBoardTimer();

        if (PluginHook.isEnabled("PlaceholderAPI")){
            new PAPIExpansion(this).register();
        }
        if (PluginHook.isEnabled("MVdWPlaceholderAPI")){
            MVdPlaceholders.registerMVdPlaceholders();
        }

        startTopTimer();
        startCalculationTimer();

        startCacheTimer();

        for (Player p : Bukkit.getOnlinePlayers()){
            if (!SkyBlock.getInstance().getUtils().hasMemoryPlayer(p.getUniqueId())){
                SkyBlock.getInstance().getUtils().loadPlayer(p.getUniqueId());
            }
        }

        int saveInterval = getConfig().getInt("settings.data-save-interval") * 20 * 60;

        new BukkitRunnable(){
            boolean debug = getConfig().getBoolean("settings.debug");
            @Override
            public void run() {
                CompletableFuture.runAsync(() ->{
                    getUtils().saveIslands();
                    getUtils().savePlayers();
                    if (debug){
                        System.out.print("\n\n SavageSkyBlock: Saved all Data Async. \n\n");
                    }
                });
            }
        }.runTaskTimer(this, 0, saveInterval);

    }

    public void onDisable(){
        getUtils().saveIslands();
        getUtils().savePlayers();

        Bukkit.getScheduler().cancelTasks(this);
    }

    public void startCacheTimer(){
        new BukkitRunnable(){
            @Override
            public void run() {

                for (Island island : Storage.islandList) {
                    if (island == null) continue;
                    for (Upgrade upgrade : island.getUpgrade_tier().keySet()) {
                        if (upgrade == null) continue;

                        int val = Upgrade.Upgrades.getTierValue(upgrade, island.getUpgradeTier(upgrade), null);

                        if (upgrade.getId() == 1){
                            //protection radius
                            if (val > island.getProtectionRadius()){
                                //update
                                island.setProtectionRadius(val);
                            }
                        }
                        if (upgrade.getId() == 2){
                            //member limit
                            if (val > island.getMemberLimit()){
                                //update
                                island.setMemberLimit(val);
                            }
                        }
                        if (upgrade.getId() == 4){
                            //hopper limit
                            if (val > island.getHopperLimit()){
                                //update
                                island.setHopperLimit(val);
                            }
                        }
                        if (upgrade.getId() == 5){
                            //spawner limit
                            if (val > island.getSpawnerLimit()){
                                //update
                                island.setSpawnerLimit(val);
                            }
                        }
                    }
                }

                //cache some stuff...
                //cache permissions we want
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (getUtils().hasMemoryPlayer(p.getUniqueId())) {
                        MemoryPlayer memoryPlayer = getUtils().getMemoryPlayer(p.getUniqueId());

                        memoryPlayer.setPlayTime(Math.addExact(memoryPlayer.getPlayTime(), 1));
                        //check for requirements here...

                        List<Quest> quests = getQuests().questList;
                        quests.removeAll(memoryPlayer.getCompletedQuests());

                        for (Quest quest : quests){
                            boolean has = true;
                            for (Requirement requirement : quest.getRequirements()){
                                if (!getQuests().hasRequirement(p.getUniqueId(), requirement)){
                                    has = false;
                                }
                            }
                            if (has){
                                //they have completed them all for this quest... send them the message...
                                //check if that specific quest is not in the map cached
                                if (Storage.completedQuestMessageQueue.get(p.getUniqueId()) == null || !Storage.completedQuestMessageQueue.get(p.getUniqueId()).contains(quest)){
                                    List<Quest> q = new ArrayList<>();
                                    if (Storage.completedQuestMessageQueue.get(p.getUniqueId()) != null){
                                        q = Storage.completedQuestMessageQueue.get(p.getUniqueId());
                                    }
                                    q.add(quest);
                                    Storage.completedQuestMessageQueue.remove(p.getUniqueId());
                                    Storage.completedQuestMessageQueue.put(p.getUniqueId(), q);
                                    //send the message

                                    p.sendMessage(getUtils().getMessage("island-quest-requirements-completed").replace("%quest%", getUtils().color(quest.getName())));

                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            // remove the quest from the cached message list, because we want to re-send the message the next interval...
                                            List<Quest> q = Storage.completedQuestMessageQueue.get(p.getUniqueId());
                                            q.remove(quest);
                                            Storage.completedQuestMessageQueue.remove(p.getUniqueId());
                                            Storage.completedQuestMessageQueue.put(p.getUniqueId(), q);
                                            //removed from the cache, so now the next interval we will send the message
                                        }
                                    }.runTaskLater(SkyBlock.getInstance(), getConfig().getInt("settings.quest-requirements-completed-messageInterval")* 20);
                                }
                            }
                        }


                        for (PermissionAttachmentInfo pInfo : p.getEffectivePermissions()) {
                            String perm = pInfo.getPermission();
                            for (String permBase : Storage.permissionToCache){
                                if (perm.toLowerCase().startsWith(permBase.toLowerCase())){
                                    //since the original permission starts with the perm we want to cache, that's our permBase
                                    //starts with it, we know we got a permission we want to cache...
                                    int value = getUtils().getIntegersFromString(perm);
                                    if (value > 0) {
                                        //value at the end of the permission is valid so we're goochi
                                        if (memoryPlayer.hasPermission(permBase)) {
                                            //we already have the permBase, check if the values have changed or not
                                            if (memoryPlayer.getPermissionValue(permBase) != value){
                                                // perm changes, update it
                                                memoryPlayer.updatePermission(permBase, value);
                                            }
                                        }else{
                                            //we don't have the permission cached, we want to cache it now...
                                            memoryPlayer.addPermission(permBase, value);
                                        }
                                    }
                                }
                            }
                        }
                        for (String permBase : memoryPlayer.getPermissionMap().keySet()){
                            int val = memoryPlayer.getPermissionValue(permBase);
                            String fullPerm = permBase+"."+val;
                            if (!p.hasPermission(fullPerm)){
                                //remove the permission node that was cached
                                memoryPlayer.removePermission(permBase);
                            }
                        }

                        //apply permission island stuff
                        Island island = getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            if (island.getOwnerUUID().equals(p.getUniqueId())) { // has to be owner to apply these
                                if (memoryPlayer.hasPermission("skyblock.members")) {
                                    int val = memoryPlayer.getPermissionValue("skyblock.members");
                                    if (val > island.getMemberLimit()) {
                                        //update
                                        island.setMemberLimit(val);
                                    }
                                }
                                if (memoryPlayer.hasPermission("skyblock.protection")) {
                                    int val = memoryPlayer.getPermissionValue("skyblock.protection");
                                    if (val > island.getProtectionRadius()) {
                                        //update
                                        island.setProtectionRadius(val);
                                    }
                                }
                                if (memoryPlayer.hasPermission("skyblock.block.hopper")) {
                                    int val = memoryPlayer.getPermissionValue("skyblock.block.hopper");
                                    if (val > island.getHopperLimit()) {
                                        //update
                                        island.setHopperLimit(val);
                                    }
                                }
                                if (memoryPlayer.hasPermission("skyblock.block.spawner")) {
                                    int val = memoryPlayer.getPermissionValue("skyblock.block.spawner");
                                    if (val > island.getSpawnerLimit()) {
                                        //update
                                        island.setSpawnerLimit(val);
                                    }
                                }
                            }
                            //send the worldBorder packet to the incoming player
                            Island is = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation());
                            if (is != null){
                                if (!MultiMaterials.mc114){
                                    SkyBlock.getInstance().getReflectionManager().nmsHandler.sendBorder(p, island.getCenterX(), island.getCenterZ(), island.getProtectionRadius());
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20L);
    }

    public void startTopTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getIslandUtils().calculateIslandTop();
            }
        }.runTaskTimerAsynchronously(this, 0, 20L);
    }

    public void startCalculationTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean broadcast = SkyBlock.getInstance().getConfig().getBoolean("settings.broadcast-isTop-calculations");

                if (broadcast) {
                    for (String s : getUtils().getMessageList("starting-calculations")) {
                        Bukkit.broadcastMessage(s);
                    }
                }

                for (Island island : Storage.islandList) {
                    getIslandUtils().calculateIslandLevel(island);
                }

                if (broadcast) {
                    for (String s : getUtils().getMessageList("completed-calculations")) {
                        Bukkit.broadcastMessage(s);
                    }
                }
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.island-level-calculation-time") * 20);
    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}