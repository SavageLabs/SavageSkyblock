package org.savage.skyblock.filemanager;

import org.savage.skyblock.SkyBlock;

import java.io.*;

public class FileManager {

    private CustomFile data = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/data.yml"));
    private CustomFile guis = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/guis.yml"));
    private CustomFile worth = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/worth.yml"));
    private CustomFile upgrades = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/upgrades.yml"));
    private CustomFile messages = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/messages.yml"));
    private CustomFile playerData = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/playerData.yml"));

    public void setup() {

        data.setup(false);
        worth.setup(true);
        guis.setup(true);
        upgrades.setup(true);
        messages.setup(true);
        playerData.setup(true);

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }

       // SkyBlock.getInstance().saveResource("default.schematic", true);
       // //load the schems into the plugin's directory


        SkyBlock.getInstance().getReflectionManager().nmsHandler.generate("skyBlock");
    }


    public void copyFile(File sourceFile, File destinationFile){
        InputStream inStream = null;
        OutputStream outStream = null;
        try{
            File afile = sourceFile;
            File bfile = destinationFile;
            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(bfile);
            byte[] buffer = new byte[1024];
            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
            }
            inStream.close();
            outStream.close();
            //delete the original file
            afile.delete();
        }catch(IOException e){
            e.printStackTrace();
        }
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

    public CustomFile getPlayerData() {
        return playerData;
    }
}