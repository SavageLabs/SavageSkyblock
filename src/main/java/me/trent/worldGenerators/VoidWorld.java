package me.trent.worldGenerators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidWorld {

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidWorldGenerator();
    }

    public class VoidWorldGenerator extends ChunkGenerator {

        public final ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int ChunkX, final int ChunkZ, final ChunkGenerator.BiomeGrid biome) {
            final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    ///biome.setBiome(x, z, this.a);
                }
            }
            if (0 >= ChunkX << 4 && 0 < ChunkX + 1 << 4 && 0 >= ChunkZ << 4 && 0 < ChunkZ + 1 << 4) {
                chunkData.setBlock(0, 63, 0, Material.BEDROCK);
            }
            return chunkData;
        }

        public final Location getFixedSpawnLocation(final World world, final Random random) {
            return new Location(world, 0.0, 65.0, 0.0);
        }

    }
}
