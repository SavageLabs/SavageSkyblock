package org.savage.skyblock.events;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        Block blockTo = e.getToBlock();
        Block blockM = e.getBlock();

        Location loc = blockTo.getLocation();
        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(loc);
        if (island != null){
            Material m = blockM.getType();
            if (m.equals(Material.WATER) || m.equals(Material.STATIONARY_WATER) || m.equals(Material.LAVA) || m.equals(Material.STATIONARY_LAVA)) {
                if (blockTo.getType().equals(Material.AIR) && generatesCobble(blockM.getType(), blockTo)) {
                    int tier = island.getUpgradeTier(Upgrade.GENERATOR);

                    HashMap<Material, Integer> materialChances = Upgrade.Upgrades.getMaterialChanceMap(tier);
                    //map is already sorted from lower chance to higher chance

                    //iterate through the chances in the map
                    for (Material material : materialChances.keySet()) {
                        int chance = materialChances.get(material);
                        int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                        if (randomNum <= chance) {
                            //do this material
                            blockTo.setType(material);
                            return;
                        }
                    }
                }
            }
        }
    }

    private BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    private boolean generatesCobble(final Material material, final Block block) {
        final Material material2 = (material.equals(Material.WATER) || material.equals(Material.STATIONARY_WATER)) ? Material.LAVA : Material.WATER;
        final Material material3 = (material.equals(Material.WATER) || material.equals(Material.STATIONARY_WATER)) ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;
        final BlockFace[] faces = this.faces;
        for (int length = faces.length, i = 0; i < length; ++i) {
            final Block relative = block.getRelative(faces[i], 1);
            if (relative.getType().equals(material2) || relative.getType().equals(material3)) {
                return true;
            }
        }
        return false;
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
