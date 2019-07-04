package org.savage.skyblock.worldedit;

import com.sk89q.worldedit.WorldEdit;

public class WorldEditPersistence {

    public static WorldEdit getWorldEdit(){
        return WorldEdit.getInstance();
    }

    public static String worldEditVersion;

    public static boolean isOldWorldEdit(){
        return worldEditVersion.contains("6.");
    }
    public static boolean isNewWorldEdit(){
        return worldEditVersion.contains("7.");
    }
}