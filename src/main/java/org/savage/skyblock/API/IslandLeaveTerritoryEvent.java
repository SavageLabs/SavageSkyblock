package org.savage.skyblock.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

/**
 * @author Trent @ Aysteria Development
 * <p>
 * Event when a player leaves an Island's zone
 */

public class IslandLeaveTerritoryEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Island island;
    private Player player;

    public IslandLeaveTerritoryEvent(Player player, Island island) {
        this.player = player;
        this.island = island;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Island getIsland() {
        return island;
    }
}