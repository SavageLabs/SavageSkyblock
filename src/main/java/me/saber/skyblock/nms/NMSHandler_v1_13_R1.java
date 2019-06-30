package me.saber.skyblock.nms;

import me.saber.skyblock.Main;
import me.saber.skyblock.Storage;
import me.saber.skyblock.island.Island;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class NMSHandler_v1_13_R1 extends NMSHandler {


    public void calculate(Chunk chunk, Island island) {

        final CraftChunk craftChunk = (CraftChunk) chunk;

        for (final Map.Entry<net.minecraft.server.v1_13_R1.BlockPosition, net.minecraft.server.v1_13_R1.TileEntity> entry : craftChunk.getHandle().tileEntities.entrySet()) {
            if (island.isBlockInIsland(entry.getKey().getX(), entry.getKey().getZ())) {
                final net.minecraft.server.v1_13_R1.TileEntity tileEntity = entry.getValue();

                String blockType;
                boolean isSpawner = false;

                if (tileEntity instanceof net.minecraft.server.v1_13_R1.TileEntityMobSpawner) {
                    blockType = ((net.minecraft.server.v1_13_R1.TileEntityMobSpawner) tileEntity).getSpawner().getMobName().toString().toUpperCase();
                    isSpawner = true;
                } else {
                    blockType = tileEntity.getBlock().getMaterial().toString().toUpperCase();
                }

                double value = Main.getInstance().getIslandUtils().getBlockLevelWorth(blockType, isSpawner);

                if (value > 0) {
                    //got a value, add to the island's level
                    island.addLevel(value);
                }
            }
        }
    }

    @Override
    public void removeBlockSuperFast(int X, int Y, int Z, boolean applyPhysics) {
        Storage.getSkyBlockWorld().getBlockAt(X, Y, Z).setType(Material.AIR, applyPhysics);
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
