package me.saber.skyblock.worldguard;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WorldGuardPersistence {

    public static WorldEdit getWorldEdit(){
        return WorldEdit.getInstance();
    }
    public static WorldGuardPlugin getWorldGuard(){
        return WorldGuardPlugin.inst();
    }

    public static String worldEditVersion;

    public static boolean isOldWorldEdit(){
        return worldEditVersion.contains("6.");
    }
    public static boolean isNewWorldEdit(){
        return worldEditVersion.contains("7.");
    }


}
