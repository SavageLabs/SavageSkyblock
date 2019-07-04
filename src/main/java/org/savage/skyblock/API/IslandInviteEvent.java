package org.savage.skyblock.API;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

import java.util.UUID;

public class IslandInviteEvent extends Event implements Cancellable {

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

    private UUID inviter;
    private UUID target;
    private Island island;
    private boolean isCancelled;

    public IslandInviteEvent(Island island, UUID inviter, UUID target) {
        this.island = island;
        this.inviter = inviter;
        this.target = target;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getInviter() {
        return inviter;
    }

    public UUID getTarget() {
        return target;
    }
}
