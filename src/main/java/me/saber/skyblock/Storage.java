package me.saber.skyblock;

import me.saber.skyblock.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Storage {

    public static HashMap<Integer, Island> islandTopMap = new HashMap<>();

    public static List<Island> islandList = new ArrayList<>();
    private static World skyBlockWorld = Bukkit.getWorld("skyBlock");

    public static World getSkyBlockWorld() {
        return skyBlockWorld;
    }

    public static double defaultY = 100;

    public static Location minLocation(){
        return new Location(getSkyBlockWorld(), Main.getInstance().getUtils().getSettingInt("min-x"), defaultY, Main.getInstance().getUtils().getSettingInt("min-z"));
    }
    public static Location maxLocation(){
        return new Location(getSkyBlockWorld(), Main.getInstance().getUtils().getSettingInt("max-x"), defaultY, Main.getInstance().getUtils().getSettingInt("max-z"));
    }

}