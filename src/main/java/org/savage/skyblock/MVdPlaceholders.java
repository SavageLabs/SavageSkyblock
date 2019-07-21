package org.savage.skyblock;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import org.savage.skyblock.island.MemoryPlayer;

import java.util.UUID;

public class MVdPlaceholders {

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
