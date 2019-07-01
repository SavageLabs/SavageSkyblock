package me.saber.skyblock.commands;

import me.saber.skyblock.Main;
import me.saber.skyblock.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBPCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("sbp")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (p.hasPermission("sbp.admin")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")){
                            Main.getInstance().reloadConfig();
                            Main.getInstance().getFileManager().dataFileCustom.loadFile(); //todo; might have to remove this if it overrides data saves
                            p.sendMessage(Main.getInstance().getUtils().getMessage("reload"));
                        }
                        if (args[0].equalsIgnoreCase("setspawn")) {
                            Main.getInstance().getConfig().set("settings.island-spawn", Main.getInstance().getUtils().serializeLocation(p.getLocation()));
                            Main.getInstance().saveConfig();
                            p.sendMessage(Main.getInstance().getUtils().getMessage("setSpawn"));
                        }
                    }

                    if (args.length == 2){
                        //- /sbp delete <player>
                        if (args[0].equalsIgnoreCase("delete")){
                            if (Bukkit.getPlayerExact(args[1]) != null){
                                //got the player
                                //check if the player has an island
                                Player target = Bukkit.getPlayerExact(args[1]);

                                if (Main.getInstance().getIslandUtils().getIsland(target.getUniqueId()) != null){
                                    Island island = Main.getInstance().getIslandUtils().getIsland(target.getUniqueId());
                                    island.delete();
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("forceDeleteDeleter"));
                                    target.sendMessage(Main.getInstance().getUtils().getMessage("forceDelete"));
                                }else{
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("noIsland"));
                                }

                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("noPlayer"));
                            }
                        }
                    }

                }else{
                    p.sendMessage(Main.getInstance().getUtils().getMessage("noPermission"));
                }
            }
        }
        return false;
    }
}