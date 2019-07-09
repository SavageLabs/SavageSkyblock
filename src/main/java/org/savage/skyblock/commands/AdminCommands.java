package org.savage.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.Island;

public class AdminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("isa")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (p.hasPermission("isa.admin")) {

                    if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                        for (String l : SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getConfig().getStringList("messages.isa-help"))) {
                            p.sendMessage(l);
                        }
                    }

                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")){
                            SkyBlock.getInstance().reloadConfig();
                            SkyBlock.getInstance().getFileManager().getData().loadFile(); //todo; might have to remove this if it overrides data saves
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("reload"));

                            Bukkit.getScheduler().cancelTasks(SkyBlock.getInstance());

                            for (Island island : Storage.islandList) {
                                SkyBlock.getInstance().getIslandUtils().calculateIslandLevel(island);
                            }

                            SkyBlock.getInstance().startTopTimer();
                            SkyBlock.getInstance().startCalculationTimer();
                            SkyBlock.getInstance().startCacheTimer();

                        }
                        if (args[0].equalsIgnoreCase("setspawn")) {
                            SkyBlock.getInstance().getConfig().set("settings.island-spawn", SkyBlock.getInstance().getUtils().serializeLocation(p.getLocation()));
                            SkyBlock.getInstance().saveConfig();
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setSpawn"));
                        }
                    }

                    if (args.length == 2){
                        //- /sbp delete <player>
                        if (args[0].equalsIgnoreCase("delete")){
                            if (Bukkit.getPlayerExact(args[1]) != null){
                                //got the player
                                //check if the player has an island
                                Player target = Bukkit.getPlayerExact(args[1]);

                                if (SkyBlock.getInstance().getIslandUtils().getIsland(target.getUniqueId()) != null) {
                                    Island island = SkyBlock.getInstance().getIslandUtils().getIsland(target.getUniqueId());
                                    island.delete();
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("forceDeleteDeleter"));
                                    target.sendMessage(SkyBlock.getInstance().getUtils().getMessage("forceDelete"));
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIslandArg"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPlayer"));
                            }
                        }
                    }
                }else{
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermission"));
                }
            }
        }
        return false;
    }
}