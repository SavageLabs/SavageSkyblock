package org.savage.skyblock.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.upgrades.Upgrade;

public class IslandUpgradeEvent extends Event implements Cancellable {

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    private final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private Island island;
    private Player player;
    private Upgrade upgrade;
    private int newTier;
    private boolean isCancelled = false;


    public IslandUpgradeEvent(Upgrade upgrade, int newTier, Island island, Player player){
        this.island = island;
        this.player = player;
        this.upgrade = upgrade;
        this.newTier = newTier;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void setNewTier(int newTier) {
        this.newTier = newTier;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    public int getNewTier() {
        return newTier;
    }

    public Island getIsland() {
        return island;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }
}
