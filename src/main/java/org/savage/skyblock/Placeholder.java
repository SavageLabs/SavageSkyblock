package org.savage.skyblock;

import org.bukkit.ChatColor;
import org.savage.skyblock.island.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Placeholder {

    public static int getIslandTopPlacement(String placeholderString) {
        placeholderString = ChatColor.stripColor(Main.getInstance().getUtils().color(placeholderString));
        if (placeholderString.contains("%top-")) {
            //contains the placeholder, we need to figure out the number in it
            placeholderString = placeholderString.substring(placeholderString.lastIndexOf("%top-"));
            return Main.getInstance().getUtils().getIntegersFromString(placeholderString);
        }
        return 1;
    }

    public static String convertPlaceholders(String s, Island island) {
        s = s.replace("%owner%", Main.getInstance().getUtils().getNameFromUUID(island.getOwnerUUID()));
        s = s.replace("{island-name}", island.getName());
        s = s.replace("{island-level}", island.getLevel() + "");
        s = s.replace("{island-top}", island.getTopPlace() + "");

        return s;
    }

    public static List<String> convertPlaceholders(List<String> list, Island island) {
        List<String> l = new ArrayList<>();

        for (String s : list) {
            boolean t = false;
            s = convertPlaceholders(s, island);

            if (s.contains("%officers%")) {
                List<UUID> officers = island.getOfficerList();
                for (UUID uuid : officers) {
                    String name = Main.getInstance().getUtils().getNameFromUUID(uuid);
                    s = s.replace("%officers%", name);
                    l.add(s);
                }
                t = true;
            }
            if (s.contains("%members%")) {
                List<UUID> members = island.getMemberList();
                for (UUID uuid : members) {
                    String name = Main.getInstance().getUtils().getNameFromUUID(uuid);
                    s = s.replace("%members%", name);
                    l.add(s);
                }
                t = true;
            }
            if (!t) {
                l.add(s);
            }
        }

        return Main.getInstance().getUtils().colorList(l);
    }

}