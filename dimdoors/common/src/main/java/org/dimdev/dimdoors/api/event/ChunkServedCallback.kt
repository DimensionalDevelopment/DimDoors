package org.dimdev.dimdoors.api.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import org.dimdev.dimcore.api.util.SimpleEvent

fun interface ChunkServedCallback {
    fun onChunkServed(level: ServerLevel, chunk: LevelChunk)

    companion object {
        @JvmField
        val EVENT: SimpleEvent<ChunkServedCallback> = SimpleEvent.of { callbacks -> { level, chunk ->
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
