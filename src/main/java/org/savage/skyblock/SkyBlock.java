package org.savage.skyblock;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.commands.AdminCommands;
import org.savage.skyblock.commands.IslandCommands;
import org.savage.skyblock.events.IslandEvents;
import org.savage.skyblock.events.PlayerEvents;
import org.savage.skyblock.filemanager.FileManager;
import org.savage.skyblock.generators.WorldGenerator;
import org.savage.skyblock.guis.*;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.IslandUtils;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.upgrades.Upgrade;
import org.savage.skyblock.island.upgrades.UpgradesUI;
import org.savage.skyblock.nms.ReflectionManager;
import org.savage.skyblock.worldedit.WorldEditPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SkyBlock extends JavaPlugin {

    private static SkyBlock instance;
    private WorldGenerator worldGenerator;
    private Utils utils;
    private FileManager fileManager;
    private IslandUtils islandUtils;
    private ReflectionManager reflectionManager;

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

    public ReflectionManager getReflectionManager() {
        return reflectionManager;
    }

    public void onEnable(){
        instance = this;
        worldGenerator = new WorldGenerator();
        utils = new Utils();
        fileManager = new FileManager();
        islandUtils = new IslandUtils();
        reflectionManager = new ReflectionManager();

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
        pm.registerEvents(new IslandEvents(), this);
        pm.registerEvents(new Panel(), this);
        pm.registerEvents(new Islands(), this);
        pm.registerEvents(new DeleteIsland(), this);
        pm.registerEvents(new Biomes(), this);
        pm.registerEvents(new Protection(), this);
        pm.registerEvents(new ISTop(), this);
        pm.registerEvents(new PlayerEvents(), this);
        pm.registerEvents(new UpgradesUI(), this);

        WorldEditPersistence.worldEditVersion = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();

        getReflectionManager().setup(); // load NMS
        getFileManager().setup(); // load flat files
        getUtils().loadIslands(); // load Islands

        startTopTimer();
        startCalculationTimer();

        startCacheTimer();

    }

    public void onDisable(){
        getUtils().saveIslands();

        Bukkit.getScheduler().cancelTasks(this);
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
                    }
                }

                //cache some stuff...
                //cache permissions we want
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (getUtils().hasMemoryPlayer(p.getUniqueId())) {
                        MemoryPlayer memoryPlayer = getUtils().getMemoryPlayer(p.getUniqueId());
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
}