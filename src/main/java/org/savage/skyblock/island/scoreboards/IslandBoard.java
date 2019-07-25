package org.savage.skyblock.island.scoreboards;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.*;
import org.savage.skyblock.island.MemoryPlayer;

public class IslandBoard {

    public boolean PAPI = PluginHook.isEnabled("PlaceholderAPI");
    public boolean MVdW = PluginHook.isEnabled("MVdWPlaceholderAPI");

    public void createScoreBoard(MemoryPlayer memoryPlayer){
        CScoreboard scoreboard = new CScoreboard("a", "b", "c");
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig();

        String title = f.getString("scoreboard-title");
        String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");
        Player p = memoryPlayer.getPlayer();

        for (String s : f.getStringList("scoreboard-rows")){
            CScoreboard.Row row = scoreboard.addRow(s);
        }

        //change the row messages before the initial sending to the player

        scoreboard.setTitle(SkyBlock.getInstance().getUtils().color(title));
        scoreboard.finish();

        memoryPlayer.setScoreboard(scoreboard);
        memoryPlayer.getScoreboard().display(memoryPlayer.getPlayer());
    }

    public void updateBoardTimer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                PAPI = PluginHook.isEnabled("PlaceholderAPI");
                MVdW = PluginHook.isEnabled("MVdWPlaceholderAPI");

                for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList) {
                    updateBoard(memoryPlayer);
                }
            }
        }.runTaskTimer(SkyBlock.getInstance(), 0, SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getInt("scoreboard-update-time"));
    }

    public void updateBoard(MemoryPlayer memoryPlayer){
        String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");
            Player p = memoryPlayer.getPlayer();
            if (p == null) return;
            CScoreboard scoreboard = memoryPlayer.getScoreboard();
            if (scoreboard == null){
                createScoreBoard(memoryPlayer);
                scoreboard = memoryPlayer.getScoreboard();
            }
            for (CScoreboard.Row row: scoreboard.getRows()){
                String oldMessage = row.getOriginalMessage();
                oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);
                if (PAPI){
                    oldMessage = PlaceholderAPI.setPlaceholders(p, oldMessage);
                }
                if (MVdW){
                    oldMessage = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(p, oldMessage); // for MVdW
                }
                oldMessage = oldMessage.replace("%player%", p.getName());
                double bal = SkyBlock.getInstance().getUtils().getBalance(p.getUniqueId());
                oldMessage = oldMessage.replace("%money%", Utils.numberFormat.formatDbl(bal));
                if (memoryPlayer.getIsland() != null){
                    oldMessage = oldMessage.replace("%island%", memoryPlayer.getIsland().getName());
                    oldMessage = oldMessage.replace("%is-top%", memoryPlayer.getIsland().getTopPlace()+"");
                    oldMessage = oldMessage.replace("%is-worth%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getWorth()));
                    oldMessage = oldMessage.replace("%is-bank%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getBankBalance()));
                }else{
                    oldMessage = oldMessage.replace("%island%", none);
                    oldMessage = oldMessage.replace("%is-top%", none);
                    oldMessage = oldMessage.replace("%is-worth%", none);
                    oldMessage = oldMessage.replace("%is-bank%", none);
                }
                oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);

                if (oldMessage.length() > 25){
                    oldMessage = oldMessage.substring(0, Math.min(oldMessage.length(), 25));
                }

                row.setMessage(oldMessage);
            }
        }
    }