package org.savage.skyblock;

import net.minecraft.server.v1_14_R1.DefinedStructureProcessorBlockIgnore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.upgrades.Upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Placeholder {

    public static int getIslandTopPlacement(String placeholderString) {
        placeholderString = ChatColor.stripColor(SkyBlock.getInstance().getUtils().color(placeholderString));
        if (placeholderString.contains("%top-")) {
            //contains the placeholder, we need to figure out the number in it
            placeholderString = placeholderString.substring(placeholderString.lastIndexOf("%top-"));
            return SkyBlock.getInstance().getUtils().getIntegersFromString(placeholderString);
        }
        return 1;
    }

    public static List<String> convertPlaceholders(List<String> originalList, Island island, Upgrade upgrade){
        if (island == null) return SkyBlock.getInstance().getUtils().color(originalList);

        List<String> newList = new ArrayList<>();

        String currentUpgrade = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.generator-current-upgrade");
        String nextUpgrade = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.generator-next-upgrade");

        HashMap<Material, Integer> map = Upgrade.Upgrades.getMaterialChanceMap(island.getUpgradeTier(upgrade));
        HashMap<Material, Integer> map2 = Upgrade.Upgrades.getMaterialChanceMap(island.getUpgradeTier(upgrade) + 1);

        for (String s : originalList){
            boolean t = false;
            String n = "%" + upgrade.getName() + "_";
            s = s.replace(n + "currentTier%", island.getUpgradeTier(upgrade) + "");
            if (s.contains("nextTier%")) {
                if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                    s = s.replace(n + "nextTier%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max"));
                } else {
                    s = s.replace(n + "nextTier%", (island.getUpgradeTier(upgrade) + 1) + "");
                }
            }

            if (s.contains("currentUpgrade%")) {
                for (Material material: map.keySet()){
                    int chance = map.get(material);
                    String ss = s;
                    ss = ss.replace(n+"currentUpgrade%", currentUpgrade.replace("%material%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.materials."+material.name().toLowerCase())).replace("%chance%", chance+""));
                    newList.add(ss);
                }
                t = true;
            }
            if (s.contains("nextUpgrade%")) {
                if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                    String ss = s;
                    ss = ss.replace(n+"nextUpgrade%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max"));
                    newList.add(ss);
                }else{
                    for (Material material : map2.keySet()) {
                        int chance = map2.get(material);
                        String ss = s;
                        ss = ss.replace(n+"nextUpgrade%", nextUpgrade.replace("%material%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.materials."+material.name().toLowerCase())).replace("%chance%", chance+""));
                        newList.add(ss);
                    }
                }
                t = true;
            }

            if (s.contains("%cost%")) {
                if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                    s = s.replace("%cost%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max")).replace("$", "");
                } else {
                    s = s.replace("%cost%", (Upgrade.Upgrades.getTierCost(upgrade, island.getUpgradeTier(upgrade)) + 1) + "");
                }
            }

            if (!t) {
                newList.add(s);
            }
        }
        return SkyBlock.getInstance().getUtils().color(newList);
    }

    public static String convertPlaceholders(String s, Island island, Upgrade upgrade) {
        String n = "%" + upgrade.getName() + "_";
        s = s.replace(n + "currentTier%", island.getUpgradeTier(upgrade) + "");
        s = s.replace(n + "currentUpgrade%", Upgrade.Upgrades.getTierValue(upgrade, island.getUpgradeTier(upgrade), island) + "");

        //check for generator upgrade...

            if (s.contains("nextTier%")) {
                if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                    s = s.replace(n + "nextTier%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max"));
                } else {
                    s = s.replace(n + "nextTier%", (island.getUpgradeTier(upgrade) + 1) + "");
                }
            }
            if (s.contains("nextUpgrade%")) {
                if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                    s = s.replace(n + "nextUpgrade%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max").replace("%", ""));
                } else {
                    s = s.replace(n + "nextUpgrade%", (Upgrade.Upgrades.getTierValue(upgrade, (island.getUpgradeTier(upgrade) + 1), null)) + "");
                }
            }

        if (s.contains("%cost%")) {
            if ((island.getUpgradeTier(upgrade) + 1) > Upgrade.Upgrades.getMaxTier(upgrade)) {
                s = s.replace("%cost%", SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("placeholders.max")).replace("$", "");
            } else {
                s = s.replace("%cost%", (Upgrade.Upgrades.getTierCost(upgrade, island.getUpgradeTier(upgrade)) + 1) + "");
            }
        }

        return SkyBlock.getInstance().getUtils().color(s);
    }

    public static String convertPlaceholders(String s, Island island) {
        if (island != null) {
            s = s.replace("%owner%", SkyBlock.getInstance().getUtils().getNameFromUUID(island.getOwnerUUID()));
            s = s.replace("%level%", island.getLevel() + "");
            s = s.replace("%worth%", island.getWorth() + "");
            s = s.replace("%block-worth%", island.getBlockWorth() + "");
            s = s.replace("%spawner-worth%", island.getSpawnerWorth() + "");

            s = s.replace("{island-name}", island.getName());
            s = s.replace("{island-level}", island.getLevel() + "");
            s = s.replace("{island-top}", island.getTopPlace() + "");
        } else {
            String invalid = SkyBlock.getInstance().getUtils().getSettingString("invalid-island-top-lore-placeholders");
            s = s.replace("%owner%", invalid);
            s = s.replace("%level%", invalid);
            s = s.replace("%worth%", invalid);
            s = s.replace("%block-worth%", invalid);
            s = s.replace("%spawner-worth%", invalid);

            s = s.replace("{island-name}", invalid);
            s = s.replace("{island-level}", invalid);
            s = s.replace("{island-top}", invalid);
        }
        return s;
    }

    public static List<String> convertPlaceholders(List<String> list, Island island) {
        if (island == null) return new ArrayList<>();

        List<String> l = new ArrayList<>();

        for (String s : list) {
            boolean t = false;

            s = convertPlaceholders(s, island);

            if (s.contains("%officers%")) {

                for (UUID uuid : island.getOfficerList()) {
                    String name = SkyBlock.getInstance().getUtils().getNameFromUUID(uuid);
                    s = s.replace("%officers%", name);
                    l.add(s);
                }
                t = true;
            }
            if (s.contains("%members%")) {
                for (UUID uuid : island.getMemberList()) {
                    String name = SkyBlock.getInstance().getUtils().getNameFromUUID(uuid);
                    s = s.replace("%members%", name);
                    l.add(s);
                }
                t = true;
            }

            if (!t) {
                l.add(s);
            }
        }

        return SkyBlock.getInstance().getUtils().color(l);
    }

}