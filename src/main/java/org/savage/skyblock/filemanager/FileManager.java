package org.savage.skyblock.filemanager;

import org.savage.skyblock.SkyBlock;
import java.io.File;

public class FileManager {

    private CustomFile data = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/data.yml"));
    private CustomFile guis = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/guis.yml"));
    private CustomFile worth = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/worth.yml"));
    private CustomFile upgrades = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/upgrades.yml"));
    private CustomFile messages = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/messages.yml"));

    public void setup() {

        data.setup(false);
        worth.setup(true);
        guis.setup(true);
        upgrades.setup(true);
        messages.setup(true);

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }

        SkyBlock.getInstance().getReflectionManager().nmsHandler.generate("skyBlock");
    }

    public CustomFile getData() {
        return data;
    }

    public CustomFile getGuis() {
        return guis;
    }

    public CustomFile getMessages() {
        return messages;
    }

    public CustomFile getUpgrades() {
        return upgrades;
    }

    public CustomFile getWorth() {
        return worth;
    }
}