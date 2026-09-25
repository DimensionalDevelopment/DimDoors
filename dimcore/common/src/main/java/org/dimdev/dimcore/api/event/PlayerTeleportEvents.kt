package org.dimdev.dimcore.api.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.util.SimpleEvent

object PlayerTeleportEvents {
    @JvmField val BEFORE: SimpleEvent<(ServerPlayer, ServerLevel, Vec3) -> Unit> = event()

    @JvmField
    val AFTER: SimpleEvent<(ServerPlayer, ServerLevel, Vec3) -> Unit> = event()

    private fun event(): SimpleEvent<(ServerPlayer, ServerLevel, Vec3) -> Unit> = SimpleEvent.of { listeners -> { player, level, pos ->
            listeners.forEach { listener ->
                listener.invoke(player, level, pos)
            }
        }
    }
}
