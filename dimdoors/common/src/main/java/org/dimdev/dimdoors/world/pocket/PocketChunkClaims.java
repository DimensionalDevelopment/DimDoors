package org.dimdev.dimdoors.world.pocket;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.ChunkBounds;
import org.dimdev.dimdoors.world.DataValues;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public final class PocketChunkClaims {
    public static boolean hasClaimedChunk(Pocket<?, ?> pocket) {
        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return false;

        ChunkBounds bounds = ChunkBounds.of(pocket);
        for (int cx = bounds.minX; cx <= bounds.maxX; cx++) {
            for (int cz = bounds.minZ; cz <= bounds.maxZ; cz++) {
                if (isClaimed(level.getChunk(cx, cz))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void claimChunks(Pocket<?, ?> pocket) {
        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return;

        ChunkBounds bounds = ChunkBounds.of(pocket);

        for (int cx = bounds.minX; cx <= bounds.maxX; cx++) {
            for (int cz = bounds.minZ; cz <= bounds.maxZ; cz++) {
                ChunkAccess chunk = level.getChunk(cx, cz);
                if (!isClaimed(chunk)) {
                    DataValues.POCKET_GENERATED.set(chunk, true);
                    chunk.setUnsaved(true);
                }
            }
        }

    }

    public static boolean isClaimed(ChunkAccess chunk) {
        return DataValues.POCKET_GENERATED.getOrDefault(chunk, false);
    }
}
