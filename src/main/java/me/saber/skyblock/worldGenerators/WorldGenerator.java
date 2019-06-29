package me.saber.skyblock.worldGenerators;

import me.saber.skyblock.WG.WG;
import me.saber.skyblock.Main;
import me.trent.worldedit6.WorldEdit6;
import me.trent.worldedit7.WorldEdit7;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

import java.io.File;

public class WorldGenerator {

    public void generateWorld(String worldName) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(new VoidWorld().getDefaultWorldGenerator(worldName, "skyBlockVoid"));
        worldCreator.createWorld();
    }

    public void pasteSchem(Location location, String fileName) {
        File file = new File(Main.getInstance().getDataFolder() + "/Schematics/" + fileName);

        //todo make a checker for WorldEdit and WorldGuard 6.1 and 7.0

        if (WG.isOldWorldEdit()){
            //is 6. run different methods
            Bukkit.broadcastMessage("OLD 6.1");
            WorldEdit6.paste(file, location);
        }

        if (WG.isNewWorldEdit()){
            Bukkit.broadcastMessage("NEW 7.1");
            WorldEdit7.paste(file, location);
        }
    }
}