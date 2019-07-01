package org.savage.skyblock.events;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.savage.skyblock.Main;
import org.savage.skyblock.island.Island;

public class PlayerEvents implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (Main.getInstance().getUtils().getSettingBool("use-chat-format")) {
            Player p = e.getPlayer();
            String oldFormat = e.getFormat();
            String newFormat = Main.getInstance().getUtils().getSettingString("chat-format");

            double level = 0;
            int top = 0;

            if (Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()) != null) {
                Island island = Main.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                level = island.getLevel();
                top = island.getTopPlace();
            }

            newFormat = newFormat.replace("{is-level}", level + "");
            newFormat = newFormat.replace("{is-top}", top + "");

            newFormat = Main.getInstance().getUtils().color(newFormat).replace("{old-format}", oldFormat);

            if (p.hasPermission("color")) {
                e.setMessage(Main.getInstance().getUtils().color(e.getMessage()));
            }
            e.setFormat(newFormat);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        Island island = Main.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island == null) return;

        Block block = e.getBlockPlaced();
        String type = block.getType().name().toUpperCase();
        double value;

        if (block.getState() instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            type = creatureSpawner.getCreatureTypeName().toUpperCase();
            value = Main.getInstance().getIslandUtils().getBlockLevelWorth(type, true);
        } else {
            value = Main.getInstance().getIslandUtils().getBlockLevelWorth(type, false);
        }
        island.addLevel(value);
        island.addBlockCount(type);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        Player p = e.getPlayer();

        Island island = Main.getInstance().getIslandUtils().getIsland(p.getUniqueId());
        if (island == null) return;

        Block block = e.getBlock();
        String type = block.getType().name().toUpperCase();
        double value;

        if (block.getState() instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            type = creatureSpawner.getCreatureTypeName().toUpperCase();
            value = Main.getInstance().getIslandUtils().getBlockLevelWorth(type, true);
        } else {
            value = Main.getInstance().getIslandUtils().getBlockLevelWorth(type, false);
        }
        island.addLevel(-value);
        island.addBlockCount(type);
    }
}