package me.saber.skyblock.Island.events;

import me.saber.skyblock.Island.Island;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class IslandTeleportEvent extends Event implements Cancellable {

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

    private Island island;
    private UUID target;
    private Location from;
    private Location to;
    private boolean isCancelled;

    public IslandTeleportEvent(Island island, UUID target, Location from, Location to) {
        this.island = island;
        this.target = target;
        this.from = from;
        this.to = to;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getTarget() {
        return target;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

}
