package org.savage.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.savage.skyblock.API.IslandTeleportEvent;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.guis.*;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.upgrades.UpgradesUI;

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
                        String[] list = {"&a&lSavage SkyBlock", " ", "&bVersion: " + Bukkit.getPluginManager().getPlugin("SavageSkyBlock").getDescription().getVersion(), "&bBy: Trent™", " "};
                        for (String s : list) {
                            p.sendMessage(SkyBlock.getInstance().getUtils().color(s));
                        }
                    }

                    if (args[0].equalsIgnoreCase("balance")){
                        //send the island balance
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("bank-balance").replace("%balance%", island.getBankBalance()+""));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (args[0].equalsIgnoreCase("bank")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            p.openInventory(island.getBank());
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-bank-open"));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (args[0].equalsIgnoreCase("upgrades")){
                        if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()) != null){
                            UpgradesUI.openUpgradesUI(p);
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
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
                            if (SkyBlock.getInstance().getIslandUtils().hasAdminPermissions(p, SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
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
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

                            IslandTeleportEvent teleportEvent = new IslandTeleportEvent(island, p.getUniqueId(), p.getLocation(), island.getHome());
                            Bukkit.getPluginManager().callEvent(teleportEvent);

                            if (!teleportEvent.isCancelled()) {
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
                        for (String l : SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getMessages().getFileConfig().getStringList("messages.is-help"))) {
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
                            if (SkyBlock.getInstance().getIslandUtils().hasAdminPermissions(p, SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
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
                            if (SkyBlock.getInstance().getIslandUtils().hasAdminPermissions(p, SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //set the home
                                if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation()).equals(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                    SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setHome(p.getLocation());
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setHome"));
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYourIsland"));
                                }
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
                    //is setname <name>
                    //is transfer <player>
                    String arg1 = args[0];
                    String arg2 = args[1];

                    if (arg1.equalsIgnoreCase("deposit")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island == null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            return false;
                        }
                        double money = Double.parseDouble(arg2);
                        if (SkyBlock.getInstance().getUtils().getBalance(p.getUniqueId()) >= money){
                            //remove the money
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("deposit-yes").replace("%money%", SkyBlock.getInstance().getUtils().formatNumber(money+"")));
                            SkyBlock.getInstance().getUtils().takeMoney(p.getUniqueId(), money);
                            island.setBankBalance((island.getBankBalance() + money));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("deposit-no"));
                        }
                    }
                    if (arg1.equalsIgnoreCase("withdraw")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island == null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            return false;
                        }
                        double money = Double.parseDouble(arg2);
                        if (island.getBankBalance() >= money){
                            //remove the money
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("withdraw-yes").replace("%money%", SkyBlock.getInstance().getUtils().formatNumber(money+"")));
                            SkyBlock.getInstance().getUtils().addMoney(p.getUniqueId(), money);
                            island.setBankBalance((island.getBankBalance() - money));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("withdraw-no"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("transfer")){
                        //transferring ownership
                        Player targetPlayer = Bukkit.getPlayerExact(arg2);
                        if (targetPlayer != null && targetPlayer.isOnline()){
                            //check for their island
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            if (island != null){
                                if (island.getOwnerUUID().equals(p.getUniqueId())){
                                    //if they are owner, let them transfer island ownership to the specified person
                                    if (SkyBlock.getInstance().getIslandUtils().getIsland(targetPlayer.getUniqueId()) == island){
                                        //in the same island, change it now
                                        island.setOwnerUUID(targetPlayer.getUniqueId());
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedOwner").replace("%player%", targetPlayer.getName()));
                                        targetPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedOwnerMe"));
                                        island.addCoOwner(p.getUniqueId());

                                        island.removeCoOwner(targetPlayer.getUniqueId());
                                        island.removeOfficer(targetPlayer.getUniqueId());
                                        island.removeMember(targetPlayer.getUniqueId());
                                    }else{
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedNoPlayer"));
                                    }
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                                }
                            }
                        }
                    }

                    if (arg1.equalsIgnoreCase("setname") || arg1.equalsIgnoreCase("name") || arg1.equalsIgnoreCase("rename")){
                        if (arg2 != null && !arg2.equalsIgnoreCase("")){
                            //check if the island's name isn't already taken...
                            if (!SkyBlock.getInstance().getIslandUtils().isIslandName(arg2)){
                                Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                                if (island != null){
                                    if (SkyBlock.getInstance().getIslandUtils().hasAdminPermissions(p, island)){
                                        island.setName(arg2);
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-name-set").replace("%name%", arg2));
                                    }else{
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                                    }
                                }else{
                                    //not in an island
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                                }
                            }else{
                                //taken
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-name-taken"));
                            }
                        }
                    }

                    if (arg1.equalsIgnoreCase("setbiome")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            if (SkyBlock.getInstance().getIslandUtils().hasAdminPermissions(p, SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
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
                                            island.promote(p.getUniqueId(), target.getUniqueId());
                                        }
                                        if (arg1.equalsIgnoreCase("demote")) {
                                            if (SkyBlock.getInstance().getIslandUtils().isOwner(p.getUniqueId(), island)) {
                                                island.demote(p.getUniqueId(), target.getUniqueId());
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