package me.saber.skyblock.commands;

import me.saber.skyblock.Island.Island;
import me.saber.skyblock.Island.events.IslandTeleportEvent;
import me.saber.skyblock.Main;
import me.saber.skyblock.guis.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player){
            Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("is")){
                if (args.length == 0){
                    Panel.openPanel(p);
                }
                if (args.length == 1){

                    if (args[0].equalsIgnoreCase("perms")){
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))){
                                Protection.openProtectionMenu(p);
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (args[0].equalsIgnoreCase("home")) {
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){

                            IslandTeleportEvent teleportEvent = new IslandTeleportEvent(Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()), p.getUniqueId(), p.getLocation(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).getHome());
                            Bukkit.getPluginManager().callEvent(teleportEvent);

                            if (!teleportEvent.isCancelled()) {
                               // p.teleport(Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).getHome());
                                p.teleport(teleportEvent.getTo());
                                p.sendMessage(Main.getInstance().getUtils().getMessage("teleportHome"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("spawn")){
                        if (Main.getInstance().getIslandUtils().getIslandSpawn() != null) {

                            IslandTeleportEvent teleportEvent = new IslandTeleportEvent(Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()), p.getUniqueId(), p.getLocation(), Main.getInstance().getIslandUtils().getIslandSpawn());
                            Bukkit.getPluginManager().callEvent(teleportEvent);

                            if (!teleportEvent.isCancelled()) {
                              //  p.teleport(Main.getInstance().getIslandUtils().getIslandSpawn());
                                p.teleport(teleportEvent.getTo());
                                p.sendMessage(Main.getInstance().getUtils().getMessage("teleportSpawn"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("noSpawn"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("help")){
                        for (String l : Main.getInstance().getUtils().colorList(Main.getInstance().getConfig().getStringList("messages.is-help"))){
                            p.sendMessage(l);
                        }
                    }
                    if (args[0].equalsIgnoreCase("create")){
                        //check if they have an island already
                        if (!Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                            Islands.openIslands(p);
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("alreadyIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("biome")){
                        //check if they have an island already
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                            if(Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(),Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                Biomes.openBiome(p);
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("delete")){
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                            //check if owner
                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))){
                                //open the delete gui
                                Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setDeleting(true);
                                DeleteIsland.openDelete(p);
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("sethome")){
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))){
                                //set the home
                                Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setHome(p.getLocation());
                                p.sendMessage(Main.getInstance().getUtils().getMessage("setHome"));
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                }
                if (args.length == 2){
                    //is kick <player>
                    String arg1 = args[0];
                    String arg2 = args[1];

                    if (arg1.equalsIgnoreCase("setbiome")){
                        if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //check if arg2 is a valid biome name
                                if (Biome.valueOf(arg2) != null && !Biome.valueOf(arg2).name().equalsIgnoreCase("")) {
                                    //is valid
                                    Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setBiome(Biome.valueOf(arg2));
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("setBiome"));
                                } else {
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("invalidBiome"));
                                }
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("join") || arg1.equalsIgnoreCase("accept")){
                        if (Bukkit.getPlayer(arg2) != null && Bukkit.getPlayer(arg2).isOnline()){
                            //join that player's island
                            Player target = Bukkit.getPlayer(arg2);
                            if (Main.getInstance().getIslandUtils().inIsland(target.getUniqueId())){
                                //in an island, check the invites
                                Island island = Main.getInstance().getIslandUtils().getIsland(target.getUniqueId());
                                if (island.isInvited(p.getUniqueId())){
                                    //check if they're not in an island firstly
                                    if (!Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())){
                                        if (island.join(p.getUniqueId())){
                                            p.sendMessage(Main.getInstance().getUtils().getMessage("joined"));
                                        }else{
                                            p.sendMessage(Main.getInstance().getUtils().getMessage("couldNotJoin"));
                                        }
                                    }else{
                                        p.sendMessage(Main.getInstance().getUtils().getMessage("alreadyIsland"));
                                    }
                                }else{
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("notInvited"));
                                }
                            }else{
                                p.sendMessage(Main.getInstance().getUtils().getMessage("inviterNotInIsland"));
                            }
                        }
                    }
                    if (arg1.equalsIgnoreCase("promote") || arg1.equalsIgnoreCase("kick") || arg1.equalsIgnoreCase("demote") || arg1.equalsIgnoreCase("invite")){
                        if (Bukkit.getPlayer(arg2) != null && Bukkit.getPlayer(arg2).isOnline()) {
                            Player target = Bukkit.getPlayerExact(arg2);
                            //check for owner
                            if (Main.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                                //check for owner or officer
                                if (!Main.getInstance().getIslandUtils().isMember(p.getUniqueId(), Main.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                    //in an island, and is not member, must be owner or officer, let them run the commands
                                    Island island = Main.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                                    if (island != null) {
                                        if (arg1.equalsIgnoreCase("invite")){
                                            island.invite(p.getUniqueId(), target.getUniqueId());
                                        }
                                        if (arg1.equalsIgnoreCase("promote")) {
                                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), island)){
                                                island.promote(target.getUniqueId());
                                            }else{
                                                p.sendMessage(Main.getInstance().getUtils().getMessage("noPermissionIsland"));
                                            }
                                        }
                                        if (arg1.equalsIgnoreCase("demote")) {
                                            if (Main.getInstance().getIslandUtils().isOwner(p.getUniqueId(), island)){
                                                island.demote(target.getUniqueId());
                                            }else{
                                                p.sendMessage(Main.getInstance().getUtils().getMessage("noPermissionIsland"));
                                            }
                                        }
                                        if (arg1.equalsIgnoreCase("kick")){
                                            island.kick(p.getUniqueId(), target.getUniqueId());
                                        }
                                    }
                                } else {
                                    p.sendMessage(Main.getInstance().getUtils().getMessage("noPermissionIsland"));
                                }
                            } else {
                                p.sendMessage(Main.getInstance().getUtils().getMessage("notInIsland"));
                            }
                        }else{
                            p.sendMessage(Main.getInstance().getUtils().getMessage("noPlayer"));
                        }
                    }
                }
            }
        }
        return false;
    }
}