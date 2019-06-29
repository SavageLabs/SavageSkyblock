package me.saber.skyblock.WG;

import com.sk89q.worldedit.WorldEdit;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;

public class WG {

    public static WorldEdit getWorldEdit(){
        return WorldEdit.getInstance();
    }
    public static WorldGuardPlugin getWorldGuard(){
        return WorldGuardPlugin.inst();
    }

    public static boolean isOldWorldEdit(){
        String version = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
        if (version.contains("6.")){
            return true;
        }
        return false;
    }
    public static boolean isNewWorldEdit(){
        String version = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
        if (version.contains("7.")){
            return true;
        }
        return false;
    }


}
