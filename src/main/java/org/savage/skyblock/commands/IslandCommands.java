package org.savage.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.savage.skyblock.API.IslandTeleportEvent;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.PluginHook;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.guis.*;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.permissions.Perm;
import org.savage.skyblock.island.permissions.PermissionUI;
import org.savage.skyblock.island.quests.QuestUI;
import org.savage.skyblock.island.permissions.Role;
import org.savage.skyblock.island.rules.Rule;
import org.savage.skyblock.island.rules.RuleUI;
import org.savage.skyblock.island.warps.IslandWarp;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.upgrades.Upgrade;
import org.savage.skyblock.island.upgrades.UpgradesUI;
import org.savage.skyblock.island.warps.WarpUI;

public class IslandCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("is")){
                if (args.length == 0){
                    if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()) != null){
                        Panel.openPanel(p);
                    }else{
                        Islands.openIslands(p);
                    }
                }
                if (args.length == 1){

                    String arg1 = args[0];

                    if (arg1.equalsIgnoreCase("version")) {
                        //version command is not configurable
                        String[] list = {"&a&lSavage SkyBlock", " ", "&bVersion: " + Bukkit.getPluginManager().getPlugin("SavageSkyBlock").getDescription().getVersion(), "&bBy: Trent™", " "};
                        for (String s : list) {
                            p.sendMessage(SkyBlock.getInstance().getUtils().color(s));
                        }
                    }

                    if (arg1.equalsIgnoreCase("leave") || arg1.equalsIgnoreCase("quit")){
                        //let them leave the island
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            if (!island.getOwnerUUID().equals(p.getUniqueId())){
                                //is not owner, then we can leave freely
                                island.leave(p.getUniqueId());
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("isOwner"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("quests") || arg1.equalsIgnoreCase("quest")){
                        QuestUI.openQuestMenuUI(p);
                    }

                    if (arg1.equalsIgnoreCase("warps")){
                        //open GUI of the island warps
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            WarpUI.openWarpUI(p);
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("inspect")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation());
                        if (island != null){
                            if (island.equals(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                //check if the player has permission to check the inspect
                                //inspect it now
                                MemoryPlayer memoryPlayer = SkyBlock.getInstance().getUtils().getMemoryPlayer(p.getUniqueId());
                                if (memoryPlayer == null) return false;

                                if (!PluginHook.isEnabled("CoreProtect")){
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-inspect-disabled"));
                                    return false;
                                }

                                if (memoryPlayer.isInspectMode()) {
                                    memoryPlayer.setInspectMode(false);
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-inspect-mode-off"));
                                } else {
                                    memoryPlayer.setInspectMode(true);
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-inspect-mode-on"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIslandHere"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("balance")){
                        //send the island balance
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("bank-balance").replace("%balance%", SkyBlock.getInstance().getUtils().formatNumber(island.getBankBalance()+"")));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("bank")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island != null){
                            p.openInventory(island.getBank());
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-bank-open"));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("upgrades")){
                        if (SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()) != null){
                            UpgradesUI.openUpgradesUI(p);
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("worth")) {
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            for (String t : SkyBlock.getInstance().getUtils().getMessageList("is-worth")) {
                                p.sendMessage(Placeholder.convertPlaceholders(t, island));
                            }
                        } else {
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("top")) {
                        ISTop.openISTop(p);
                    }

                    if (arg1.equalsIgnoreCase("rules")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
                                RuleUI.openRulesUI(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("perms")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
                                PermissionUI.openPermissionUI(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("home") || arg1.equalsIgnoreCase("go")) {
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
                    if (arg1.equalsIgnoreCase("spawn")){
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
                    if (arg1.equalsIgnoreCase("help")){
                        for (String l : SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getMessages().getFileConfig().getStringList("messages.is-help"))) {
                            p.sendMessage(l);
                        }
                    }
                    if (arg1.equalsIgnoreCase("create")){
                        //check if they have an island already
                        if (!SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Islands.openIslands(p);
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("alreadyIsland"));
                        }
                    }
                    if (arg1.equalsIgnoreCase("biome")){
                        //check if they have an island already
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
                                Biomes.openBiome(p);
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                        }
                    }
                    if (arg1.equalsIgnoreCase("delete")){
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
                    if (arg1.equalsIgnoreCase("sethome")){
                        if (SkyBlock.getInstance().getIslandUtils().inIsland(p.getUniqueId())) {
                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
                                if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation()).equals(SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()))) {
                                    SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setHome(p.getLocation());
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setHome"));
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notYourIsland"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
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
                    //is warp <name>
                    //is createWarp|setWarp <name>
                    //is teleport <player>
                    String arg1 = args[0];
                    String arg2 = args[1];

                    if (arg1.equalsIgnoreCase("teleport")){
                        //check if the name is either an island's name, or an online player's name
                        Island island = SkyBlock.getInstance().getIslandUtils().getIslandFromName(arg2);
                        if (island == null){
                            Player target = Bukkit.getPlayerExact(arg2);
                            if (target != null) {
                                if (SkyBlock.getInstance().getIslandUtils().getIsland(target.getUniqueId()) != null){
                                    island = SkyBlock.getInstance().getIslandUtils().getIsland(target.getUniqueId());
                                }
                            }
                        }
                        if (island == null){
                            //island-not-exist
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-not-exist"));
                            return false;
                        }else{
                            //check the island's rule
                            if (island.hasRule(Rule.ISLAND_LOCK)){
                                //island is locked
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-locked"));
                                return false;
                            }else{
                                //teleport them
                                p.teleport(island.getHome());
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-teleport").replace("%island%", island.getName()));
                            }
                        }
                    }

                    if (arg1.equalsIgnoreCase("removeWarp") || arg1.equalsIgnoreCase("deleteWarp") || arg1.equalsIgnoreCase("delWarp")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island == null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            return false;
                        }

                        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                        if (island.hasPerm(role, Perm.DELETE_WARP)){
                            if (SkyBlock.getInstance().getIslandUtils().getIslandWarp(island, arg2) != null) {
                                //has a warp named this name
                                //remove it now
                                IslandWarp islandWarp = SkyBlock.getInstance().getIslandUtils().getIslandWarp(island, arg2);
                                if (islandWarp != null){
                                    island.removeIslandWarp(islandWarp);
                                    islandWarp.remove();
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-removed").replace("%name%", arg2));
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-none"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-none"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("createWarp") || arg1.equalsIgnoreCase("setWarp")) {
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island == null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            return false;
                        }

                        if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(p.getLocation()).equals(island)){

                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.SET_WARP)){
                                if (SkyBlock.getInstance().getIslandUtils().getIslandWarp(island, arg2) == null){
                                    //there isn't a warp with this name... allow
                                    //check their upgrade value for island warps before allowing them to create a new one
                                    int currentWarps = island.getIslandWarps().size() + 1;
                                    if (Upgrade.Upgrades.getTierValue(Upgrade.WARP_AMOUNT, island.getUpgradeTier(Upgrade.WARP_AMOUNT), null) >= currentWarps){
                                        //allow them to create it
                                        IslandWarp islandWarp = new IslandWarp(island, arg2, p.getLocation());
                                        island.addIslandWarp(islandWarp);
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-created").replace("%name%", arg2));
                                    }else{
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-max"));
                                    }
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-exists"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                            }
                        }else{
                            //noPermissionIsland
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                        }
                    }

                    if (arg1.equalsIgnoreCase("warp")){
                        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                        if (island == null){
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                            return false;
                        }

                        IslandWarp islandWarp = SkyBlock.getInstance().getIslandUtils().getIslandWarp(island, arg2);
                        if (islandWarp != null){
                            //teleport
                            p.teleport(islandWarp.getLocation());
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-teleported").replace("%name%", arg2));
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-warp-none"));
                        }
                    }

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

                        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                        if (island.hasPerm(role, Perm.BANK_WITHDRAW)){
                            double money = Double.parseDouble(arg2);
                            if (island.getBankBalance() >= money){
                                //remove the money
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("withdraw-yes").replace("%money%", SkyBlock.getInstance().getUtils().formatNumber(money+"")));
                                SkyBlock.getInstance().getUtils().addMoney(p.getUniqueId(), money);
                                island.setBankBalance((island.getBankBalance() - money));
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("withdraw-no"));
                            }
                        }else{
                            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
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
                                    Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                                    if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
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

                            Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());
                            if (island == null){
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("notInIsland"));
                                return false;
                            }

                            Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                            if (island.hasPerm(role, Perm.MANAGE_PERMISSIONS)){
                                if (Biome.valueOf(arg2) != null && !Biome.valueOf(arg2).name().equalsIgnoreCase("")) {
                                    //is valid
                                    SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId()).setBiome(Biome.valueOf(arg2));
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("setBiome"));
                                } else {
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("invalidBiome"));
                                }
                            }else{
                                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
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
                                        Role role = SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island);
                                        if (role == null) return false;

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