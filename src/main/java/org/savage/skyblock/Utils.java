package org.savage.skyblock;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.savage.skyblock.island.Island;
import org.savage.skyblock.island.MemoryPlayer;
import org.savage.skyblock.island.upgrades.Upgrade;

import java.util.*;

public class Utils {

    public void log(String message){
        Bukkit.getLogger().info("\n"+message+"\n");
    }

    public String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        original = original.toLowerCase();
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public List<String> color(List<String> list){
        List<String> color = new ArrayList<>();
        for (String s : list){
            color.add(color(s));
        }
        return color;
    }

    public ItemStack createItem(String materialName, int data, String name, List<String> lore, int amount){
        Material material = null;
        material = Material.valueOf(materialName.toUpperCase());
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(color(lore));
        itemStack.setAmount(amount);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String getMessage(String key){
        return color(SkyBlock.getInstance().getFileManager().getMessages().getFileConfig().getString("messages." + key));
    }

    public List<String> getMessageList(String key) {
        return color(SkyBlock.getInstance().getFileManager().getMessages().getFileConfig().getStringList("messages." + key));
    }

    public int getSettingInt(String key){
        return SkyBlock.getInstance().getConfig().getInt("settings." + key);
    }
    public String getSettingString(String key) {
        return SkyBlock.getInstance().getConfig().getString("settings." + key);
    }
    public double getSettingDouble(String key){
        return SkyBlock.getInstance().getConfig().getDouble("settings." + key);
    }

    public boolean getSettingBool(String key) {
        return SkyBlock.getInstance().getConfig().getBoolean("settings." + key);
    }

    public static Location randomLocation(Location min, Location max) {
        Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), min.getY(), Math.abs(max.getZ() - min.getZ()));
        return new Location(min.getWorld(), (Math.random() * range.getX()) + (min.getX() <= max.getX() ? min.getX() : max.getX()), range.getY(), (Math.random() * range.getZ()) + (min.getZ() <= max.getZ() ? min.getZ() : max.getZ()));
    }

    public Location generateIslandLocation(Location min, Location max){
        Location randomLocation = randomLocation(min, max);
        if (randomLocation != null){
            //check this location's distance from all preexisting islands's locations
            if (!Storage.islandList.isEmpty()) {
                for (Island island : Storage.islandList) {
                    Location location = island.getLocation();

                    double distance = randomLocation.distanceSquared(location);
                    if (distance >= 100) {
                        return randomLocation;
                    }
                }
            }else{
                return randomLocation;
            }
        }
        return generateIslandLocation(min, max);
    }

    public void loadIslands(){
        //onEnable
        //layout: ownerUUID;member1,member2;x;y;z;protectionRadius
        List<String> data = SkyBlock.getInstance().getFileManager().getData().getFileConfig().getStringList("data");

        for (String islandData : data){
            String[] l = islandData.split(";");
            UUID ownerUUID = UUID.fromString(l[0]);
            double x = Double.parseDouble(l[4]);
            double y = Double.parseDouble(l[5]);
            double z = Double.parseDouble(l[6]);
            int protectionRadius = Integer.parseInt(l[7]);
            String home = l[8];
            String biome = l[9];

            //private boolean permissionMemberPlace = true;
            //    private boolean permissionMemberBreak = true;
            //    private boolean permissionMemberInteract = true;
            //
            //    private boolean permissionOfficerPlace = true;
            //    private boolean permissionOfficerBreak = true;
            //    private boolean permissionOfficerInteract = true;

            boolean memberPlace = Boolean.parseBoolean(l[10]);
            boolean memberBreak = Boolean.parseBoolean(l[11]);
            boolean memberInteract = Boolean.parseBoolean(l[12]);

            boolean officerPlace = Boolean.parseBoolean(l[13]);
            boolean officerBreak = Boolean.parseBoolean(l[14]);
            boolean officerInteract = Boolean.parseBoolean(l[15]);

            String name = l[16];
            int memberLimit = Integer.parseInt(l[17]);

            HashMap<Upgrade, Integer> upgradesMap = new HashMap<>();

            Location homeLocation = null;

            if (deserializeLocation(home) != null && !home.equalsIgnoreCase("")){
                homeLocation = deserializeLocation(home);
            }


            String[] l2 = l[1].split(";");
            String[] l3 = l[2].split(";");
            String[] l4 = l[3].split(";");

            List<UUID> memberList = new ArrayList<>();
            List<UUID> officerList = new ArrayList<>();
            List<UUID> coOwnerList = new ArrayList<>();

            if (!Arrays.asList(l2).isEmpty()) {
                for (String s : l2) {
                    if (!s.equalsIgnoreCase("")) {
                        memberList.add(UUID.fromString(s));
                    }
                }
            }
            if (!Arrays.asList(l3).isEmpty()) {
                for (String s : l3) {
                    if (!s.equalsIgnoreCase("")) {
                        officerList.add(UUID.fromString(s));
                    }
                }
            }
            if (!Arrays.asList(l4).isEmpty()) {
                for (String s : l4) {
                    if (!s.equalsIgnoreCase("")) {
                        coOwnerList.add(UUID.fromString(s));
                    }
                }
            }

            try {
                String upgradeString = l[18];

                for (String upgrades : upgradeString.split(",")) {
                    int id = Integer.parseInt(upgrades.split("!")[0]);
                    int tier = Integer.parseInt(upgrades.split("!")[1]);
                    upgradesMap.put(Upgrade.Upgrades.getUpgrade(id), tier);
                }
            }catch(ArrayIndexOutOfBoundsException e){}


            Island island = new Island("", x, y, z, ownerUUID, coOwnerList, memberList, officerList, protectionRadius, name, memberLimit);

            island.setUpgradeMap(upgradesMap);

            if (name.equalsIgnoreCase("")) {
                island.setName(getNameFromUUID(ownerUUID));
            }

            island.setPermissionMemberPlace(memberPlace);
            island.setPermissionMemberBreak(memberBreak);
            island.setPermissionMemberInteract(memberInteract);

            island.setPermissionOfficerPlace(officerPlace);
            island.setPermissionOfficerBreak(officerBreak);
            island.setPermissionOfficerInteract(officerInteract);

            if (homeLocation != null){
                island.setHome(homeLocation);
            }else{
                island.setHome(island.getLocation());
            }
            //island.setBiome(Biome.valueOf(biome));
        }

    }

    public void saveIslands(){
        //layout: ownerUUID;member1,member2;x,y,z;protectionRadius

        // SkyBlock.getInstance().getFileManager().d.set("data", new ArrayList<>());
        // SkyBlock.getInstance().getFileManager().dataFileCustom.saveFile();

        List<String> islandData = new ArrayList<>();

        for (Island island : Storage.islandList){
            UUID owner = island.getOwnerUUID();
            double x = island.getCenterX();
            double y = island.getCenterY();
            double z = island.getCenterZ();
            List<UUID> members = island.getMemberList();
            List<UUID> officers = island.getOfficerList();
            List<UUID> coowners = island.getCoownerList();
            int protectionRadius = island.getProtectionRadius();
            String home = serializeLocation(island.getHome());
            String biome = island.getBiome().name();
            String name = island.getName();
            int memberLimit = island.getMemberLimit();
            HashMap<Upgrade, Integer> upgradesMap = island.getUpgrade_tier();

            String upgradeString = "";
            String memberList = "";
            String officerList = "";
            String coOwnerList = "";

            if (!members.isEmpty()){
                for (UUID uuid : members){
                    if (memberList.equalsIgnoreCase("")){
                        memberList = uuid.toString();
                    }else{
                        memberList = memberList+","+uuid.toString();
                    }
                }
            }
            if (!officers.isEmpty()){
                for (UUID uuid : officers){
                    if (officerList.equalsIgnoreCase("")){
                        officerList = uuid.toString();
                    }else{
                        officerList = officerList+","+uuid.toString();
                    }
                }
            }
            if (!coowners.isEmpty()){
                for (UUID uuid : coowners){
                    if (coOwnerList.equalsIgnoreCase("")){
                        coOwnerList = uuid.toString();
                    }else{
                        coOwnerList = coOwnerList+","+uuid.toString();
                    }
                }
            }

            for (Upgrade upgrade : upgradesMap.keySet()){
                int tier = upgradesMap.get(upgrade);
                int id = upgrade.getId();
                if (upgradeString.equalsIgnoreCase("")) {
                    //empty
                    upgradeString = id+"!"+tier;
                }else{
                    upgradeString = upgradeString+","+id+"!"+tier;
                }
            }

            islandData.add(owner.toString() + ";" + memberList + ";" + officerList + ";" + coOwnerList+ ";" + x + ";" + y + ";" + z + ";" + protectionRadius + ";" + home + ";" + biome + ";" + island.canMemberPlace() + ";" + island.canMemberBreak() + ";" + island.canMemberInteract() + ";" +
                    island.canOfficerPlace() + ";" + island.canOfficerBreak() + ";" + island.canOfficerInteract() + ";" + name+";"+memberLimit+";"+upgradeString);
        }

        SkyBlock.getInstance().getFileManager().getData().getFileConfig().set("data", islandData);
        SkyBlock.getInstance().getFileManager().getData().saveFile();
    }

    public double getBalance(UUID uuid){
        return SkyBlock.getEcon().getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public void takeMoney(UUID uuid, double amount){
        SkyBlock.getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public void addMoney(UUID uuid, double amount){
        SkyBlock.getEcon().depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
    }


    public List<Block> getBlocksBetweenPoints(Location l1, Location l2) {
        List<Block> blocks = new ArrayList<Block>();
        int topBlockX = (l1.getBlockX() < l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());
        int bottomBlockX = (l1.getBlockX() > l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());
        int topBlockY = (l1.getBlockY() < l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());
        int bottomBlockY = (l1.getBlockY() > l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());
        int topBlockZ = (l1.getBlockZ() < l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());
        int bottomBlockZ = (l1.getBlockZ() > l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int y = bottomBlockY; y <= topBlockY; y++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    Block block = l1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public List<Block> getNearbyBlocks(Location location, int radius, List<Material> blocksToFind) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    for (Material b: blocksToFind){
                        if (b.equals(block.getType())){
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public List<Block> getBlocksInChunk(Island island, Chunk chunk){
        List<Block> list = new ArrayList<>();
        for(int xx = chunk.getX(); xx < chunk.getX()+16; xx++) {
            for(int zz = chunk.getZ(); zz < chunk.getZ()+16; zz++) {
                for(int yy = 0; yy < 128; yy++) {
                    Block block = chunk.getBlock(xx, yy, zz);
                    if (block != null && !block.getType().equals(Material.AIR)){
                        if (block.getLocation() != null && SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(block.getLocation()) != null) {
                            if (SkyBlock.getInstance().getIslandUtils().getIslandFromLocation(block.getLocation()).equals(island)) {
                                list.add(block);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public List<Chunk> getChunks(Location location, int radius){
        List<Chunk> chunks = new ArrayList<>();
        Chunk centerChunk = location.getChunk();
        for (int x = centerChunk.getX()-radius; x < centerChunk.getX() + radius; x++) {
            for (int z = centerChunk.getZ()-radius; z < centerChunk.getZ() + radius; z++) {
                if (centerChunk.getWorld().getChunkAt(x, z) != null && centerChunk.getWorld().getChunkAt(x, z).isLoaded()) {
                    Chunk chunk = centerChunk.getWorld().getChunkAt(x, z);
                    if (!chunks.contains(chunk) && chunk.getWorld().equals(location.getWorld())){
                        chunks.add(chunk);
                    }
                }
            }
        }
        return chunks;
    }

    public String getNameFromUUID(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            return Bukkit.getPlayer(uuid).getName();
        } else {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    public int getIntegersFromString(String string) {
        return Integer.parseInt(string.replaceAll("[\\D]", ""));
    }

    public String stripIntegersFromString(String string) {
        return string.replaceAll("[0-9]", "");
    }

    public Location deserializeLocation(String locationString){
        try {
            String[] l = locationString.split(",");
            return new Location(Bukkit.getWorld(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]));
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    public String serializeLocation(Location location){
        return location.getWorld().getName()+","+location.getX()+","+location.getY()+","+location.getZ();
    }

    public boolean hasMemoryPlayer(UUID uuid){
        for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList){
            if (memoryPlayer.getUuid().equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public MemoryPlayer getMemoryPlayer(UUID uuid){
        for (MemoryPlayer memoryPlayer : Storage.memoryPlayerList){
            if (memoryPlayer.getUuid().equals(uuid)){
                return memoryPlayer;
            }
        }
        return null;
    }

    public boolean hasPermissionAtleast(UUID uuid, String permissionBase){ // uses the cache class
        if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOp()){
            return true;
        }
        if (hasMemoryPlayer(uuid)) {
            return getMemoryPlayer(uuid).hasPermission(permissionBase);
        }
        return false;
    }
}