package me.saber.skyblock.Island.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * @author Trent @ Aysteria Development
 *
 * Event when creating an Island
 */

public class IslandCreateEvent extends Event implements Cancellable {

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
    private boolean isCancelled;

    public IslandCreateEvent(UUID owner) {
        this.owner = owner;
        this.isCancelled = false;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

}