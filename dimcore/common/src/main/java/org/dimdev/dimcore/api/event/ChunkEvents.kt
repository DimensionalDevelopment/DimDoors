package org.dimdev.dimcore.api.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess
import org.dimdev.dimcore.util.util.SimpleEvent

object ChunkEvents {
    @JvmField val ALLOW: SimpleEvent<(ServerLevel, ChunkAccess) -> Boolean> = SimpleEvent.of { listeners -> { level, chunk ->
            listeners.all { listener -> listener.invoke(level, chunk) }
        }
    }
}