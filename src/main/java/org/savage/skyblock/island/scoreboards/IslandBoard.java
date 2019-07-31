package org.savage.skyblock.island.scoreboards;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.*;
import org.savage.skyblock.island.MemoryPlayer;

public class IslandBoard {

    public boolean PAPI = PluginHook.isEnabled("PlaceholderAPI");

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

                for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList) {
                    updateBoard(memoryPlayer);
                }
            }
        }.runTaskTimer(SkyBlock.getInstance(), 0, SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getInt("scoreboard-update-time"));
    }

    public void updateBoard(MemoryPlayer memoryPlayer) {
        if (SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getBoolean("scoreboard-enabled")) {
            String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");
            Player p = memoryPlayer.getPlayer();
            if (p == null) return;
            CScoreboard scoreboard = memoryPlayer.getScoreboard();
            if (scoreboard == null) {
                createScoreBoard(memoryPlayer);
                scoreboard = memoryPlayer.getScoreboard();
            }
            for (CScoreboard.Row row : scoreboard.getRows()) {
                String oldMessage = row.getOriginalMessage();
                oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);

                if (PAPI) {
                    oldMessage = PlaceholderAPI.setPlaceholders(p, oldMessage);
                }

                oldMessage = oldMessage.replace("%player%", p.getName());
                double bal = SkyBlock.getInstance().getUtils().getBalance(p.getUniqueId());
                oldMessage = oldMessage.replace("%money%", Utils.numberFormat.formatDbl(bal));
                if (memoryPlayer.getIsland() != null) {
                    oldMessage = oldMessage.replace("%is-name%", memoryPlayer.getIsland().getName());
                    oldMessage = oldMessage.replace("%is-owner%", memoryPlayer.getIsland().getOwnersName());
                    oldMessage = oldMessage.replace("%is-top%", memoryPlayer.getIsland().getTopPlace() + "");
                    oldMessage = oldMessage.replace("%is-worth_total%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getWorth()));
                    oldMessage = oldMessage.replace("%is-worth_bank%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getBankBalance()));
                    oldMessage = oldMessage.replace("%is-worth_blocks%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getBlockWorth()));
                    oldMessage = oldMessage.replace("%is-worth_spawners%", Utils.numberFormat.formatDbl(memoryPlayer.getIsland().getSpawnerWorth()));
                } else {
                    oldMessage = oldMessage.replace("%is-name%", none);
                    oldMessage = oldMessage.replace("%is-owner%", none);
                    oldMessage = oldMessage.replace("%is-top%", none);
                    oldMessage = oldMessage.replace("%is-worth_total%", none);
                    oldMessage = oldMessage.replace("%is-worth_bank%", none);
                    oldMessage = oldMessage.replace("%is-worth_blocks%", none);
                    oldMessage = oldMessage.replace("%is-worth_spawners%", none);
                }
                oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);

                if (oldMessage.length() > 32) {
                    oldMessage = oldMessage.substring(0, Math.min(oldMessage.length(), 32));
                }

                row.setMessage(oldMessage);
            }
        }else{
            if (memoryPlayer.getScoreboard() != null){
                memoryPlayer.getScoreboard().remove(memoryPlayer.getPlayer());
            }
        }
    }
    }