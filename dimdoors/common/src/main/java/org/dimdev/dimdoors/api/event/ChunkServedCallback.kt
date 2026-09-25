package org.dimdev.dimdoors.api.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import org.dimdev.dimcore.api.util.SimpleEvent

interface ChunkServedCallback {
    fun onChunkServed(level: ServerLevel, chunk: LevelChunk)

    companion object {
        @JvmField
        val EVENT: SimpleEvent<ChunkServedCallback> = SimpleEvent.of { callbacks ->
            object : ChunkServedCallback {
                override fun onChunkServed(level: ServerLevel, chunk: LevelChunk) {
                    callbacks.forEach { callback ->
                        callback.onChunkServed(
                            level,
                            chunk
                        )
                    }
                }
            }
        }
    }
}
