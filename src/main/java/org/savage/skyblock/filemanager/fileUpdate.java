package org.savage.skyblock.filemanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class fileUpdate {

    public static void updateFile(File newFile, File oldFile){
        FileConfiguration newF = YamlConfiguration.loadConfiguration(newFile);
        FileConfiguration oldF = YamlConfiguration.loadConfiguration(oldFile);
        int changes = 0;

        if (newF != null && oldF != null){

            for (String key : newF.getConfigurationSection("").getKeys(false)){
                if (!oldF.contains(key)){
                    oldF.set(key, newF.getConfigurationSection(key));
                    changes++;
                }
            }
            try{
                oldF.save(oldFile);
                System.out.println("\n [SavageSkyBlock Config Updater] Updated "+changes+" values in the config.yml automatically! \n");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}