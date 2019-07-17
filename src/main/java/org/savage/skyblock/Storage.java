package org.savage.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.quests.Quest;

import java.util.*;

public class Storage {

    public static List<String> permissionToCache = Arrays.asList("skyblock.block.hopper", "skyblock.block.spawner", "skyblock.members", "skyblock.protection");

    public static HashMap<UUID, List<Quest>> completedQuestMessageQueue = new HashMap<>();

    //public static HashMap<Integer, Island> islandTopMap = new HashMap<>();
    public static int currentTop = 1;

    public static List<Island> islandList = new ArrayList<>();
    public static List<MemoryPlayer> memoryPlayerList = new ArrayList<>();

    private static World skyBlockWorld = Bukkit.getWorld("skyBlock");

    public static World getSkyBlockWorld() {
        return skyBlockWorld;
    }

    public static double defaultY = 100;

    public static Location minLocation(){
        return new Location(getSkyBlockWorld(), SkyBlock.getInstance().getUtils().getSettingInt("min-x"), defaultY, SkyBlock.getInstance().getUtils().getSettingInt("min-z"));
    }
    public static Location maxLocation(){
        return new Location(getSkyBlockWorld(), SkyBlock.getInstance().getUtils().getSettingInt("max-x"), defaultY, SkyBlock.getInstance().getUtils().getSettingInt("max-z"));
    }

}