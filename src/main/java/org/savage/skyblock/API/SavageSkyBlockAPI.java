package org.savage.skyblock.API;

import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Utils;
import org.savage.skyblock.filemanager.FileManager;
import org.savage.skyblock.generators.WorldGenerator;
import org.savage.skyblock.island.IslandUtils;

public class SavageSkyBlockAPI {

    private SkyBlock getSkyBlockPlusInstance() {
        return SkyBlock.getInstance();
    }

    public IslandUtils getIslandUtils(){
        return getSkyBlockPlusInstance().getIslandUtils();
    }

    public WorldGenerator getWorldGenerator(){
        return getSkyBlockPlusInstance().getWorldGenerator();
    }

    public Utils getUtils(){
        return getSkyBlockPlusInstance().getUtils();
    }

    public FileManager getFileManager(){
        return getSkyBlockPlusInstance().getFileManager();
    }
}