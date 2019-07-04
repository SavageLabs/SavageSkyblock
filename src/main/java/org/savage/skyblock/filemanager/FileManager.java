package org.savage.skyblock.filemanager;

import org.bukkit.Bukkit;
import org.savage.skyblock.SkyBlock;

import java.io.File;

public class FileManager {

    public CustomFile dataFileCustom = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/data.yml"));
    public CustomFile guiFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/guis.yml"));
    public CustomFile levelWorth = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/worth.yml"));

    public void setup(){

        dataFileCustom.setup(false);
        levelWorth.setup(true);
        guiFile.setup(true);

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }
        if (Bukkit.getWorld("skyBlock") == null) {
            SkyBlock.getInstance().getWorldGenerator().generateWorld("skyBlock");
        }
    }
}