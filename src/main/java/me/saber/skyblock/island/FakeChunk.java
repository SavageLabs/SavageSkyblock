package me.saber.skyblock.island;

public class FakeChunk {

    private int x, z;
    private String worldName;

    public FakeChunk(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public String toString() {
        return getWorldName() + "," + getX() + "," + getZ();
    }
}
