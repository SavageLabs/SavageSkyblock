package org.savage.skyblock.island.scoreboards;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.*;
import org.savage.skyblock.island.MemoryPlayer;

public class IslandBoard {

    public boolean PAPI = PluginHook.isEnabled("PlaceholderAPI");

    public void createScoreBoard(MemoryPlayer memoryPlayer) {
        CScoreboard scoreboard = new CScoreboard("a", "b", "c", memoryPlayer);
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig();

        String title = f.getString("scoreboard-title");
        String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");
        Player p = memoryPlayer.getPlayer();

        for (String s : f.getStringList("scoreboard-rows")) {
            if (s.length() > 32) {
               // s = s.substring(0, Math.min(s.length(), 32));
            }
            CScoreboard.Row row = scoreboard.addRow(s);
        }

        //change the row messages before the initial sending to the player

        scoreboard.setTitle(SkyBlock.getInstance().getUtils().color(title));
        scoreboard.finish();

        memoryPlayer.setScoreboard(scoreboard);
        memoryPlayer.getScoreboard().display(memoryPlayer.getPlayer());
    }

    public void updateBoardTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                PAPI = PluginHook.isEnabled("PlaceholderAPI");
                boolean enabled = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getBoolean("scoreboard-enabled");

                if (enabled) {
                    for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList) {
                        if (memoryPlayer.getPlayer() != null && Storage.scoreboard_worlds.contains(memoryPlayer.getPlayer().getWorld().getName())) {
                            updateBoard(memoryPlayer);
                        }else{
                            memoryPlayer.getScoreboard().remove(memoryPlayer.getPlayer());
                            memoryPlayer.setScoreboard(null);
                        }
                    }
                }else{
                    for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList) {
                        if (memoryPlayer.getScoreboard() != null){
                            memoryPlayer.getScoreboard().remove(memoryPlayer.getPlayer());
                            memoryPlayer.setScoreboard(null);
                        }
                    }
                }
            }

        }.runTaskTimer(SkyBlock.getInstance(), 0, SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getInt("scoreboard-update-time"));
    }

    public void updateBoard(MemoryPlayer memoryPlayer) {
        if (memoryPlayer != null){
            if (memoryPlayer.getScoreboard() == null){
                createScoreBoard(memoryPlayer);
            }
            memoryPlayer.getScoreboard().update();
        }
    }

    public String convertPlaceholders(MemoryPlayer memoryPlayer, String oldMessage){
        Player p = memoryPlayer.getPlayer();
        String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");

        oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);

        if (SkyBlock.getInstance().getIslandBoard().PAPI) {
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
        return oldMessage;
    }
}