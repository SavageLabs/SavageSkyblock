package me.saber.skyblock;

import me.saber.skyblock.Island.IslandUtils;
import me.saber.skyblock.fileManager.FileManager;
import me.saber.skyblock.worldGenerators.WorldGenerator;

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