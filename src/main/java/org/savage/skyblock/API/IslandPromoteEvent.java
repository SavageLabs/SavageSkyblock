package org.savage.skyblock.API;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

import java.util.UUID;

public class IslandPromoteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private UUID promoter;
    private UUID target;
    private Island island;
    private boolean isCancelled;

    public IslandPromoteEvent(Island island, UUID promoter, UUID target) {
        this.island = island;
        this.promoter = promoter;
        this.target = target;
        this.isCancelled = false;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getPromoter() {
        return promoter;
    }

    public UUID getTarget() {
        return target;
    }

}
