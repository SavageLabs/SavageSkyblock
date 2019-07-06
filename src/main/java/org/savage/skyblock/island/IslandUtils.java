package org.savage.skyblock.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.savage.skyblock.API.IslandCreateEvent;
import org.savage.skyblock.API.IslandCreatedEvent;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;

import java.util.*;

public class IslandUtils {

    public Location getIslandSpawn() {
        String s = SkyBlock.getInstance().getConfig().getString("settings.island-spawn");
        return SkyBlock.getInstance().getUtils().deserializeLocation(s);
    }

    public void createIsland(Player p, String permission, String schematicName) {
        if (!inIsland(p.getUniqueId())) {
            if (p.hasPermission(permission)) {

                IslandCreateEvent createEvent = new IslandCreateEvent(p.getUniqueId());
                Bukkit.getPluginManager().callEvent(createEvent);

                if (!createEvent.isCancelled()) {
                    Location location = SkyBlock.getInstance().getUtils().generateIslandLocation(Storage.minLocation(), Storage.maxLocation());
                    Island island = new Island(schematicName, location.getX(), location.getY(), location.getZ(), p.getUniqueId(),
                            new ArrayList<>(), new ArrayList<>(), SkyBlock.getInstance().getUtils().getSettingInt("default-protection-radius"), p.getName(),
                            SkyBlock.getInstance().getUtils().getSettingInt("default-member-limit"));

                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("createIsland"));

                    Bukkit.getPluginManager().callEvent(new IslandCreatedEvent(p.getUniqueId(), island));

                }
            } else {
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIslandPermissionCreate"));
            }
        } else {
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("alreadyIsland"));
        }
    }

    public Island getIsland(UUID uuid) {
        for (Island island : Storage.islandList) {
            if (island.getOwnerUUID().equals(uuid) || island.getMemberList().contains(uuid) || island.getOfficerList().contains(uuid)) {
                return island;
            }
        }
        return null;
    }

    public Island getIslandFromLocation(Location location) {
        for (Island island : Storage.islandList) {
            Location islandLocation = island.getLocation();
            int protectionRange = island.getProtectionRadius();

            try {
                if (location.distanceSquared(islandLocation) <= protectionRange) {
                    return island;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }


    public boolean isOwner(UUID uuid, Island island) {
        return island.getOwnerUUID().equals(uuid);
    }

    public boolean isMember(UUID uuid, Island island) {
        return island.getMemberList().contains(uuid);
    }

    public boolean isOfficer(UUID uuid, Island island) {
        return island.getOfficerList().contains(uuid);
    }

    public boolean inIsland(UUID uuid) {
        for (Island island : Storage.islandList) {
            if (island.getOwnerUUID().equals(uuid) || island.getMemberList().contains(uuid) || island.getOfficerList().contains(uuid)) {
                return true;
            }
        }
        return false;
    }

    public double getLevelWorth(String blockType, boolean isSpawner) {
        if (isSpawner) {
            return SkyBlock.getInstance().getFileManager().worth.getFileConfig().getDouble("level-worth.spawners." + blockType);
        } else {
            return SkyBlock.getInstance().getFileManager().worth.getFileConfig().getDouble("level-worth.blocks." + blockType);
        }
    }

    public double getMoneyWorth(String blockType, boolean isSpawner) {
        if (isSpawner) {
            return SkyBlock.getInstance().getFileManager().worth.getFileConfig().getDouble("money-worth.spawners." + blockType);
        } else {
            return SkyBlock.getInstance().getFileManager().worth.getFileConfig().getDouble("money-worth.blocks." + blockType);
        }
    }

    public boolean hasWorth(String blockType, boolean isSpawner){
        double level = getLevelWorth(blockType, isSpawner);
        double money = getMoneyWorth(blockType, isSpawner);
        return level > 0 || money > 0;
    }

    public Island getIslandFromPlacement(int place) {
        for (Island island : Storage.islandList) {
            if (island.getTopPlace() == place) {
                return island;
            }
        }
        return null;
    }

    public void calculateIslandTop() {

        List<Double> levels = new ArrayList<>();

        for (Island island : Storage.islandList) {

            island.setLevel(0);
            island.setBlockWorth(0);
            island.setSpawnerWorth(0);

            for (Map.Entry<FakeItem, Integer> entry : island.getBlocks().entrySet()) {
                FakeItem fakeItem = entry.getKey();
                int amount = entry.getValue();
                if (amount == 0) continue;
                double val;

               // System.out.print("\n "+fakeItem.getType()+","+amount+"\n");

                if (fakeItem.isSpawner()) {
                    val = getMoneyWorth(fakeItem.getType(), true) * amount;
                    island.addSpawnerWorth(val);
                    val = getLevelWorth(fakeItem.getType(), true) * amount;
                    island.addLevel(val);
                } else {
                    val = getMoneyWorth(fakeItem.getType(), false) * amount;
                    island.addBlockWorth(val);
                    val = getLevelWorth(fakeItem.getType(), false) * amount;
                    island.addLevel(val);
                }
            }

            double level = island.getLevel();
            levels.add(level);
        }

        levels.sort(Comparator.reverseOrder());

        int current = 1;
        for (double d : levels) {
            for (Island island : Storage.islandList) {
                if (d == island.getLevel()) {
                    // map.put(current, island);
                    island.setTopPlace(current);
                    current++;
                }
            }
        }
        levels.clear();
    }

    public void calculateIslandLevel(Island island) {
        List<FakeChunk> fakeChunkList = island.getFakeChunks();

        island.clearBlockCount();

        for (FakeChunk chunk : fakeChunkList) {
            SkyBlock.getInstance().getReflectionManager().nmsHandler.calculate(island.getLocation().getWorld().getChunkAt(chunk.getX(), chunk.getZ()), island);
        }
        fakeChunkList.clear();

    }
}