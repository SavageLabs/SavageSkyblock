package org.savage.skyblock;

import org.bukkit.Material;
import org.savage.skyblock.nms.Version;

public class MultiMaterials {

    public static Material SUGAR_CANE_BLOCK, BANNER, CROPS, REDSTONE_LAMP_ON,
            STAINED_GLASS, STATIONARY_WATER, STAINED_CLAY, WOOD_BUTTON,
            SOIL, MOB_SPANWER, THIN_GLASS, IRON_FENCE, NETHER_FENCE, FENCE,
            WOODEN_DOOR, TRAP_DOOR, FENCE_GATE, BURNING_FURNACE, DIODE_BLOCK_OFF,
            DIODE_BLOCK_ON, ENCHANTMENT_TABLE, FIREBALL;

    public static boolean mc113 = false;
    public static boolean mc114 = false;
    public static boolean mc17 = false;
    public static boolean mc18 = false;

    public static void setupMultiversionMaterials() {

        String version = Version.getVersion().getVersionInteger()+"";

        if (version.startsWith("17")){
            mc17 = true;
        }
        if (version.startsWith("18")){
            mc18 = true;
        }
        if (version.startsWith("113")){
            mc113 = true;
        }
        if (version.startsWith("114")){
            mc114 = true;
        }

        if (mc113 || mc114) {
            BANNER = Materials.BLACK_BANNER.parseMaterial();
            CROPS = Material.valueOf("LEGACY_CROPS");
            SUGAR_CANE_BLOCK = Material.valueOf("LEGACY_SUGAR_CANE_BLOCK");
            REDSTONE_LAMP_ON = Material.valueOf("LEGACY_REDSTONE_LAMP_ON");
            STAINED_GLASS = Material.valueOf("LEGACY_STAINED_GLASS");
            STATIONARY_WATER = Material.valueOf("LEGACY_STATIONARY_WATER");
            STAINED_CLAY = Material.valueOf("LEGACY_STAINED_CLAY");
            WOOD_BUTTON = Material.valueOf("LEGACY_WOOD_BUTTON");
            SOIL = Material.valueOf("LEGACY_SOIL");
            MOB_SPANWER = Material.valueOf("LEGACY_MOB_SPAWNER");
            THIN_GLASS = Material.valueOf("LEGACY_THIN_GLASS");
            IRON_FENCE = Material.valueOf("LEGACY_IRON_FENCE");
            NETHER_FENCE = Material.valueOf("LEGACY_NETHER_FENCE");
            FENCE = Material.valueOf("LEGACY_FENCE");
            WOODEN_DOOR = Material.valueOf("LEGACY_WOODEN_DOOR");
            TRAP_DOOR = Material.valueOf("LEGACY_TRAP_DOOR");
            FENCE_GATE = Material.valueOf("LEGACY_FENCE_GATE");
            BURNING_FURNACE = Material.valueOf("LEGACY_BURNING_FURNACE");
            DIODE_BLOCK_OFF = Material.valueOf("LEGACY_DIODE_BLOCK_OFF");
            DIODE_BLOCK_ON = Material.valueOf("LEGACY_DIODE_BLOCK_ON");
            ENCHANTMENT_TABLE = Material.valueOf("LEGACY_ENCHANTMENT_TABLE");
            FIREBALL = Material.valueOf("LEGACY_FIREBALL");
            if (mc114) {
                //TODO We have to support here all of new items of MC 1.14
                // but i'm too lazy to implement them now, hihihi
            }
        } else {
            if (!mc17) {
                BANNER = Material.valueOf("BANNER");
            }
            CROPS = Material.valueOf("CROPS");
            SUGAR_CANE_BLOCK = Material.valueOf("SUGAR_CANE_BLOCK");
            REDSTONE_LAMP_ON = Material.valueOf("REDSTONE_LAMP_ON");
            STAINED_GLASS = Material.valueOf("STAINED_GLASS");
            STATIONARY_WATER = Material.valueOf("STATIONARY_WATER");
            STAINED_CLAY = Material.valueOf("STAINED_CLAY");
            WOOD_BUTTON = Material.valueOf("WOOD_BUTTON");
            SOIL = Material.valueOf("SOIL");
            MOB_SPANWER = Material.valueOf("MOB_SPAWNER");
            THIN_GLASS = Material.valueOf("THIN_GLASS");
            IRON_FENCE = Material.valueOf("IRON_FENCE");
            NETHER_FENCE = Material.valueOf("NETHER_FENCE");
            FENCE = Material.valueOf("FENCE");
            WOODEN_DOOR = Material.valueOf("WOODEN_DOOR");
            TRAP_DOOR = Material.valueOf("TRAP_DOOR");
            FENCE_GATE = Material.valueOf("FENCE_GATE");
            BURNING_FURNACE = Material.valueOf("BURNING_FURNACE");
            DIODE_BLOCK_OFF = Material.valueOf("DIODE_BLOCK_OFF");
            DIODE_BLOCK_ON = Material.valueOf("DIODE_BLOCK_ON");
            ENCHANTMENT_TABLE = Material.valueOf("ENCHANTMENT_TABLE");
            FIREBALL = Material.valueOf("FIREBALL");
        }
    }
}
