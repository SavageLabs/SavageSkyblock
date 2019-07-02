package org.savage.skyblock.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.savage.skyblock.Main;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.events.IslandCreateEvent;
import org.savage.skyblock.island.events.IslandCreatedEvent;

import java.util.*;

public class IslandUtils {

    public Location getIslandSpawn() {
        String s = Main.getInstance().getConfig().getString("settings.island-spawn");
        return Main.getInstance().getUtils().deserializeLocation(s);
    }

    public void createIsland(Player p, String permission, String schematicName) {
        if (!inIsland(p.getUniqueId())) {
            if (p.hasPermission(permission)) {

                IslandCreateEvent createEvent = new IslandCreateEvent(p.getUniqueId());
                Bukkit.getPluginManager().callEvent(createEvent);

                if (!createEvent.isCancelled()) {
                    Location location = Main.getInstance().getUtils().generateIslandLocation(Storage.minLocation(), Storage.maxLocation());
                    Island island = new Island(schematicName, location.getX(), location.getY(), location.getZ(), p.getUniqueId(), new ArrayList<>(), new ArrayList<>(), Main.getInstance().getUtils().getSettingInt("protectionRadius"), p.getName());

                    p.sendMessage(Main.getInstance().getUtils().getMessage("createIsland"));

                    Bukkit.getPluginManager().callEvent(new IslandCreatedEvent(p.getUniqueId(), island));

                }
            } else {
                p.sendMessage(Main.getInstance().getUtils().getMessage("noIslandPermissionCreate"));
            }
        } else {
            p.sendMessage(Main.getInstance().getUtils().getMessage("alreadyIsland"));
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
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("level-worth.spawners." + blockType);
        } else {
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("level-worth.blocks." + blockType);
        }
    }

    public double getMoneyWorth(String blockType, boolean isSpawner) {
        if (isSpawner) {
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("money-worth.spawners." + blockType);
        } else {
            return Main.getInstance().getFileManager().levelWorth.getFileConfig().getDouble("money-worth.blocks." + blockType);
        }
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
        //Storage.islandTopMap.clear();

        //HashMap<Integer, Island> map = new HashMap<>();

        List<Double> levels = new ArrayList<>();

        for (Island island : Storage.islandList) {
            double level = island.getLevel();
            levels.add(level);
        }

        Collections.sort(levels);
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
        //  Storage.islandTopMap = map;
    }

    public void calculateIslandLevel(Island island) {
        List<FakeChunk> fakeChunkList = island.getFakeChunks();

        island.clearBlockCount();
        island.setLevel(0);
        island.setWorth(0);

        for (FakeChunk chunk : fakeChunkList) {
            Main.getInstance().getReflectionManager().nmsHandler.calculate(island.getLocation().getWorld().getChunkAt(chunk.getX(), chunk.getZ()), island);
        }
        fakeChunkList.clear();

        //do the money worth now
        for (Map.Entry<FakeItem, Integer> entry : island.getBlocks().entrySet()) {
            FakeItem fakeItem = entry.getKey();
            int amount = entry.getValue();
            if (amount == 0) continue;
            double val;
            if (fakeItem.isSpawner()) {
                val = getMoneyWorth(fakeItem.getType(), true);
                island.addSpawnerWorth(val);
            } else {
                val = getMoneyWorth(fakeItem.getType(), false);
                island.addBlockWorth(val);
            }

            //add to the island's worth
            island.addWorth(val);

        }
    }
}