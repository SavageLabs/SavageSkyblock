package me.saber.skyblock.island;

import me.saber.skyblock.Main;
import me.saber.skyblock.Storage;
import me.saber.skyblock.island.events.IslandCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
        return island.getOwnerUUID().equals(uuid);
    }

    public boolean isMember(UUID uuid, Island island){
        return island.getMemberList().contains(uuid);
    }
    public boolean isOfficer(UUID uuid, Island island){
        return island.getOfficerList().contains(uuid);
    }

    public boolean inIsland(UUID uuid){
        for (Island island : Storage.islandList){
            if (island.getOwnerUUID().equals(uuid) || island.getMemberList().contains(uuid) || island.getOfficerList().contains(uuid)){
                return true;
            }
        }
        return false;
    }

    public double getBlockLevelWorth(String blockType, boolean isSpawner){
        if (isSpawner){
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("worth.spawners."+blockType);
        }else{
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("worth.blocks."+blockType);
        }
    }

    public void calculateIslandLevel(Island island){
        String ver = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.v", "");

        List<FakeChunk> fakeChunkList = island.getFakeChunks();

        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_8_r3.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_8_r3.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_8_r2.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_8_r2.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_9_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_9_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_9_r2.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_9_r2.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_10_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_10_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_11_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_11_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_12_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_12_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_13_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_13_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_13_r2.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_13_r2.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }
        if (ver.equalsIgnoreCase(Main.getInstance().getReflectionManager().nmsHandler_v1_14_r1.getVersion())) {
            island.setLevel(0);
            for (FakeChunk fakeChunk : fakeChunkList) {
                Main.getInstance().getReflectionManager().nmsHandler_v1_14_r1.calculate(island.getLocation().getWorld().getChunkAt(fakeChunk.getX(), fakeChunk.getZ()), island);
            }
        }

    }
}