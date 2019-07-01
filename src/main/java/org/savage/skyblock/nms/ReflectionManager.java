package org.savage.skyblock.nms;


import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ReflectionManager {

    public List<Material> tileEntities = Arrays.asList(Material.CHEST, Material.DROPPER, Material.HOPPER, Material.HOPPER_MINECART, Material.ENDER_CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE);

    public NMSHandler nmsHandler;

    public void setup() {

        try {
            nmsHandler = (NMSHandler) Class.forName("org.savage.skyblock.nms.NMSHandler_" + Version.getRawVersionString()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}