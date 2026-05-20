package org.dimdev.dimdoors.util;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.world.pocket.PocketChunkLoadingManager;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public record ChunkBounds(int minX, int maxX, int minZ, int maxZ) {
    public static ChunkBounds of(Pocket<?, ?> pocket) {
        BoundingBox box = pocket.getBox();
        return new ChunkBounds(box.minX() >> 4, box.maxX() >> 4, box.minZ() >> 4, box.maxZ() >> 4);
    }

    public boolean contains(int chunkX, int chunkZ) {
        return chunkX >= minX && chunkX <= maxX && chunkZ >= minZ && chunkZ <= maxZ;
    }

    public int width() {
        return maxX - minX + 1;
    }

    public int length() {
        return maxZ - minZ + 1;
    }
}
