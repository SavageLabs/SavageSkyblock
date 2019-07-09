package org.savage.skyblock;

import com.bgsoftware.wildstacker.api.WildStacker;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PluginHook {

    public static boolean isEnabled(String pluginName){
        return Bukkit.getPluginManager().getPlugin(pluginName).isEnabled();
    }


    private static WildStacker getWildStackerAPI(){
        return WildStackerAPI.getWildStacker();
    }


    public static int getSpawnerCount(Location location){
        if (isEnabled("WildStacker")) {
           if (getWildStackerAPI().getSystemManager().getStackedSpawner(location) != null){
                return getWildStackerAPI().getSystemManager().getStackedSpawner(location).getStackAmount();
            }
        }
        return 1;
    }
}
