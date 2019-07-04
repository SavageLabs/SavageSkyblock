package org.savage.skyblock;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
import org.savage.skyblock.nms.ReflectionManager;
import org.savage.skyblock.worldedit.WorldEditPersistence;

import java.util.ArrayList;
import java.util.List;

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

        saveDefaultConfig();

        getFileManager().setup();

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

        getUtils().loadIslands();

        setupEconomy();

        WorldEditPersistence.worldEditVersion = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();

        getReflectionManager().setup();

        startTopTimer();

    }

    public void onDisable(){
        getUtils().saveIslands();
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

    public void startTopTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (String s : getUtils().getMessageList("starting-calculations")) {
                    Bukkit.broadcastMessage(s);
                }

                for (Island island : Storage.islandList) {
                    getIslandUtils().calculateIslandLevel(island);
                }
                getIslandUtils().calculateIslandTop();

                for (String s : getUtils().getMessageList("completed-calculations")) {
                    Bukkit.broadcastMessage(s);
                }
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.island-level-calculation-time") * 20);
    }
}