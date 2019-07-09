package org.savage.skyblock.generators;

import me.trent.worldedit6.WorldEdit6;
import me.trent.worldedit7.WorldEdit7;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.worldedit.WorldEditPersistence;

import java.io.File;

public class WorldGenerator {

    public void generateWorld(String worldName) { // simple generator for all versions
        World world = WorldCreator.name(worldName).type(WorldType.FLAT).environment(World.Environment.NORMAL).generator(new VoidWorld()).createWorld();
    }

    public void pasteSchem(Location location, String fileName) {
        File file = new File(SkyBlock.getInstance().getDataFolder() + "/Schematics/" + fileName);

        if (WorldEditPersistence.isOldWorldEdit()) {
            WorldEdit6.paste(file, location);
        }

        if (WorldEditPersistence.isNewWorldEdit()) {
            WorldEdit7.paste(file, location);
        }
    }
}