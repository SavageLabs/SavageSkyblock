package me.trent;

import me.trent.Island.IslandUtils;
import me.trent.fileManager.FileManager;
import me.trent.worldGenerators.WorldGenerator;

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