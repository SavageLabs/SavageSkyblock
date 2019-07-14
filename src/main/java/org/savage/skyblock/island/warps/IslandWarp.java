package org.savage.skyblock.island.warps;

import org.bukkit.Location;
import org.savage.skyblock.island.Island;

public class IslandWarp {

    private Island island;
    private String name;
    private Location location;

    public IslandWarp(Island island, String name, Location location){
        this.island = island;
        this.name = name;
        this.location = location;
    }

    public void remove(){
        this.island = null;
        this.name = null;
        this.location = null;
    }

    public Island getIsland() {
        return island;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}