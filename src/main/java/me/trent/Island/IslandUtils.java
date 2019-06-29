package me.trent.Island;

import me.trent.Island.events.IslandCreateEvent;
import me.trent.Main;
import me.trent.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class IslandUtils {

    public Location getIslandSpawn(){
        String s = Main.getInstance().getConfig().getString("settings.island-spawn");
        return Main.getInstance().getUtils().deserializeLocation(s);
    }

    public void createIsland(Player p, String permission, String schematicName){
        if (!inIsland(p.getUniqueId())){
            if (p.hasPermission(permission)) {

                IslandCreateEvent createEvent = new IslandCreateEvent(p.getUniqueId());
                Bukkit.getPluginManager().callEvent(createEvent);

                if (!createEvent.isCancelled()) {
                    Location location = Main.getInstance().getUtils().generateIslandLocation(Storage.minLocation(), Storage.maxLocation());
                    Island island = new Island(schematicName, location.getX(), location.getY(), location.getZ(), p.getUniqueId(), new ArrayList<>(), new ArrayList<>(), Main.getInstance().getUtils().getSettingInt("protectionRadius"));

                    p.sendMessage(Main.getInstance().getUtils().getMessage("createIsland"));
                }
            }else{
                p.sendMessage(Main.getInstance().getUtils().getMessage("noIslandPermissionCreate"));
            }
        }else{
            p.sendMessage(Main.getInstance().getUtils().getMessage("alreadyIsland"));
        }
    }

    public Island getIsland(UUID uuid){
        for (Island island : Storage.islandList){
            if (island.getOwnerUUID().equals(uuid) || island.getMemberList().contains(uuid) || island.getOfficerList().contains(uuid)){
                return island;
            }
        }
        return null;
    }

    public Island getIslandFromLocation(Location location){
        for (Island island : Storage.islandList){
            Location islandLocation = island.getLocation();
            int protectionRange = island.getProtectionRadius();

            try {
                if (location.distanceSquared(islandLocation) <= protectionRange) {
                    return island;
                }
            }catch(IllegalArgumentException e){ }
        }
        return null;
    }


    public boolean isOwner(UUID uuid, Island island){
        if (island.getOwnerUUID().equals(uuid)){
            return true;
        }
        return false;
    }

    public boolean isMember(UUID uuid, Island island){
        if (island.getMemberList().contains(uuid)){
            return true;
        }
        return false;
    }
    public boolean isOfficer(UUID uuid, Island island){
        if (island.getOfficerList().contains(uuid)){
            return true;
        }
        return false;
    }

    public boolean inIsland(UUID uuid){
        for (Island island : Storage.islandList){
            if (island.getOwnerUUID().equals(uuid) || island.getMemberList().contains(uuid) || island.getOfficerList().contains(uuid)){
                return true;
            }
        }
        return false;
    }


}
