package me.saber.skyblock.island;

import me.saber.skyblock.island.events.*;
import me.saber.skyblock.Main;
import me.saber.skyblock.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Island {

    private Island islandInstance;

    private double centerX;
    private double centerY;
    private double centerZ;

    private int protectionRadius;

    private UUID ownerUUID;
    private List<UUID> memberList;
    private List<UUID> officerList;
    private boolean open = false; // meaning people can join anytime
    private boolean deleting = false;
    private Location home;
    private Biome biome = Biome.valueOf(Main.getInstance().getUtils().getSettingString("default-biome"));

    private List<UUID> invites = new ArrayList<>();

    private boolean permissionMemberPlace = true;
    private boolean permissionMemberBreak = true;
    private boolean permissionMemberInteract = true;

    private boolean permissionOfficerPlace = true;
    private boolean permissionOfficerBreak = true;
    private boolean permissionOfficerInteract = true;

    private int hopperCount = 0;
    private int spawnerCount = 0;

    private double level = 0;


    public Island(String schematic, double x, double y, double z, UUID ownerUUID, List<UUID> memberList, List<UUID> officerList, int protectionRadius){
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.ownerUUID = ownerUUID;
        this.memberList = memberList;
        this.officerList = officerList;
        this.protectionRadius = protectionRadius;
        this.home = getLocation();

        islandInstance = this;

        Storage.islandList.add(getIslandInstance());
        Main.getInstance().getUtils().log("Loaded island");

        if (!schematic.equalsIgnoreCase("")){
            //generate the island schematic
            Main.getInstance().getWorldGenerator().pasteSchem(getLocation(), schematic);
            Bukkit.getPlayer(getOwnerUUID()).teleport(getLocation());
        }
    }

    public double getLevel() {
        return level;
    }

    public void addLevel(double toAdd){
        this.level = getLevel() + toAdd;
    }

    public void setLevel(double level){
        this.level = level;
    }

    public boolean isBlockInIsland(int x, int z) {
        Location loc1 = getLocation().add(getProtectionRadius(), 0, getProtectionRadius());
        Location loc2 = getLocation().subtract(getProtectionRadius(), 0, getProtectionRadius());
        if (x <= loc1.getX() && x >= loc2.getX()) {
            if (z <= loc1.getX() && z >= loc2.getX()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPlayersInIsland(){
        List<UUID> uuids = new ArrayList<>();
        uuids.add(getOwnerUUID());
        uuids.addAll(getOfficerList());
        uuids.addAll(getMemberList());
        for (UUID uuid : uuids){
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()){
                if (Main.getInstance().getIslandUtils().getIslandFromLocation(Bukkit.getPlayer(uuid).getLocation()) != null) {
                    if (Main.getInstance().getIslandUtils().getIslandFromLocation(Bukkit.getPlayer(uuid).getLocation()).equals(getIslandInstance())) {
                        //in the island
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void unload(){
        //unload the island data from ram, no deleting anything
        Storage.islandList.remove(getIslandInstance());
        this.islandInstance = null;
    }

    public boolean join(UUID joiner){
        if (!getMemberList().contains(joiner) && !getOfficerList().contains(joiner)){

            IslandJoinEvent joinEvent = new IslandJoinEvent(getIslandInstance(), joiner);
            Bukkit.getPluginManager().callEvent(joinEvent);
            if (!joinEvent.isCancelled()) {
                memberList.add(joiner);
                removeInvite(joiner);
                return true;
            }
        }
        return false;
    }

    public void invite(UUID inviter, UUID target){
        Player p = Bukkit.getPlayer(inviter);
        if (!isInvited(target)){

            IslandInviteEvent inviteEvent = new IslandInviteEvent(getIslandInstance(), inviter, target);
            Bukkit.getPluginManager().callEvent(inviteEvent);

            if (!inviteEvent.isCancelled()) {

                if (inviter.equals(target)) {
                    p.sendMessage(Main.getInstance().getUtils().getMessage("cannotPromoteDemoteSelf"));
                    return;
                }

                addInvite(target);
                Bukkit.getPlayer(target).sendMessage(Main.getInstance().getUtils().getMessage("invited").replace("%player%", p.getName()));
                p.sendMessage(Main.getInstance().getUtils().getMessage("invitedMe").replace("%player%", Bukkit.getPlayer(target).getName()));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (isInvited(target)) {
                            //still invited,
                            removeInvite(target);
                            if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()) {
                                Bukkit.getPlayer(target).sendMessage(Main.getInstance().getUtils().getMessage("inviteExpire").replace("%player%", p.getName()));
                            }
                        }
                    }
                }.runTaskLater(Main.getInstance(), Main.getInstance().getUtils().getSettingInt("inviteExpireTime") * 20);
            }
        }else{
            p.sendMessage(Main.getInstance().getUtils().getMessage("alreadyInvited"));
        }
    }

    public void kick(UUID kicker, UUID target) {
        IslandKickEvent kickEvent = new IslandKickEvent(getIslandInstance(), kicker, target);
        Bukkit.getPluginManager().callEvent(kickEvent);

        if (!kickEvent.isCancelled()) {
            if (getMemberList().contains(target) || getOfficerList().contains(target) || getOwnerUUID().equals(target)){
                if (getOwnerUUID().equals(kicker) || getOfficerList().contains(kicker)) {
                    if (kicker.equals(target)){
                        Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("kickedYourself"));
                        return;
                    }
                    if (getOfficerList().contains(target) || getOwnerUUID().equals(target)) {
                        if (getOfficerList().contains(kicker)) {
                            //can't do it
                            Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("noPermissionKick"));
                        }else if (getOwnerUUID().equals(kicker)){
                            //let owner kick them
                            Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("kicked").replace("%player%", Bukkit.getOfflinePlayer(target).getName()));
                            if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()){
                                Bukkit.getPlayer(target).sendMessage(Main.getInstance().getUtils().getMessage("kickedMe"));
                            }
                            officerList.remove(target);
                        }
                    }else{
                        //let officer kick them
                        Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("kicked").replace("%player%", Bukkit.getOfflinePlayer(target).getName()));
                        if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()){
                            Bukkit.getPlayer(target).sendMessage(Main.getInstance().getUtils().getMessage("kickedMe"));
                        }
                        memberList.remove(target);
                    }
                }else{
                    Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("noPermissionKick"));
                }
            }else{
                Bukkit.getPlayer(kicker).sendMessage(Main.getInstance().getUtils().getMessage("kickedNoPlayer"));
            }
        }
    }

    public void delete(){
        Location islandSpawn = Main.getInstance().getIslandUtils().getIslandSpawn();
        if (islandSpawn != null) {
            IslandDeleteEvent deleteEvent = new IslandDeleteEvent(getIslandInstance(), getOwnerUUID());
            Bukkit.getPluginManager().callEvent(deleteEvent);

            if (!deleteEvent.isCancelled()) {

                int radius = getProtectionRadius();

                Bukkit.getPlayer(getOwnerUUID()).teleport(Main.getInstance().getIslandUtils().getIslandSpawn());

                List<UUID> uuids = new ArrayList<>();
                uuids.addAll(getMemberList());
                uuids.addAll(getOfficerList());

                for (UUID uuid : uuids) {
                    //teleport them all and send a message
                    if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(Main.getInstance().getUtils().getMessage("deletedIsland"));
                        p.teleport(Main.getInstance().getIslandUtils().getIslandSpawn());
                    }
                }

                List<Block> blocks = Main.getInstance().getUtils().getNearbyBlocks(getLocation(), radius);

                for (Block block : blocks) {
                    if (block != null && !block.getType().equals(Material.AIR)) {
                        block.setType(Material.AIR);
                    }
                }
                Bukkit.getPlayer(getOwnerUUID()).sendMessage(Main.getInstance().getUtils().getMessage("deleteIsland"));

                unload();
            }
        }else{
            Bukkit.getPlayer(getOwnerUUID()).sendMessage(Main.getInstance().getUtils().getMessage("noSpawn"));
        }
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld("skyBlock"), getCenterX(), getCenterY(), getCenterZ());
    }

    public void demote(UUID uuid){
        Player owner = Bukkit.getPlayer(getOwnerUUID());
        Player target = null;
        if (Bukkit.getOfflinePlayer(uuid).isOnline()){
            target = Bukkit.getPlayer(uuid);
        }

        IslandDemoteEvent demoteEvent = new IslandDemoteEvent(getIslandInstance(), getOwnerUUID(), uuid);
        Bukkit.getPluginManager().callEvent(demoteEvent);

        if (!demoteEvent.isCancelled()) {

            if (uuid.equals(getOwnerUUID())) {
                Bukkit.getPlayer(getOwnerUUID()).sendMessage(Main.getInstance().getUtils().getMessage("cannotPromoteDemoteSelf"));
                return;
            }

            if (owner != null && owner.isOnline()){
                if (getMemberList().contains(uuid)){
                    owner.sendMessage(Main.getInstance().getUtils().getMessage("demotedNo"));
                }else{
                    if (getOfficerList().contains(uuid)){
                        officerList.remove(uuid);
                        memberList.add(uuid);
                        owner.sendMessage(Main.getInstance().getUtils().getMessage("demotedMember").replace("%player%", Bukkit.getOfflinePlayer(uuid).getName()));
                        if (target != null) {
                            target.sendMessage(Main.getInstance().getUtils().getMessage("demotedMemberMe"));
                        }
                    }else{
                        owner.sendMessage(Main.getInstance().getUtils().getMessage("demotedNoPlayer"));
                    }
                }
            }
        }
    }

    public void promote(UUID uuid){
        Player owner = Bukkit.getPlayer(getOwnerUUID());
        Player target = null;
        if (Bukkit.getOfflinePlayer(uuid).isOnline()){
            target = Bukkit.getPlayer(uuid);
        }

        IslandPromoteEvent promoteEvent = new IslandPromoteEvent(getIslandInstance(), getOwnerUUID(), uuid);
        Bukkit.getPluginManager().callEvent(promoteEvent);

        if (!promoteEvent.isCancelled()) {

            if (uuid.equals(getOwnerUUID())) {
                Bukkit.getPlayer(getOwnerUUID()).sendMessage(Main.getInstance().getUtils().getMessage("cannotPromoteDemoteSelf"));
                return;
            }

            if (owner != null && owner.isOnline()) {
                if (getMemberList().contains(uuid)) {
                    memberList.remove(uuid);
                    officerList.add(uuid);
                    owner.sendMessage(Main.getInstance().getUtils().getMessage("promotedOfficer").replace("%player%", Bukkit.getOfflinePlayer(uuid).getName()));
                    if (target != null) {
                        target.sendMessage(Main.getInstance().getUtils().getMessage("promotedOfficerMe"));
                    }
                    return;
                } else {
                    if (getOfficerList().contains(uuid)) {
                        //is an officer, promote to owner
                        officerList.remove(uuid);
                        setOwnerUUID(uuid);
                        officerList.add(owner.getUniqueId());
                        owner.sendMessage(Main.getInstance().getUtils().getMessage("promotedOwner").replace("%player%", Bukkit.getOfflinePlayer(uuid).getName()));
                        if (target != null) {
                            target.sendMessage(Main.getInstance().getUtils().getMessage("promotedOwnerMe"));
                        }
                    }else{
                        owner.sendMessage(Main.getInstance().getUtils().getMessage("promotedNoPlayer"));
                    }
                }
            }
        }
    }

    public void setBiome(Biome biome){
        this.biome = biome;
        List<Chunk> chunks = Main.getInstance().getUtils().getChunks(getLocation(), getProtectionRadius() / 16);
        //for (Chunk c : chunks){
        //   // chunk.getWorld().setBiome(chunk.getX() * 16, chunk.getZ() * 16, biome);
        //    Location center = new Location(c.getWorld(), c.getX() << 4, 64, c.getZ() << 4).add(7, 0, 7);
        //    center.getBlock().setBiome(biome);
        //}

        getLocation().getWorld().setBiome(getLocation().getBlockZ(), getLocation().getBlockZ(), biome);
    }

    public Biome getBiome() {
        return biome;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public boolean isInvited(UUID uuid){
        if (this.invites.contains(uuid)){
            return true;
        }
        return false;
    }

    public void removeInvite(UUID uuid){
        this.invites.remove(uuid);
    }

    public void addInvite(UUID uuid){
        this.invites.add(uuid);
    }

    public List<UUID> getOfficerList() {
        return officerList;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Location getHome() {
        return home;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public boolean isOpen() {
        return open;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public int getProtectionRadius() {
        return protectionRadius;
    }

    public Island getIslandInstance() {
        return islandInstance;
    }

    public List<UUID> getMemberList() {
        return memberList;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setProtectionRadius(int protectionRadius) {
        this.protectionRadius = protectionRadius;
    }

    public boolean canMemberBreak(){
        return permissionMemberBreak;
    }
    public boolean canMemberPlace(){
        return permissionMemberPlace;
    }
    public boolean canMemberInteract(){
        return permissionMemberInteract;
    }

    public boolean canOfficerBreak(){
        return permissionOfficerBreak;
    }
    public boolean canOfficerPlace(){
        return permissionOfficerPlace;
    }
    public boolean canOfficerInteract(){
        return permissionOfficerInteract;
    }

    public void setPermissionMemberBreak(boolean permissionMemberBreak) {
        this.permissionMemberBreak = permissionMemberBreak;
    }

    public void setPermissionMemberInteract(boolean permissionMemberInteract) {
        this.permissionMemberInteract = permissionMemberInteract;
    }

    public void setPermissionMemberPlace(boolean permissionMemberPlace) {
        this.permissionMemberPlace = permissionMemberPlace;
    }

    public void setPermissionOfficerBreak(boolean permissionOfficerBreak) {
        this.permissionOfficerBreak = permissionOfficerBreak;
    }

    public void setPermissionOfficerInteract(boolean permissionOfficerInteract) {
        this.permissionOfficerInteract = permissionOfficerInteract;
    }

    public void setPermissionOfficerPlace(boolean permissionOfficerPlace) {
        this.permissionOfficerPlace = permissionOfficerPlace;
    }

    public int getHopperCount() {
        return hopperCount;
    }

    public int getSpawnerCount() {
        return spawnerCount;
    }

    public void setHopperCount(int hopperCount) {
        this.hopperCount = hopperCount;
    }

    public void setSpawnerCount(int spawnerCount) {
        this.spawnerCount = spawnerCount;
    }
}