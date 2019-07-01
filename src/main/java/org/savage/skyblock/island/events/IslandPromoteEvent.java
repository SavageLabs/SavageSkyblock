package org.savage.skyblock.island.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

import java.util.UUID;

public class IslandPromoteEvent extends Event implements Cancellable {

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private UUID owner;
    private UUID target;
    private Island island;
    private boolean isCancelled;

    public IslandPromoteEvent(Island island, UUID owner, UUID target) {
        this.island = island;
        this.owner = owner;
        this.target = target;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getTarget() {
        return target;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }
}
