package me.saber.skyblock.island.events;

import me.saber.skyblock.island.Island;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class IslandKickEvent extends Event implements Cancellable {

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

    private UUID kicker;
    private UUID target;
    private Island island;
    private boolean isCancelled;

    public IslandKickEvent(Island island, UUID kicker, UUID target) {
        this.island = island;
        this.kicker = kicker;
        this.target = target;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getKicker() {
        return kicker;
    }

    public UUID getTarget() {
        return target;
    }

}
