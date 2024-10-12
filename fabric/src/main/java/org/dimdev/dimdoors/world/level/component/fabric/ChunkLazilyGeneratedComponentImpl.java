package org.dimdev.dimdoors.world.level.component.fabric;

import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.world.DataKeys;
import org.dimdev.dimdoors.world.fabric.DataKeysImpl;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;

public class ChunkLazilyGeneratedComponentImpl {
    public static ChunkLazilyGeneratedComponent get(LevelChunk chunk) {
        var key = ((DataKeysImpl.FabricDataKey<ChunkLazilyGeneratedComponent>) DataKeys.CHUNK_LAZILY_GENERATED).type();
        return chunk.getAttachedOrCreate(key);
    }
}
