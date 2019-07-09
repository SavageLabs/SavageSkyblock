package org.savage.skyblock.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.savage.skyblock.API.IslandEnterEvent;
import org.savage.skyblock.API.IslandLeaveEvent;
import org.savage.skyblock.API.IslandTeleportEvent;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.MemoryPlayer;

public class PlayerEvents implements Listener {

    @EventHandler
    public void teleportEvent(PlayerTeleportEvent e){
        //check if the island they're teleport from is not null
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getFrom());
        if (island != null){
            //good we can now call our own event from this one
            Bukkit.getPluginManager().callEvent(new IslandTeleportEvent(island, e.getPlayer().getUniqueId(), e.getFrom(), e.getTo()));
        }
    }

    @EventHandler
    public void enterIsland(IslandEnterEvent e){
        Player p = e.getPlayer();
        Island island = e.getIsland();
        if (p != null && island != null){
            if (SkyBlock.getInstance().getUtils().getSettingBool("send-island-enter-message")){
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-enter").replace("%island%", island.getName()));
            }

            if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).equals(island)){
                //is their island, check for is fly permission
                if (p.hasPermission(SkyBlock.getInstance().getUtils().getSettingString("island-fly-permission"))) {
                    p.setAllowFlight(true);
                }
            }


            //send the worldBorder packet to the incoming player
            SkyBlock.getInstance().getReflectionManager().nmsHandler.sendBorder(p, island.getCenterX(), island.getCenterZ(), island.getProtectionRadius());
        }
    }

    @EventHandler
    public void leaveIsland(IslandLeaveEvent e){
        Player p = e.getPlayer();
        Island island = e.getIsland();
        if (p != null && island != null){
            if (SkyBlock.getInstance().getUtils().getSettingBool("send-island-leave-message")){
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-leave").replace("%island%", island.getName()));
            }
        }
        if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation()) != SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId())){
            //not their island
            if (p.hasPermission(SkyBlock.getInstance().getUtils().getSettingString("island-fly-permission"))) {
                p.setAllowFlight(false);
                if (p.isFlying()){
                    p.setFlying(false);
                }
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (SkyBlock.getInstance().getUtils().getSettingBool("use-chat-format")) {
            Player p = e.getPlayer();
            String oldFormat = e.getFormat();
            String newFormat = SkyBlock.getInstance().getUtils().getSettingString("chat-format");

            double level = 0;
            int top = 0;

            if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()) != null) {
                Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                level = island.getLevel();
                top = island.getTopPlace();
            }

            newFormat = newFormat.replace("{is-level}", level + "");
            newFormat = newFormat.replace("{is-top}", top + "");

            newFormat = SkyBlock.getInstance().getUtils().color(newFormat).replace("{old-format}", oldFormat);

            if (p.hasPermission("color")) {
                e.setMessage(SkyBlock.getInstance().getUtils().color(e.getMessage()));
            }
            e.setFormat(newFormat);
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent e){
        Player p = e.getPlayer();

        if (!SkyBlock.getInstance().getUtils().hasMemoryPlayer(p.getUniqueId())){
            MemoryPlayer memoryPlayer = new MemoryPlayer(p.getUniqueId());
        }

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island.equals(SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation()))){
            //logged into their island
            Bukkit.getPluginManager().callEvent(new IslandEnterEvent(p, island));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void placeBlockChecker(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island == null) return;


        Block block = e.getBlockPlaced();

        if (!p.getWorld().equals(Storage.getSkyBlockWorld())){
            return;
        }

        if (!p.isOp() && !island.isBlockInIsland(block.getX(), block.getZ())){
            e.setCancelled(true);
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("placeblockRestriction"));
            return;
        }

        String type = block.getType().name().toUpperCase();

        int defaultHopper = SkyBlock.getInstance().getUtils().getSettingInt("default-hopper-limit");
        int defaultSpawner = SkyBlock.getInstance().getUtils().getSettingInt("default-spawner-limit");

        //check perms
        int islandHoppers = island.getHopperCount() + 1;
        int islandSpawners = island.getSpawnerCount() + 1;

        if (!p.isOp()) {
            if (type.equalsIgnoreCase("HOPPER")) {
                if (islandHoppers > defaultHopper) {
                    if (SkyBlock.getInstance().getUtils().hasPermissionAtleast(island.getOwnerUUID(), "skyblock.block.anvil")) {
                        int val = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId()).getPermissionValue("skyblock.block.anvil");
                        if (val < islandHoppers) {
                            e.setCancelled(true);
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("hopper-limit"));
                            return;
                        }
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("hopper-limit"));
                        return;
                    }
                }
            }
            if (type.contains("SPAWNER")) {
                if (islandSpawners > defaultSpawner) {
                    if (SkyBlock.getInstance().getUtils().hasPermissionAtleast(island.getOwnerUUID(), "skyblock.block.spawner")) {
                        int val = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId()).getPermissionValue("skyblock.block.spawner");
                        if (val < islandSpawners) {
                            e.setCancelled(true);
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("spawner-limit"));
                            return;
                        }
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("spawner-limit"));
                        return;
                    }
                }
            }
        }

        double levelValue;
        double moneyValue;
        boolean spawner;

        if (block.getState() instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            type = creatureSpawner.getCreatureTypeName().toUpperCase();
            levelValue = SkyBlock.getInstance().getIslandUtils().getLevelWorth(type, true);
            moneyValue = SkyBlock.getInstance().getIslandUtils().getMoneyWorth(type, true);
            spawner = true;
        } else {
            levelValue = SkyBlock.getInstance().getIslandUtils().getLevelWorth(type, false);
            moneyValue = SkyBlock.getInstance().getIslandUtils().getMoneyWorth(type, false);
            spawner = false;
        }

        if (moneyValue > 0) {
            if (spawner) {
                island.addSpawnerWorth(moneyValue);
            } else {
                island.addBlockWorth(moneyValue);
            }
        }


        if (levelValue > 0) {
            island.addBlockCount(type, spawner, 1);
            island.addLevel(levelValue);
        } else if (block.getType().equals(Material.HOPPER) || block.getType().equals(Material.MOB_SPAWNER)) {
            island.addBlockCount(type, spawner, 1);
        }
    }


    @EventHandler
    public void playerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) || e.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)){
                if (SkyBlock.getInstance().getConfig().getBoolean("settings.disable-fallDamage")){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void breakBlock(BlockBreakEvent e) {
        if (!e.isCancelled()) {
            Player p = e.getPlayer();

            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
            if (island == null) return;

            Block block = e.getBlock();

            if (!p.getWorld().equals(Storage.getSkyBlockWorld())){
                return;
            }

            if (!p.isOp() && !island.isBlockInIsland(block.getX(), block.getZ())){
                e.setCancelled(true);
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("breakblockRestriction"));
                return;
            }

            String type = block.getType().name().toUpperCase();
            double levelValue;
            double moneyValue;
            boolean spawner;

            if (block.getState() instanceof CreatureSpawner) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                type = creatureSpawner.getCreatureTypeName().toUpperCase();
                levelValue = SkyBlock.getInstance().getIslandUtils().getLevelWorth(type, true);
                moneyValue = SkyBlock.getInstance().getIslandUtils().getMoneyWorth(type, true);
                spawner = true;
            } else {
                levelValue = SkyBlock.getInstance().getIslandUtils().getLevelWorth(type, false);
                moneyValue = SkyBlock.getInstance().getIslandUtils().getMoneyWorth(type, false);
                spawner = false;
            }

            if (moneyValue > 0) {
                if (spawner) {
                    island.addSpawnerWorth(-moneyValue);
                } else {
                    island.addBlockWorth(-moneyValue);
                }
            }

            if (levelValue > 0) {
                island.removeBlockCount(type, spawner, 1);
                island.addLevel(-levelValue);
            } else if (block.getType().equals(Material.HOPPER) || block.getType().equals(Material.MOB_SPAWNER)) {
                island.removeBlockCount(type, spawner, 1);
            }
        }
    }
}