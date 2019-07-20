package org.savage.skyblock.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.rules.Rule;

public class WildStackerEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void mobSpawning(com.bgsoftware.wildstacker.api.events.SpawnerSpawnEvent e){
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getEntity().getLivingEntity().getLocation());
        if (island == null) return;
        if (island.hasRule(Rule.MOB_SPAWNING)){
            e.getEntity().remove();
        }
    }
}
