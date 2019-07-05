package org.savage.skyblock.nms;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.Storage;
import org.savage.skyblock.island.Island;

import java.util.List;
import java.util.Map;

public class NMSHandler_v1_8_R3 extends NMSHandler {

    @Override
    public String getVersion() {
        return "1_8_R3";
    }

    public void calculate(Chunk chunk, Island island) {

        List<org.bukkit.Material> tileEntities = SkyBlock.getInstance().getReflectionManager().tileEntities;

        final CraftChunk craftChunk = (CraftChunk) chunk;

        final int minX = chunk.getX() << 4;
        final int minZ = chunk.getZ() << 4;
        final int maxX = minX | 15;
        final int maxY = chunk.getWorld().getMaxHeight();
        final int maxZ = minZ | 15;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = 0; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block != null && !block.getType().equals(org.bukkit.Material.AIR)) {
                        if (!tileEntities.contains(block.getType())) {
                            String type = block.getType().name().toUpperCase();

                            if (SkyBlock.getInstance().getIslandUtils().hasWorth(type, false)){
                                island.addBlockCount(type, false);
                            }
                        }
                    }
                }
            }
        }

        for (final Map.Entry<BlockPosition, TileEntity> entry : craftChunk.getHandle().tileEntities.entrySet()) {
            if (island.isBlockInIsland(entry.getKey().getX(), entry.getKey().getZ())) {
                final TileEntity tileEntity = entry.getValue();
                if (tileEntity != null) {

                    String blockType;
                    boolean isSpawner = false;

                    if (tileEntity instanceof TileEntityMobSpawner) {
                        blockType = ((TileEntityMobSpawner) tileEntity).getSpawner().getMobName().toUpperCase();
                        isSpawner = true;
                    } else {
                        blockType = tileEntity.w().getName().toUpperCase();
                    }
                    if (SkyBlock.getInstance().getIslandUtils().hasWorth(blockType, isSpawner)){
                        island.addBlockCount(blockType, isSpawner);
                    }
                }
            }
        }
    }

    @Override
    public void removeBlockSuperFast(int X, int Y, int Z, boolean applyPhysics) {
        Storage.getSkyBlockWorld().getBlockAt(X, Y, Z).setType(org.bukkit.Material.AIR, applyPhysics);
    }

    @Override
    public void sendBorder(Player p, double x, double z, double radius) {
        final WorldBorder worldBorder = new WorldBorder();
        worldBorder.setCenter(x, z);
        worldBorder.setSize(radius * 2);
        worldBorder.setWarningDistance(0);
        final EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
    }

    @Override
    public void sendTitle(Player p, String text, int in, int stay, int out, String type) {
        final PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.valueOf(type),
                IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\":\"" + text + " \"}")), in, stay, out);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
    }
}
