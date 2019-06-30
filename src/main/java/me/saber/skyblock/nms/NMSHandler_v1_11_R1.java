package me.saber.skyblock.nms;

import me.saber.skyblock.Main;
import me.saber.skyblock.Storage;
import me.saber.skyblock.island.Island;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class NMSHandler_v1_11_R1 extends NMSHandler {

    @Override
    public String getVersion() {
        return "1_11_R1";
    }

    public void calculate(Chunk chunk, Island island) {

        final CraftChunk craftChunk = (CraftChunk) chunk;

        final int minX = chunk.getX() << 4;
        final int minZ = chunk.getZ() << 4;
        final int maxX = minX | 15;
        final int maxY = chunk.getWorld().getMaxHeight();
        final int maxZ = minZ | 15;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = 0; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    org.bukkit.block.Block block = chunk.getBlock(x, y, z);
                    if (block != null && !block.getType().equals(org.bukkit.Material.AIR)) {
                        if (!Main.getInstance().getReflectionManager().tileEntities.contains(block.getType())) {
                            String type = block.getType().name().toUpperCase();
                            double value = Main.getInstance().getIslandUtils().getBlockLevelWorth(type, false);
                            if (value > 0) {
                                island.addLevel(value);
                                island.addBlockCount(type);
                            }
                        }
                    }
                }
            }
        }

        for (final Map.Entry<BlockPosition, TileEntity> entry : craftChunk.getHandle().tileEntities.entrySet()) {
            if (island.isBlockInIsland(entry.getKey().getX(), entry.getKey().getZ())) {
                final TileEntity tileEntity = entry.getValue();

                String blockType;
                boolean isSpawner = false;

                if (tileEntity instanceof TileEntityMobSpawner) {
                    blockType = ((TileEntityMobSpawner) tileEntity).getSpawner().getMobName().toString().toUpperCase();
                    isSpawner = true;
                } else {
                    blockType = tileEntity.getBlock().getName().toUpperCase();
                }

                double value = Main.getInstance().getIslandUtils().getBlockLevelWorth(blockType, isSpawner);

                if (value > 0) {
                    island.addLevel(value);
                    island.addBlockCount(blockType);
                }
            }
        }
    }

    @Override
    public void removeBlockSuperFast(int X, int Y, int Z, boolean applyPhysics) {
        net.minecraft.server.v1_11_R1.World w = ((org.bukkit.craftbukkit.v1_11_R1.CraftWorld) Storage.getSkyBlockWorld()).getHandle();
        net.minecraft.server.v1_11_R1.Chunk chunk = w.getChunkAt(X >> 4, Z >> 4);
        net.minecraft.server.v1_11_R1.BlockPosition bp = new net.minecraft.server.v1_11_R1.BlockPosition(X, Y, Z);
        net.minecraft.server.v1_11_R1.IBlockData ibd = net.minecraft.server.v1_11_R1.Block.getByCombinedId(0);

        w.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
        chunk.a(bp, ibd);
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
