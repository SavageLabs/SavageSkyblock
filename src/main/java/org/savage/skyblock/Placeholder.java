package org.savage.skyblock;

import org.bukkit.ChatColor;
import org.savage.skyblock.island.Island;

import java.util.ArrayList;
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
        List<String> l = new ArrayList<>();

        for (String s : list) {
            boolean t = false;

            s = convertPlaceholders(s, island);

            if (s.contains("%officers%")) {
                if (island != null) {
                    List<UUID> officers = island.getOfficerList();
                    for (UUID uuid : officers) {
                        String name = SkyBlock.getInstance().getUtils().getNameFromUUID(uuid);
                        s = s.replace("%officers%", name);
                        l.add(s);
                    }
                }
                t = true;
            }
            if (s.contains("%members%")) {
                if (island != null) {
                    List<UUID> members = island.getMemberList();
                    for (UUID uuid : members) {
                        String name = SkyBlock.getInstance().getUtils().getNameFromUUID(uuid);
                        s = s.replace("%members%", name);
                        l.add(s);
                    }
                }
                t = true;
            }
            if (!t) {
                l.add(s);
            }
        }

        return SkyBlock.getInstance().getUtils().colorList(l);
    }

}