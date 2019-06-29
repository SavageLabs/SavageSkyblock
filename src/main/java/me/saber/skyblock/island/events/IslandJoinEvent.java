package me.saber.skyblock.island.events;

import me.saber.skyblock.island.Island;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class IslandJoinEvent extends Event implements Cancellable {

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

    private UUID joiner;
    private Island island;
    private boolean isCancelled;

    public IslandJoinEvent(Island island, UUID joiner) {
        this.island = island;
        this.joiner = joiner;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getJoiner() {
        return joiner;
    }
}
