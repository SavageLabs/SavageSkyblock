package org.savage.skyblock.filemanager;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.nms.NMSHandler_v1_13_R2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public CustomFile dataFileCustom = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/data.yml"));
    public CustomFile guiFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/guis.yml"));
    public CustomFile levelWorth = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/worth.yml"));

    public void setup() {

        dataFileCustom.setup(false);
        levelWorth.setup(true);
        guiFile.setup(true);

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }

        SkyBlock.getInstance().getReflectionManager().nmsHandler.generate("skyBlock");

        //SkyBlock.getInstance().getWorldGenerator().generateWorld("skyBlock");







        /*
        if (Bukkit.getWorld("skyBlock") == null) {
            //we want to check for a folder in the world dirs
            if (new File("skyBlock").exists()) {
                System.out.print("\n\n\n\n\n\n\n THERE \n\n\n\n\n");
                //try to load it now
                Bukkit.getServer().createWorld(new WorldCreator("skyBlock")); // load
            } else {

            }
        }

         */
    }
}