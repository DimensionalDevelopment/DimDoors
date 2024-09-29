package org.dimdev.dimdoors.world.level.component.neoforge;

import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.world.DataKeys;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.world.neoforge.DataKeysImpl;

public class ChunkLazilyGeneratedComponentImpl {
    public static ChunkLazilyGeneratedComponent get(LevelChunk  chunk) {
        return chunk.getData(((DataKeysImpl.DataKeyImpl<ChunkLazilyGeneratedComponent>) DataKeys.CHUNK_LAZILY_GENERATED).type());
    }
}
