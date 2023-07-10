package org.dimdev.dimdoors.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkServedCallback {
    Event<ChunkServedCallback> EVENT = EventFactory.createLoop();

    void onChunkServed(ServerLevel level, LevelChunk chunk);
}
