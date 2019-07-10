package org.savage.skyblock.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.API.*;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.upgrades.Upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Island {

    private Island islandInstance;

    private double centerX;
    private double centerY;
    private double centerZ;

    private int protectionRadius;

    private String name;

    private UUID ownerUUID;
    private List<UUID> memberList;
    private List<UUID> officerList;
    private List<UUID> coownerList;
    private boolean open = false; // meaning people can join anytime
    private boolean deleting = false;
    private Location home;
    private Biome biome = Biome.valueOf(SkyBlock.getInstance().getUtils().getSettingString("default-biome"));

    private List<UUID> invites = new ArrayList<>();

    private boolean permissionMemberPlace = true;
    private boolean permissionMemberBreak = true;
    private boolean permissionMemberInteract = true;

    private boolean permissionOfficerPlace = true;
    private boolean permissionOfficerBreak = true;
    private boolean permissionOfficerInteract = true;

    private double level = 0;
    private int topPlace = 0;

    private double blockWorth = 0;
    private double spawnerWorth = 0;

    private int memberLimit;

    private HashMap<Upgrade, Integer> upgrade_tier = new HashMap<>();
    private HashMap<FakeItem, Integer> blocks = new HashMap<>();

    public Island(String schematic, double x, double y, double z, UUID ownerUUID, List<UUID> coownerList, List<UUID> officerList, List<UUID> memberList, int protectionRadius, String name, int memberLimit) {
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.ownerUUID = ownerUUID;
        this.coownerList = coownerList;
        this.memberList = memberList;
        this.officerList = officerList;
        this.protectionRadius = protectionRadius;
        this.home = getLocation();
        this.name = name;
        this.memberLimit = memberLimit;

        islandInstance = this;

        Storage.islandList.add(getIslandInstance());
        SkyBlock.getInstance().getUtils().log("Loaded island");

        if (!schematic.equalsIgnoreCase("")){
            //generate the island schematic
            Player p = Bukkit.getPlayer(getOwnerUUID());
            if (p != null && p.isOnline()){
                p.closeInventory();
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("island-loading"));
            }

            SkyBlock.getInstance().getWorldGenerator().pasteSchem(getLocation(), schematic);

            new BukkitRunnable(){
                @Override
                public void run() {
                    Bukkit.getPlayer(getOwnerUUID()).teleport(getLocation());
                }
            }.runTaskLater(SkyBlock.getInstance(), 10L);
        }

        setTopPlace(Storage.currentTop);
        Storage.currentTop++;
    }

    public int getHopperCount() {
        return getBlockCount(new FakeItem("HOPPER", false));
    }

    public int getSpawnerCount() {
        int count = 0;
        for (FakeItem fakeItem : getBlocks().keySet()) {
            if (fakeItem.isSpawner()) {
                count = Math.addExact(count, getBlocks().get(fakeItem));
            }
        }
        return count;
    }

    public int getSpawnerCount(String spawnerType) {
        int count = 0;
        for (FakeItem fakeItem : getBlocks().keySet()) {
            if (fakeItem.isSpawner()) {
                if (fakeItem.getType().equalsIgnoreCase(spawnerType)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void setUpgradeMap(HashMap<Upgrade, Integer> upgrade_tier) {
        this.upgrade_tier = upgrade_tier;
    }

    public int getUpgradeTier(Upgrade upgrade){
        if (getUpgrade_tier().get(upgrade) != null){
            return getUpgrade_tier().get(upgrade);
        }
        return 0;
    }

    public void setUpgradeTier(Upgrade upgrade, int tier){
        if (getUpgrade_tier().get(upgrade) != null){
            this.upgrade_tier.remove(upgrade);
        }
        this.upgrade_tier.put(upgrade, tier);
    }

    public HashMap<Upgrade, Integer> getUpgrade_tier() {
        return upgrade_tier;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    public void clearBlockCount() {
        this.blocks.clear();
    }

    public boolean blocksHas(FakeItem fakeItem) {
        for (FakeItem fakeItems : blocks.keySet()) {
            if (fakeItem.equals(fakeItems)) {
                return true;
            }
        }
        return false;
    }

    public int getBlockCount(FakeItem fakeItem) {
        for (FakeItem fakeItems : blocks.keySet()) {
            if (fakeItem.equals(fakeItems)) {
                return blocks.get(fakeItems);
            }
        }
        return 0;
    }

    public FakeItem getDuplicateFakeItem(FakeItem fakeItem) {
        for (FakeItem fakeItems : blocks.keySet()) {
            if (fakeItem.equals(fakeItems)) {
                return fakeItems;
            }
        }
        return null;
    }

    public void addBlockCount(String blockTypeName, boolean spawner, int toAdd) {
        FakeItem fakeItem = new FakeItem(blockTypeName, spawner);
        if (blocksHas(fakeItem)) {
            //already has it in the map, add to it
            FakeItem dupe = getDuplicateFakeItem(fakeItem);
            int amount = getBlocks().get(dupe);
            this.blocks.remove(dupe);
            this.blocks.put(dupe, Math.addExact(amount, toAdd));
        } else {
            //doesn't, add the initial one
            this.blocks.put(fakeItem, toAdd);
        }
    }

    public void removeBlockCount(String blockTypeName, boolean spawner, int toRemove) {
        FakeItem fakeItem = new FakeItem(blockTypeName, spawner);
        if (blocksHas(fakeItem)) {
            //already has it in the map, add to it
            FakeItem dupe = getDuplicateFakeItem(fakeItem);
            int amount = blocks.get(dupe);
            int end = Math.subtractExact(amount, toRemove);
            this.blocks.remove(dupe);
            if (end > 0) {
                this.blocks.put(dupe, end);
            }
        }
    }


    public boolean hasPlayer(UUID uuid){
        return getOwnerUUID().equals(uuid) || getOfficerList().contains(uuid) || getCoownerList().contains(uuid) || getMemberList().contains(uuid);
    }

    public List<UUID> getAllPlayers(){
        List<UUID> l = getOfficerList();
        l.addAll(getMemberList());
        l.addAll(getCoownerList());
        l.add(getOwnerUUID());
        return l;
    }

    public double getWorth() {
        return (getBlockWorth() + getSpawnerWorth());
    }


    public void addBlockWorth(double d) {
        this.blockWorth = (getBlockWorth() + d);
    }

    public void addSpawnerWorth(double d) {
        this.spawnerWorth = (getSpawnerWorth() + d);
    }

    public double getBlockWorth() {
        return blockWorth;
    }

    public void setBlockWorth(double d) {
        this.blockWorth = d;
    }

    public double getSpawnerWorth() {
        return spawnerWorth;
    }

    public void setSpawnerWorth(double d) {
        this.spawnerWorth = d;
    }

    public int getTopPlace() {
        return topPlace;
    }

    public void setTopPlace(int topPlace) {
        this.topPlace = topPlace;
    }

    public HashMap<FakeItem, Integer> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getInvites() {
        return invites;
    }

    public List<FakeChunk> getFakeChunks() {
        List<FakeChunk> fakeChunks = new ArrayList<>();
        Location center = getLocation();
        int radius = getProtectionRadius();

        new SpiralTask(center, (radius / 16)) {
            @Override
            public boolean work() {
                fakeChunks.add(this.currentChunk());
                return true;
            }
        }.run();
        return fakeChunks;
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

        if (loc1.getBlockX() >= x && loc1.getZ() >= z) {
            return x >= loc2.getBlockX() && z >= loc2.getBlockZ();
        }
        return false;
    }

    public boolean hasPlayersInIsland(){
        List<UUID> uuids = new ArrayList<>();
        uuids.add(getOwnerUUID());
        uuids.addAll(getOfficerList());
        uuids.addAll(getMemberList());
        uuids.addAll(getCoownerList());
        for (UUID uuid : uuids){
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()){
                if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(Bukkit.getPlayer(uuid).getLocation()) != null) {
                    if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(Bukkit.getPlayer(uuid).getLocation()).equals(getIslandInstance())) {
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
                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("cannotPromoteDemoteSelf"));
                    return;
                }

                addInvite(target);
                Bukkit.getPlayer(target).sendMessage(SkyBlock.getInstance().getUtils().getMessage("invited").replace("%player%", p.getName()));
                p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("invitedMe").replace("%player%", Bukkit.getPlayer(target).getName()));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (isInvited(target)) {
                            //still invited,
                            removeInvite(target);
                            if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()) {
                                Bukkit.getPlayer(target).sendMessage(SkyBlock.getInstance().getUtils().getMessage("inviteExpire").replace("%player%", p.getName()));
                            }
                        }
                    }
                }.runTaskLater(SkyBlock.getInstance(), SkyBlock.getInstance().getUtils().getSettingInt("inviteExpireTime") * 20);
            }
        }else{
            p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("alreadyInvited"));
        }
    }

    public void kick(UUID kicker, UUID target) {
        IslandKickEvent kickEvent = new IslandKickEvent(getIslandInstance(), kicker, target);
        Bukkit.getPluginManager().callEvent(kickEvent);

        if (!kickEvent.isCancelled()) {
            if (getMemberList().contains(target) || getOfficerList().contains(target) || getOwnerUUID().equals(target)){
                if (getOwnerUUID().equals(kicker) || getOfficerList().contains(kicker)) {
                    if (kicker.equals(target)){
                        Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kickedYourself"));
                        return;
                    }
                    if (getOfficerList().contains(target) || getOwnerUUID().equals(target)) {
                        if (getOfficerList().contains(kicker)) {
                            //can't do it
                            Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionKick"));
                        }else if (getOwnerUUID().equals(kicker)){
                            //let owner kick them
                            Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kicked").replace("%player%", Bukkit.getOfflinePlayer(target).getName()));
                            if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()){
                                Bukkit.getPlayer(target).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kickedMe"));
                            }
                            officerList.remove(target);
                        }
                    }else{
                        //let officer kick them
                        Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kicked").replace("%player%", Bukkit.getOfflinePlayer(target).getName()));
                        if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()){
                            Bukkit.getPlayer(target).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kickedMe"));
                        }
                        memberList.remove(target);
                    }
                }else{
                    Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionKick"));
                }
            }else{
                Bukkit.getPlayer(kicker).sendMessage(SkyBlock.getInstance().getUtils().getMessage("kickedNoPlayer"));
            }
        }
    }

    public void delete(){
        Location islandSpawn = SkyBlock.getInstance().getIslandUtils().getIslandSpawn();
        if (islandSpawn != null) {
            IslandDeleteEvent deleteEvent = new IslandDeleteEvent(getIslandInstance(), getOwnerUUID());
            Bukkit.getPluginManager().callEvent(deleteEvent);

            if (!deleteEvent.isCancelled()) {

                int radius = getProtectionRadius();

                Bukkit.getPlayer(getOwnerUUID()).teleport(SkyBlock.getInstance().getIslandUtils().getIslandSpawn());

                List<UUID> uuids = new ArrayList<>();
                uuids.addAll(getMemberList());
                uuids.addAll(getOfficerList());
                uuids.addAll(getCoownerList());

                for (UUID uuid : uuids) {
                    if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("deletedIsland"));
                        p.teleport(SkyBlock.getInstance().getIslandUtils().getIslandSpawn());
                    }
                }

                List<Block> blocks = SkyBlock.getInstance().getUtils().getNearbyBlocks(getLocation(), radius);
                for (Block b : blocks){
                    SkyBlock.getInstance().getReflectionManager().nmsHandler.removeBlockSuperFast(b.getX(), b.getY(), b.getZ(), false);
                }

                Bukkit.getPlayer(getOwnerUUID()).sendMessage(SkyBlock.getInstance().getUtils().getMessage("deleteIsland"));

                unload();
            }
        }else{
            Bukkit.getPlayer(getOwnerUUID()).sendMessage(SkyBlock.getInstance().getUtils().getMessage("noSpawn"));
        }
    }

    public Location getLocation(){
        return new Location(Storage.getSkyBlockWorld(), getCenterX(), getCenterY(), getCenterZ());
    }


    public void demote(UUID admin, UUID target){
        Player adminPlayer = Bukkit.getPlayer(admin);
        if (!getMemberList().contains(target) && !getOfficerList().contains(target)){
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedNoPlayer"));
            return;
        }
        String targetName;
        if (Bukkit.getPlayer(target) != null){
            targetName = Bukkit.getPlayer(target).getName();
        }else{
            targetName = Bukkit.getOfflinePlayer(target).getName();
        }
        if (targetName == null){
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedNoPlayer"));
            return;
        }
        if (getOwnerUUID().equals(admin) || getCoownerList().contains(admin)){
            //is either owner or coowner,

            if (getOfficerList().contains(target)) {
                    //target is an officer, demote to member
                    IslandDemoteEvent demoteEvent = new IslandDemoteEvent(getIslandInstance(), admin, target);
                    Bukkit.getPluginManager().callEvent(demoteEvent);

                    this.officerList.remove(target);
                    this.memberList.add(target);

                    adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedMember").replace("%player%", targetName));
                    Player targetPlayer = Bukkit.getPlayerExact(targetName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedMemberMe"));
                    }
                }
            if (getCoownerList().contains(target)){
                if (getOwnerUUID().equals(admin)){
                    //is owner trying to demote a coowner
                    IslandDemoteEvent demoteEvent = new IslandDemoteEvent(getIslandInstance(), admin, target);
                    Bukkit.getPluginManager().callEvent(demoteEvent);

                    this.coownerList.remove(target);
                    this.officerList.add(target);

                    adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedOfficer").replace("%player%", targetName));
                    Player targetPlayer = Bukkit.getPlayerExact(targetName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedOfficerMe"));
                    }
                }else{
                    adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                }
            }

        }else{
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
        }
    }

    //todo make a transfer ownership method

    public void promote(UUID admin, UUID target){
        Player adminPlayer = Bukkit.getPlayer(admin);
        if (!getMemberList().contains(target) && !getOfficerList().contains(target)){
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedNoPlayer"));
            return;
        }
        String targetName;
        if (Bukkit.getPlayer(target) != null){
            targetName = Bukkit.getPlayer(target).getName();
        }else{
            targetName = Bukkit.getOfflinePlayer(target).getName();
        }
        if (targetName == null){
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("demotedNoPlayer"));
            return;
        }
        if (getOwnerUUID().equals(admin) || getCoownerList().contains(admin)){
            //is either owner or coowner,
            if (getMemberList().contains(target)){
                //target is a member, the admin can promote them
                //promote member to an officer
                IslandPromoteEvent promoteEvent = new IslandPromoteEvent(getIslandInstance(), admin, target);
                Bukkit.getPluginManager().callEvent(promoteEvent);
                if (!this.officerList.contains(target)){
                    this.officerList.add(target);
                }
                this.memberList.remove(target);
                adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedOfficer").replace("%player%", targetName));
                Player targetPlayer = Bukkit.getPlayerExact(targetName);
                if (targetPlayer != null && targetPlayer.isOnline()){
                    targetPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedOfficerMe"));
                }
                return;
                //Send player has been promoted to officer
            }

                if (getOfficerList().contains(target)) {
                    if (getOwnerUUID().equals(admin)) {
                        //target is an officer, promote them to coOwner
                        IslandPromoteEvent promoteEvent = new IslandPromoteEvent(getIslandInstance(), admin, target);
                        Bukkit.getPluginManager().callEvent(promoteEvent);

                        if (!this.coownerList.contains(target)){
                            this.coownerList.add(target);
                        }
                        this.officerList.remove(target);
                        adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedCoOwner").replace("%player%", targetName));
                        Player targetPlayer = Bukkit.getPlayerExact(targetName);
                        if (targetPlayer != null && targetPlayer.isOnline()) {
                            targetPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promotedCoOwnerMe"));
                        }
                    }else{
                        adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                    }
                }
                if (getCoownerList().contains(target)){
                    if (getOwnerUUID().equals(admin)){
                        //is owner trying to promote a coowner,
                        adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("promoteMax"));
                    }else{
                        adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                    }
                }

        }else{
            //Send not enough permission todo;
            adminPlayer.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
        }
    }

    public void addCoOwner(UUID uuid){
        this.coownerList.add(uuid);
    }

    public void removeOfficer(UUID uuid){
        this.officerList.remove(uuid);
    }
    public void removeCoOwner(UUID uuid){
        this.coownerList.remove(uuid);
    }
    public void removeMember(UUID uuid){
        this.memberList.remove(uuid);
    }

    public void setBiome(Biome biome){
        this.biome = biome;
        getLocation().getWorld().setBiome(getLocation().getBlockZ(), getLocation().getBlockZ(), biome);
    }

    public Biome getBiome() {
        return biome;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public boolean isInvited(UUID uuid){
        return this.invites.contains(uuid);
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

    public List<UUID> getCoownerList() {
        return coownerList;
    }

    public void setCoownerList(List<UUID> coownerList) {
        this.coownerList = coownerList;
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

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public void setCenterZ(double centerZ) {
        this.centerZ = centerZ;
    }

    public void setIslandInstance(Island islandInstance) {
        this.islandInstance = islandInstance;
    }

    public boolean isPermissionMemberBreak() {
        return permissionMemberBreak;
    }

    public boolean isPermissionMemberInteract() {
        return permissionMemberInteract;
    }

    public boolean isPermissionMemberPlace() {
        return permissionMemberPlace;
    }

    public boolean isPermissionOfficerBreak() {
        return permissionOfficerBreak;
    }

    public boolean isPermissionOfficerPlace() {
        return permissionOfficerPlace;
    }

    public boolean isPermissionOfficerInteract() {
        return permissionOfficerInteract;
    }

}