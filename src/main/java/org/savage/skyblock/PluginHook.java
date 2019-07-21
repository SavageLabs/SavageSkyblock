package org.savage.skyblock;

import com.bgsoftware.wildstacker.api.WildStacker;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PluginHook {

    public static boolean isEnabled(String pluginName){
        if (Bukkit.getPluginManager().getPlugin(pluginName) != null){
            return Bukkit.getPluginManager().getPlugin(pluginName).isEnabled();
        }
        return false;
    }

    private static WildStacker getWildStacker(){
        return WildStackerAPI.getWildStacker();
    }

    public static int getSpawnerCount(Location location){
        if (isEnabled("WildStacker")) {
           if (getWildStacker().getSystemManager().getStackedSpawner(location) != null){
                return getWildStacker().getSystemManager().getStackedSpawner(location).getStackAmount();
            }
        }
        return 1;
    }
}