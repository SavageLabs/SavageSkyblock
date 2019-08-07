package org.savage.skyblock.filemanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.savage.skyblock.SkyBlock;

import javax.print.URIException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class CustomFile {

    private File file;
    private YamlConfiguration fileConfig;

    public CustomFile(File file){
        this.file = file;
        this.fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void setup(boolean loadFromProject, String inFolder){
        if (!getFile().exists()){
            if (loadFromProject){
                if (!inFolder.equalsIgnoreCase("")){
                    SkyBlock.getInstance().saveResource(inFolder+"/"+file.getName(), false);
                }else{
                    SkyBlock.getInstance().saveResource(file.getName(), false);
                }
            }else{
                try{
                    getFile().createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }else{
            //already exists, try to update
            /*
            try {
                fileUpdate.updateFile(new File(SkyBlock.getInstance().getClass().getResource(file.getName()).toURI()), new File(SkyBlock.getInstance().getDataFolder()+"/"+file.getName()));
            }catch(URISyntaxException e){
                e.printStackTrace();
            }
            
             */
        }
        loadFile();
    }

    public void loadFile() {
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
