package org.savage.skyblock;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.bgsoftware.wildstacker.api.WildStacker;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.savage.skyblock.island.MemoryPlayer;

import java.util.UUID;

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

    public static void registerMVdPlaceholders(){
        //we want to register all of our placeholders here for PAPI, and MVdWPlaceholderAPI.
        boolean MVdW = PluginHook.isEnabled("MVdWPlaceholderAPI");

        String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");

        if (MVdW){
            PlaceholderAPI.registerPlaceholder(SkyBlock.getInstance(), "island", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    UUID uuid = e.getPlayer().getUniqueId();
                    MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);
                    if (memoryPlayer.getIsland() == null){
                        return none;
                    }
                    return memoryPlayer.getIsland().getName();
                }
            });
            PlaceholderAPI.registerPlaceholder(SkyBlock.getInstance(), "is-bank", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    UUID uuid = e.getPlayer().getUniqueId();
                    MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);
                    if (memoryPlayer.getIsland() == null){
                        return 0+"";
                    }
                    return SkyBlock.getInstance().getUtils().formatNumber(memoryPlayer.getIsland().getBankBalance()+"");
                }
            });
            PlaceholderAPI.registerPlaceholder(SkyBlock.getInstance(), "is-top", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    UUID uuid = e.getPlayer().getUniqueId();
                    MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);
                    if (memoryPlayer.getIsland() == null){
                        return none;
                    }
                    return memoryPlayer.getIsland().getTopPlace()+"";
                }
            });
            PlaceholderAPI.registerPlaceholder(SkyBlock.getInstance(), "is-worth", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    UUID uuid = e.getPlayer().getUniqueId();
                    MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(uuid);
                    if (memoryPlayer.getIsland() == null){
                        return 0+"";
                    }
                    return SkyBlock.getInstance().getUtils().formatNumber(memoryPlayer.getIsland().getWorth()+"");
                }
            });
        }
    }
}