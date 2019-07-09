package org.savage.skyblock.API;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;

import java.util.UUID;

public class IslandDemoteEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private UUID demoter;
    private UUID target;
    private Island island;

    public IslandDemoteEvent(Island island, UUID demoter, UUID target) {
        this.island = island;
        this.demoter = demoter;
        this.target = target;
    }

    public Island getIsland() {
        return island;
    }

    public UUID getDemoter() {
        return demoter;
    }

    public UUID getTarget() {
        return target;
    }

}