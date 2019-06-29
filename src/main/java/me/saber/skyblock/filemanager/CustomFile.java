package me.saber.skyblock.filemanager;
import me.saber.skyblock.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomFile {

    private File file;
    private YamlConfiguration fileConfig;

    public CustomFile(File file){
        this.file = file;
        this.fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void setup(boolean loadFromProject){
        if (!getFile().exists()){
            //create
            if (loadFromProject){
                Main.getInstance().saveResource(file.getName(), false);
            }else{
                try{
                    getFile().createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        reloadFile();
    }

    public void reloadFile(){
        this.fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void saveFile(){
        try{
            getFileConfig().save(file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileConfig(YamlConfiguration fileConfig) {
        this.fileConfig = fileConfig;
    }

    public YamlConfiguration getFileConfig() {
        return fileConfig;
    }
}
