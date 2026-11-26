package org.dimdev.dimcore.api.event

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.util.util.SimpleEvent

object BlockEvents {
    @JvmField
    val AFTER_PLACE: SimpleEvent<(ServerLevel, BlockPos, BlockState, Entity?) -> Unit> = SimpleEvent.of { listeners -> { level, pos, state, placer ->
            listeners.forEach { listener -> listener.invoke(level, pos, state, placer) }
        }
    }
}
