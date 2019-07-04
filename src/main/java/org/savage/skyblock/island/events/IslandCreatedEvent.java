package org.savage.skyblock.island.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

import java.util.UUID;

/**
 * @author Trent @ Aysteria Development
 * <p>
 * Event when an Island has been created, cannot modify.
 */

public class IslandCreatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private UUID owner;
    private Island island;

    public IslandCreatedEvent(UUID owner, Island island) {
        this.owner = owner;
        this.island = island;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getOwner() {
        return owner;
    }

}