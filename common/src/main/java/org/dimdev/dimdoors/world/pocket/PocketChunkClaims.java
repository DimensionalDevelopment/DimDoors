package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.ChunkBounds;
import org.dimdev.limlib.util.DataValue;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public final class PocketChunkClaims {
    public static final DataValue<Boolean> POCKET_GENERATED = DimensionalDoors.getSided().registerDataValue("pocket_generated", () -> false, Codec.BOOL, null);

    public static void init() {
    }

    public static boolean hasClaimedChunk(Pocket<?, ?> pocket) {
        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return false;

        ChunkBounds bounds = ChunkBounds.of(pocket);
        for (int cx = bounds.minX(); cx <= bounds.maxX(); cx++) {
            for (int cz = bounds.minZ(); cz <= bounds.maxZ(); cz++) {
                if (isClaimed(level.getChunk(cx, cz))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int claimChunks(Pocket<?, ?> pocket) {
        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return 0;

        ChunkBounds bounds = ChunkBounds.of(pocket);
        int claimed = 0;

        for (int cx = bounds.minX(); cx <= bounds.maxX(); cx++) {
            for (int cz = bounds.minZ(); cz <= bounds.maxZ(); cz++) {
                ChunkAccess chunk = level.getChunk(cx, cz);
                if (!isClaimed(chunk)) {
                    POCKET_GENERATED.set(chunk, true);
                    chunk.setUnsaved(true);
                    claimed++;
                }
            }
        }

        return claimed;
    }

    private static boolean isClaimed(ChunkAccess chunk) {
        return Boolean.TRUE.equals(POCKET_GENERATED.get(chunk));
    }
}
