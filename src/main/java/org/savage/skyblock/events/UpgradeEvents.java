package org.savage.skyblock.events;

import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.material.Crops;
import org.savage.skyblock.MultiMaterials;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.upgrades.Upgrade;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class UpgradeEvents implements Listener {

    @EventHandler
    public void generator(BlockFromToEvent e){
        Block block = e.getToBlock();
        Location loc = block.getLocation();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(loc);
        if (island != null){
            int tier = island.getUpgradeTier(Upgrade.GENERATOR);

            HashMap<Material, Integer> materialChances = Upgrade.Upgrades.getMaterialChanceMap(tier);
            //map is already sorted from lower chance to higher chance

            //iterate through the chances in the map
            for (Material material : materialChances.keySet()){
                int chance = materialChances.get(material);
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                if (randomNum <= chance){
                    //do this material
                    block.setType(material);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent e) {
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getLocation());
        if (island != null) {
            int tier = island.getUpgradeTier(Upgrade.SPAWNER_RATE);
            if (tier == 0) return;
            lowerSpawnerDelay(e, Upgrade.Upgrades.getTierValue(Upgrade.SPAWNER_RATE, tier, null));
        }
    }

    private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
        int lowerby = (int) Math.round(e.getSpawner().getDelay() * multiplier);
        e.getSpawner().setDelay(e.getSpawner().getDelay() - lowerby);
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(e.getBlock().getLocation());
        if (island != null) {
            int tier = island.getUpgradeTier(Upgrade.SPAWNER_RATE);
            int chance = Upgrade.Upgrades.getTierValue(Upgrade.CROP_RATE, tier, null);
            if (tier == 0 || chance == 0) return;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
            if (randomNum <= chance) growCrop(e);
        }
    }

    private void growCrop(BlockGrowEvent e) {
        if (e.getBlock().getType().equals(Material.CROPS)) {
            e.setCancelled(true);
            Crops c = new Crops(CropState.RIPE);
            BlockState bs = e.getBlock().getState();
            bs.setData(c);
            bs.update();
        }
        Block below = e.getBlock().getLocation().subtract(0, 1, 0).getBlock();
        if (below.getType() == MultiMaterials.SUGAR_CANE_BLOCK) {
            Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();

            if (above.getType() == Material.AIR && above.getLocation().add(0, -2, 0).getBlock().getType() != Material.AIR) {
                above.setType(MultiMaterials.SUGAR_CANE_BLOCK);
            }
        } else if (below.getType() == Material.CACTUS) {
            Block above = e.getBlock().getLocation().add(0, 1, 0).getBlock();

            if (above.getType() == Material.AIR && above.getLocation().add(0, -2, 0).getBlock().getType() != Material.AIR) {
                above.setType(Material.CACTUS);
            }
        }
    }
}
