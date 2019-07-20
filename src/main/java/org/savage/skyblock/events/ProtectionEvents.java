package org.savage.skyblock.events;

import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.permissions.Perm;
import org.savage.skyblock.island.permissions.Role;
import org.savage.skyblock.island.rules.Rule;

public class ProtectionEvents implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void mobSpawning(EntitySpawnEvent e){
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getEntity().getLocation());
        if (island == null) return;
        if (island.hasRule(Rule.MOB_SPAWNING)){
            e.setCancelled(true);
        }
    }
    @EventHandler (priority = EventPriority.HIGHEST)
    public void mobSpawning(SpawnerSpawnEvent e){
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getEntity().getLocation());
        if (island == null) return;
        if (island.hasRule(Rule.MOB_SPAWNING)){
            e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void breakBlock(BlockBreakEvent e){
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getBlock().getLocation());
        if (island == null) return;
        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
        if (role == null) return;
        if (!island.hasPerm(role, Perm.DESTROY)){
            e.setCancelled(true);
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void placeBlock(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getBlock().getLocation());
        if (island == null) return;
        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
        if (role == null) return;
        if (!island.hasPerm(role, Perm.BUILD)){
            e.setCancelled(true);
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent e){
        Block block = e.getClickedBlock();
        if (block == null) return;
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(block.getLocation());
        if (island == null) return;
        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
        if (role == null) return;
            if (block instanceof Chest || block instanceof Furnace || block instanceof DoubleChest || block instanceof Dropper || block instanceof Hopper || block instanceof BrewingStand) {
                if (!island.hasPerm(role, Perm.ACCESS_CONTAINERS)) {
                    p.closeInventory(); // just in case.......... <===3 pointy wiener...
                    e.setCancelled(true);
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
                }
            } else {
                if (!island.hasPerm(role, Perm.INTERACT)) {
                    e.setCancelled(true);
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
                }
            }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractAtEntityEvent e){
        Entity entity = e.getRightClicked();
        if (entity == null) return;
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(entity.getLocation());
        if (island == null) return;
        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
        if (role == null) return;
        if (!island.hasPerm(role, Perm.INTERACT)){
            e.setCancelled(true);
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void interact(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (e.getEntity() == null) return;
            Entity entity = e.getEntity();
            Player p = (Player)e.getDamager();
            Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(entity.getLocation());
            if (island == null) return;
            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
            if (role == null) return;
            if (!island.hasPerm(role, Perm.MOB_ATTACK)) {
                e.setCancelled(true);
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void vehicle(VehicleEnterEvent e) {
        if (e.getEntered() instanceof Player){
            Player p = (Player)e.getEntered();
            Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getVehicle().getLocation());
            if (island == null) return;
            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
            if (role == null) return;
            if (!island.hasPerm(role, Perm.VEHICLE_USE)) {
                e.setCancelled(true);
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void pickup(InventoryPickupItemEvent e) {
        if (e.getInventory() instanceof PlayerInventory){
            Inventory i = e.getInventory();
            if (i.getHolder() != null && i.getHolder() instanceof Player){
                Player p = (Player)i.getHolder();
                Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getItem().getLocation());
                if (island == null) return;
                Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                if (role == null) return;
                if (!island.hasPerm(role, Perm.ITEM_PICKUP)) {
                    e.setCancelled(true);
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void drop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getItemDrop().getLocation());
        if (island == null) return;
        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
        if (role == null) return;
        if (!island.hasPerm(role, Perm.ITEM_DROP)) {
            e.setCancelled(true);
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYours"));
        }
    }
}