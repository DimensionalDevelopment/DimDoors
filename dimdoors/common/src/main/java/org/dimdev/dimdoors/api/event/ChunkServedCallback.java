package org.dimdev.dimdoors.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimcore.api.util.SimpleEvent;

public interface ChunkServedCallback {
    SimpleEvent<ChunkServedCallback> EVENT = SimpleEvent.of(callbacks -> (level, chunk) -> callbacks.forEach(callback -> callback.onChunkServed(level, chunk)));

    void onChunkServed(ServerLevel level, LevelChunk chunk);
}
