package org.savage.skyblock.island;

public class FakeItem {

    private String type;
    private boolean spawner;

    public FakeItem(String type, boolean spawner) {
        this.type = type;
        this.spawner = spawner;
    }

    public boolean isSpawner() {
        return spawner;
    }

    public String getType() {
        return type;
    }

    public boolean equals(FakeItem fakeItem) {
        return getType().equalsIgnoreCase(fakeItem.getType()) && (isSpawner() == fakeItem.isSpawner());
    }
}