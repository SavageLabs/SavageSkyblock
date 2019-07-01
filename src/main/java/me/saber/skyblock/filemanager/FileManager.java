package me.saber.skyblock.filemanager;

import me.saber.skyblock.Main;
import org.bukkit.Bukkit;

import java.io.File;

public class FileManager {

    public CustomFile dataFileCustom = new CustomFile(new File(Main.getInstance().getDataFolder()+"/data.yml"));
    public CustomFile guiFile = new CustomFile(new File(Main.getInstance().getDataFolder() + "/guis.yml"));
    public CustomFile levelWorth = new CustomFile(new File(Main.getInstance().getDataFolder()+"/level-worth.yml"));

    public void setup(){

        dataFileCustom.setup(false);
        levelWorth.setup(true);
        guiFile.setup(true);

        if (!new File(Main.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(Main.getInstance().getDataFolder() + "/Schematics").mkdir();
        }
        if (Bukkit.getWorld("skyBlock") == null) {
            Main.getInstance().getWorldGenerator().generateWorld("skyBlock");
        }
    }
}