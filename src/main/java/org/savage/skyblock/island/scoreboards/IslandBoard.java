package org.savage.skyblock.island.scoreboards;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.*;
import org.savage.skyblock.island.MemoryPlayer;

public class IslandBoard {


    public void createScoreBoard(MemoryPlayer memoryPlayer){
        CScoreboard scoreboard = new CScoreboard("a", "b", "c");
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig();

        String title = f.getString("scoreboard-title");

        for (String s : f.getStringList("scoreboard-rows")){
            CScoreboard.Row row = scoreboard.addRow(s);
        }

        scoreboard.setTitle(SkyBlock.getInstance().getUtils().color(title));

        scoreboard.finish();

        memoryPlayer.setScoreboard(scoreboard);
        memoryPlayer.getScoreboard().display(memoryPlayer.getPlayer());
    }

    public void updateBoard(){
        boolean PAPI = PluginHook.isEnabled("PlaceholderAPI");
        boolean MVdW = PluginHook.isEnabled("MVdWPlaceholderAPI");
        new BukkitRunnable() {
            @Override
            public void run() {
                String none = SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getString("placeholders.none");
                for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList){
                    Player p = memoryPlayer.getPlayer();
                    if (p == null) continue;
                    CScoreboard scoreboard = memoryPlayer.getScoreboard();
                    if (scoreboard == null){
                        createScoreBoard(memoryPlayer);
                        scoreboard = memoryPlayer.getScoreboard();
                    }
                    for (CScoreboard.Row row: scoreboard.getRows()){
                        String oldMessage = row.getOriginalMessage();
                       // Bukkit.broadcastMessage("old: "+oldMessage);

                        oldMessage = SkyBlock.getInstance().getUtils().color(oldMessage);

                        if (PAPI){
                            //Bukkit.broadcastMessage("old1:" +oldMessage);
                            oldMessage = PlaceholderAPI.setPlaceholders(p, oldMessage);
                           // Bukkit.broadcastMessage("old2:" +oldMessage);
                        }

                        if (MVdW){
                            oldMessage = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(p, oldMessage); // for MVdW
                           // Bukkit.broadcastMessage("Mvd: "+oldMessage);
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

                        String ver = SkyBlock.getInstance().getReflectionManager().nmsHandler.getVersion();

                        if (ver.contains("1_8") || ver.contains("1_9") || ver.contains("1_10") || ver.contains("1_11") || ver.contains("1_12")){
                            if (oldMessage.length() > 16){
                                oldMessage = oldMessage.substring(0, Math.min(oldMessage.length(), 16));
                            }
                        }else{
                            if (oldMessage.length() > 25){
                                oldMessage = oldMessage.substring(0, Math.min(oldMessage.length(), 25));
                            }
                        }

                        row.setMessage(oldMessage);
                    }
                }
            }
        }.runTaskTimer(SkyBlock.getInstance(), 0, SkyBlock.getInstance().getFileManager().getScoreboard().getFileConfig().getInt("scoreboard-update-time"));
    }
}