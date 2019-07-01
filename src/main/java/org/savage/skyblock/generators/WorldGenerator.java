package org.savage.skyblock.generators;

import me.trent.worldedit6.WorldEdit6;
import me.trent.worldedit7.WorldEdit7;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.savage.skyblock.Main;
import org.savage.skyblock.worldguard.WorldGuardPersistence;

import java.io.File;

public class WorldGenerator {

    public void generateWorld(String worldName) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(new VoidWorld().getDefaultWorldGenerator(worldName, "skyBlockVoid"));
        worldCreator.createWorld();
    }

    public void pasteSchem(Location location, String fileName) {
        File file = new File(Main.getInstance().getDataFolder() + "/Schematics/" + fileName);

        if (WorldGuardPersistence.isOldWorldEdit()){
            WorldEdit6.paste(file, location);
        }

        if (WorldGuardPersistence.isNewWorldEdit()){
            WorldEdit7.paste(file, location);
        }
    }
}