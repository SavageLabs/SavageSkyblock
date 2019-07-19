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
    private CustomFile scoreboard = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/scoreboard.yml"));

    private CustomFile questFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/Quests/quests.yml"));
    private CustomFile foreverQuestFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/Quests/forever.yml"));
    private CustomFile dailyQuestFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/Quests/daily.yml"));
    private CustomFile weeklyQuestFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/Quests/weekly.yml"));
    private CustomFile monthlyQuestFile = new CustomFile(new File(SkyBlock.getInstance().getDataFolder() + "/Quests/monthly.yml"));

    public void setup() {

        data.setup(false, "");
        worth.setup(true, "");
        guis.setup(true, "");
        upgrades.setup(true, "");
        messages.setup(true, "");
        playerData.setup(true, "");
        scoreboard.setup(true, "");

        if (!new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").exists()) {
            new File(SkyBlock.getInstance().getDataFolder() + "/Schematics").mkdir();
        }

        File questFolder = new File(SkyBlock.getInstance().getDataFolder()+"/Quests");

        if (!questFolder.exists()){
            questFolder.mkdir();//create the folder
            questFile.setup(true, "Quests");
            foreverQuestFile.setup(true, "Quests");
            dailyQuestFile.setup(true, "Quests");
            weeklyQuestFile.setup(true, "Quests");
            monthlyQuestFile.setup(true, "Quests");
        }
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

    public CustomFile getScoreboard() {
        return scoreboard;
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

    public CustomFile getDailyQuestFile() {
        return dailyQuestFile;
    }

    public CustomFile getForeverQuestFile() {
        return foreverQuestFile;
    }

    public CustomFile getMonthlyQuestFile() {
        return monthlyQuestFile;
    }

    public CustomFile getQuestFile() {
        return questFile;
    }

    public CustomFile getWeeklyQuestFile() {
        return weeklyQuestFile;
    }
}