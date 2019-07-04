package org.savage.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.guis.*;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.events.IslandTeleportEvent;

public class IslandCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("is")){
                if (args.length == 0){
                    Panel.openPanel(p);
                }
                if (args.length == 1){

                    if (args[0].equalsIgnoreCase("version")) {
                        //version command is not configurable
                        String[] list = {"&a&lSavage SkyBlock", " ", "&bVersion: " + Bukkit.getPluginManager().getPlugin("SavageSkyBlock").getDescription().getVersion()};
                        for (String s : list) {
                            p.sendMessage(SkyBlock.getInstance().getUtils().color(s));
                        }
                    }

                    if (args[0].equalsIgnoreCase("worth")) {
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            for (String t : SkyBlock.getInstance().getUtils().getMessageList("is-worth")) {
                                p.sendMessage(Placeholder.convertPlaceholders(t, island));
                            }
                        } else {
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (args[0].equalsIgnoreCase("top")) {
                        ISTop.openISTop(p);
                    }

                    if (args[0].equalsIgnoreCase("perms")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                Protection.openProtectionMenu(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (args[0].equalsIgnoreCase("home")) {
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {

                            IslandTeleportEvent teleportEvent = new IslandTeleportEvent(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()), p.getUniqueId(), p.getLocation(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).getHome());
                            Bukkit.getPluginManager().callEvent(teleportEvent);

                            if (!teleportEvent.isCancelled()) {
                                // p.teleport(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).getHome());
                                p.teleport(teleportEvent.getTo());
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("teleportHome"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("spawn")){
                        if (SkyBlock.getInstance().getIslandUtils().getIslandSpawn() != null) {

                            IslandTeleportEvent teleportEvent = new IslandTeleportEvent(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()), p.getUniqueId(), p.getLocation(), SkyBlock.getInstance().getIslandUtils().getIslandSpawn());
                            Bukkit.getPluginManager().callEvent(teleportEvent);

                            if (!teleportEvent.isCancelled()) {
                                //  p.teleport(SkyBlock.getInstance().getIslandUtils().getIslandSpawn());
                                p.teleport(teleportEvent.getTo());
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("teleportSpawn"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noSpawn"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("help")){
                        for (String l : SkyBlock.getInstance().getUtils().colorList(SkyBlock.getInstance().getConfig().getStringList("messages.is-help"))) {
                            p.sendMessage(l);
                        }
                    }
                    if (args[0].equalsIgnoreCase("create")){
                        //check if they have an island already
                        if (!SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Islands.openIslands(p);
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("alreadyIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("biome")){
                        //check if they have an island already
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                Biomes.openBiome(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("delete")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            //check if owner
                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //open the delete gui
                                SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setDeleting(true);
                                DeleteIsland.openDelete(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (args[0].equalsIgnoreCase("sethome")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //set the home
                                SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setHome(p.getLocation());
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setHome"));
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                }
                if (args.length == 2){
                    //is kick <player>
                    String arg1 = args[0];
                    String arg2 = args[1];

                    if (arg1.equalsIgnoreCase("setbiome")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //check if arg2 is a valid biome name
                                if (Biome.valueOf(arg2) != null && !Biome.valueOf(arg2).name().equalsIgnoreCase("")) {
                                    //is valid
                                    SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setBiome(Biome.valueOf(arg2));
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setBiome"));
                                } else {
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("invalidBiome"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notOwner"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("join") || arg1.equalsIgnoreCase("accept")){
                        if (Bukkit.getPlayer(arg2) != null && Bukkit.getPlayer(arg2).isOnline()){
                            //join that player's island
                            Player target = Bukkit.getPlayer(arg2);
                            if (SkyBlock.getInstance().getIslandUtils().inIsland(target.getUniqueId())) {
                                //in an island, check the invites
                                Island island = SkyBlock.getInstance().getIslandUtils().getIsland(target.getUniqueId());
                                if (island.isInvited(p.getUniqueId())){
                                    //check if they're not in an island firstly
                                    if (!SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                                        if (island.join(p.getUniqueId())){
                                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("joined"));
                                        }else{
                                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("couldNotJoin"));
                                        }
                                    }else{
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("alreadyIsland"));
                                    }
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInvited"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("inviterNotInIsland"));
                            }
                        }
                    }
                    if (arg1.equalsIgnoreCase("promote") || arg1.equalsIgnoreCase("kick") || arg1.equalsIgnoreCase("demote") || arg1.equalsIgnoreCase("invite")){
                        if (Bukkit.getPlayer(arg2) != null && Bukkit.getPlayer(arg2).isOnline()) {
                            Player target = Bukkit.getPlayerExact(arg2);
                            //check for owner
                            if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                                //check for owner or officer
                                if (!SkyBlock.getInstance().getIslandUtils().isMember(p.getUniqueId(), SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                    //in an island, and is not member, must be owner or officer, let them run the commands
                                    Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                                    if (island != null) {
                                        if (arg1.equalsIgnoreCase("invite")){
                                            island.invite(p.getUniqueId(), target.getUniqueId());
                                        }
                                        if (arg1.equalsIgnoreCase("promote")) {
                                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), island)) {
                                                island.promote(target.getUniqueId());
                                            }else{
                                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                                            }
                                        }
                                        if (arg1.equalsIgnoreCase("demote")) {
                                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), island)) {
                                                island.demote(target.getUniqueId());
                                            }else{
                                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                                            }
                                        }
                                        if (arg1.equalsIgnoreCase("kick")){
                                            island.kick(p.getUniqueId(), target.getUniqueId());
                                        }
                                    }
                                } else {
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                                }
                            } else {
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPlayer"));
                        }
                    }
                }
            }
        }
        return false;
    }
}