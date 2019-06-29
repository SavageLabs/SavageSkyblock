package me.trent.fileManager;

import me.trent.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

    public File dataFile = new File(Main.getInstance().getDataFolder()+"/data.yml");
    public FileConfiguration d = YamlConfiguration.loadConfiguration(dataFile);

    public void save(){
        try{
            d.save(dataFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void load(){
        d = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void setup(){
        if (!dataFile.exists()){

            try{
                dataFile.createNewFile();

                d.set("data", new ArrayList<>());

                save();

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
