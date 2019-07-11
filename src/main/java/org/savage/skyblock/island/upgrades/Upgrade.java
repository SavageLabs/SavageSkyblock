package org.savage.skyblock.island.upgrades;

import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

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

        public static double getTierCost(Upgrade upgrade, int tier){
            String name = upgrade.getName();
            return SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getDouble("upgrades."+name+".tiers."+tier+".cost");
        }

        public static int getMaxTier(Upgrade upgrade){
            String name = upgrade.getName();
            return SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getConfigurationSection("upgrades."+name+".tiers").getKeys(false).size();
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
