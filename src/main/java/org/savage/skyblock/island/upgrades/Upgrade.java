package org.savage.skyblock.island.upgrades;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.savage.skyblock.Materials;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public enum Upgrade {

    PROTECTION_RADIUS(1, "protection-radius"),
    MEMBER_LIMIT(2, "member-limit"),
    SPAWNER_RATE(3, "spawner-rate"),
    HOPPER_LIMIT(4, "hopper-limit"),
    SPAWNER_LIMIT(5, "spawner-limit"),
    GENERATOR(6, "generator"),
    CROP_RATE(7, "crop-rate");

    private int id;
    private String name;

    Upgrade(int id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static class Upgrades {

        public static int getTierValue(Upgrade upgrade, int tier, Island island){
            String name = upgrade.getName();
            if (island == null){
                return SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getInt("upgrades."+name+".tiers."+tier+".upgrade-value");
            }else { // get the value straight from the island
                if (upgrade.getId() == 1) {
                    return island.getProtectionRadius();
                }
                if (upgrade.getId() == 2) {
                    return island.getMemberLimit();
                }
                if (upgrade.getId() == 4) {
                    return island.getHopperLimit();
                }
                //if we're not returned yet, return the config values for the tier
                return SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getInt("upgrades."+name+".tiers."+tier+".upgrade-value");
            }
            //upgrade above can be modified via permission and override their original Is Upgrade Values...
        }

        public static HashMap<Material, Integer> getMaterialChanceMap(int tier){
            HashMap<Material, Integer> map = new HashMap<>();

            int max = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getConfigurationSection("upgrades.generator.tiers."+tier+".blocks").getKeys(false).size();
            YamlConfiguration f = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig();
            for (int a = 1; a<= max; a++){
                //iterate through all of the block values
                String materialName = f.getString("upgrades.generator.tiers."+tier+".blocks."+a+".material");
                int data = f.getInt("upgrades.generator.tiers."+tier+".blocks."+a+".material-data");
                int chance = f.getInt("upgrades.generator.tiers."+tier+".blocks."+a+".chance");

                Material material;

                Materials materials = Materials.requestXMaterial(materialName, (byte)data);
                if (materials != null && materials.parseMaterial() != null){
                    material = materials.parseMaterial();
                }else{
                    material = Material.getMaterial(materialName.toUpperCase());
                }
                if (material != null){
                    map.put(material, chance);
                }
            }

            HashMap<Material, Integer> sorted = map
                    .entrySet()
                    .stream()
                    .sorted(comparingByValue())
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));

            return sorted;
        }

        public static double getTierCost(Upgrade upgrade, int tier){
            String name = upgrade.getName();
            return SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getDouble("upgrades."+name+".tiers."+tier+".cost");
        }

        public static int getMaxTier(Upgrade upgrade){
            String name = upgrade.getName();
            int m = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getConfigurationSection("upgrades."+name+".tiers").getKeys(false).size();
            if (upgrade.equals(Upgrade.GENERATOR)){
                return m - 1;
            }
            return m;
        }

        public static Upgrade getUpgrade(int id){
            for (Upgrade upgrade : Upgrade.values()){
                if (upgrade.getId() == id){
                    return upgrade;
                }
            }
            return null;
        }

        public static Upgrade getUpgrade(String name){
            for (Upgrade upgrade : Upgrade.values()){
                if (upgrade.getName().equalsIgnoreCase(name)){
                    return upgrade;
                }
            }
            return null;
        }
    }

}
