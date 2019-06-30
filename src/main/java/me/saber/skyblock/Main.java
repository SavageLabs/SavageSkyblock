package me.saber.skyblock;

import me.saber.skyblock.commands.IslandCommands;
import me.saber.skyblock.commands.SBPCommands;
import me.saber.skyblock.events.IslandEvents;
import me.saber.skyblock.filemanager.FileManager;
import me.saber.skyblock.generators.WorldGenerator;
import me.saber.skyblock.guis.*;
import me.saber.skyblock.island.Island;
import me.saber.skyblock.island.IslandUtils;
import me.saber.skyblock.nms.ReflectionManager;
import me.saber.skyblock.worldguard.WorldGuardPersistence;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static Main instance;
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

    public static Main getInstance() {
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
        getUtils().setup();

        getCommand("is").setExecutor(new IslandCommands());
        getCommand("sbp").setExecutor(new SBPCommands());

        Bukkit.getPluginManager().registerEvents(new IslandEvents(), this);
        Bukkit.getPluginManager().registerEvents(new Panel(), this);
        Bukkit.getPluginManager().registerEvents(new Islands(), this);
        Bukkit.getPluginManager().registerEvents(new DeleteIsland(), this);
        Bukkit.getPluginManager().registerEvents(new Biomes(), this);
        Bukkit.getPluginManager().registerEvents(new Protection(), this);

        getUtils().loadIslands();

        setupEconomy();

        WorldGuardPersistence.worldEditVersion = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();

        getReflectionManager().setup();


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Island island : Storage.islandList) {
                    getIslandUtils().calculateIslandLevel(island);
                }
                getIslandUtils().calculateIslandTop();
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.island-level-calculation-time"));
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
}