package org.dimdev.dimdoors.world.level.component.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.ModAttachmentTypes;

public class ChunkLazilyGeneratedComponentImpl {
    public static void setGenerated(LevelChunk chunk, boolean value) {
        chunk.setAttached(ModAttachmentTypes.HAS_BEEN_LAZY_GENNED, value);
    }

    public static boolean isGenerated(LevelChunk chunk) {
//        ServerChunkEvents.CHUNK_LOAD.register(new ServerChunkEvents.Load() {
//            @Override
//            public void onChunkLoad(ServerLevel serverLevel, LevelChunk levelChunk) {
//
//            }
//        });

        return chunk.hasAttached(ModAttachmentTypes.HAS_BEEN_LAZY_GENNED) ? chunk.getAttached(ModAttachmentTypes.HAS_BEEN_LAZY_GENNED) : false;
    }
}
