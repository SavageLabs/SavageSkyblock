package org.savage.skyblock.events;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.savage.skyblock.API.IslandEnterEvent;
import org.savage.skyblock.API.IslandLeaveEvent;
import org.savage.skyblock.API.IslandTeleportEvent;
import org.savage.skyblock.Materials;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.MemoryPlayer;

import java.math.BigDecimal;
import java.util.List;

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
    public void inspect(PlayerInteractEvent e){
        Player p = e.getPlayer();
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
        if (memoryPlayer == null) return;

        if (memoryPlayer.isInspectMode()){
            e.setCancelled(true);
            //send the data
            List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
            if (info.size() == 0) {
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-inspect-noResults"));
                return;
            }
            CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
            List<String> msg = SkyBlock.getInstance().getUtils().getMessageList("island-inspect-results");
            for (int i = 0; i < info.size(); i++) {
                CoreProtectAPI.ParseResult row = coAPI.parseResult(info.get(i));
                for (String s : msg){
                    p.sendMessage(s
                            .replace("%time%", SkyBlock.getInstance().getUtils().convertTime(row.getTime()))
                            .replace("%action%", row.getActionString())
                            .replace("%player%", row.getPlayer())
                            .replace("%block%", row.getType().toString().toLowerCase()));
                }
            }
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

       SkyBlock.getInstance().getUtils().loadPlayer(p.getUniqueId()); // load the memory player from file

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        Island island1 = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation());
        if (island != null && island1 != null){
            //logged into their island
            Bukkit.getPluginManager().callEvent(new IslandEnterEvent(p, island));
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

    @EventHandler (priority = EventPriority.HIGHEST)
    public void placeBlockChecker(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island == null) return;
        MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());


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

        int hopperLimit = island.getHopperLimit();
        int spawnerLimit = island.getSpawnerLimit();

        //check perms
        int islandHoppers = island.getHopperCount() + 1;
        int islandSpawners = island.getSpawnerCount() + 1;

        if (!p.isOp()){
            //player isn't op so we can restrict their placing
            if (type.equalsIgnoreCase("HOPPER")) {
                if (islandHoppers > hopperLimit){
                    e.setCancelled(true);
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("hopper-limit"));
                    return;
                }
            }
            if (type.contains("SPAWNER")) {
                if (islandSpawners > spawnerLimit){
                    e.setCancelled(true);
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("spawner-limit"));
                    return;
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

        //log the material's main name for now...
        //todo; allow for data type values... for 1.8 - 1.12

        memoryPlayer.addBlocksPlaced(block.getType(), 0, spawner);
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
            MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
            memoryPlayer.addBlocksBroken(block.getType(), 0, spawner);
        }
    }

    @EventHandler
    public void balanceChangeEvent(UserBalanceUpdateEvent e){
        Player p = e.getPlayer();
        double oldBalance = e.getOldBalance().doubleValue();
        double newBalance = e.getNewBalance().doubleValue();

        if (oldBalance > newBalance){
            //means they lost money, so they're spending the money...
            MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
            double difference = oldBalance - newBalance;
            memoryPlayer.setMoneySpent(memoryPlayer.getMoneySpent() + difference);
        }
    }

    @EventHandler
    public void killPlayer(PlayerDeathEvent e){
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        MemoryPlayer player = SkyBlock.getInstance().getUtils().getMemoryPlayer(killer.getUniqueId());
        MemoryPlayer victim = SkyBlock.getInstance().getUtils().getMemoryPlayer(killer.getUniqueId());
        player.setPlayerKills(player.getPlayerKills() + 1);
        victim.setDeaths(victim.getDeaths() + 1);
    }

    @EventHandler
    public void killMonster(EntityDeathEvent e){
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity == null) return;
        if (livingEntity instanceof ArmorStand) return;

        if (livingEntity.getKiller() == null) return;
        if (livingEntity.getKiller() instanceof Player){
            Player p = livingEntity.getKiller();
            MemoryPlayer player = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
            player.setMobKills(player.getMobKills() + 1);
        }
    }
}