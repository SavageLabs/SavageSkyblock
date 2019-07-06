package org.savage.skyblock.filemanager;

import org.savage.skyblock.SkyBlock;
import java.io.File;

public class FileManager {

    public CustomFile dataFileCustom = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/data.yml"));
    public CustomFile guiFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/guis.yml"));
    public CustomFile worth = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/worth.yml"));
    public CustomFile upgrades = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/upgrades.yml"));

    public void setup() {

        dataFileCustom.setup(false);
        worth.setup(true);
        guiFile.setup(true);
        upgrades.setup(true);

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }

        SkyBlock.getInstance().getReflectionManager().nmsHandler.generate("skyBlock");
    }
}