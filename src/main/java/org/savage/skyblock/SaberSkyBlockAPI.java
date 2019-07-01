package org.savage.skyblock;

import org.savage.skyblock.filemanager.FileManager;
import org.savage.skyblock.generators.WorldGenerator;
import org.savage.skyblock.island.IslandUtils;

public class SaberSkyBlockAPI {

    private Main getSkyBlockPlusInstance(){
        return Main.getInstance();
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