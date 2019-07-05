package org.savage.skyblock.generators;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoidWorld extends ChunkGenerator {

    private Random rand = new Random();
    private PerlinOctaveGenerator gen;

    @SuppressWarnings("deprecation")
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        if (world.getEnvironment().equals(World.Environment.NETHER)) {
           // return generateNetherBlockSections(world, random, chunkX, chunkZ, biomeGrid);
        }
        byte[][] result = new byte[world.getMaxHeight() / 16][];

            return result;
        }

    void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        // is this chunk part already initialized?
        if (result[y >> 4] == null) {
            // Initialize the chunk part
            result[y >> 4] = new byte[4096];
        }
        // set the block (look above, how this is done)
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    // This needs to be set to return true to override minecraft's default
    // behavior
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        /*
	if (world.getEnvironment().equals(World.Environment.NETHER)) {
	    return Arrays.<BlockPopulator> asList(new NetherPopulator());
	}*/
        return Arrays.asList(new BlockPopulator[0]);
    }
    }
