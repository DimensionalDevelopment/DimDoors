package org.dimdev.dimdoors.entity.mask

import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.util.SimpleEvent

object MaskEvents {
    @JvmField val ALLOW_DETECT: SimpleEvent<(MaskEntity, ServerPlayer) -> Boolean> = allow()
    @JvmField val ALLOW_CATCH: SimpleEvent<(MaskEntity, ServerPlayer) -> Boolean> = allow()

    @JvmField
    val ALLOW_WORN_EFFECTS: SimpleEvent<(ServerPlayer, MaskType) -> Boolean> = allow()

    @JvmField
    val DETECTION_RANGE: SimpleEvent<(MaskEntity, ServerPlayer, Double) -> Double> = SimpleEvent.of { listeners -> { mask, player, range ->
            listeners.fold(range) { current, listener -> listener.invoke(mask, player, current) }
        }
    }

    @JvmField val ALERT: SimpleEvent<(MaskEntity, ServerPlayer) -> Unit> = event()
    @JvmField val STUN: SimpleEvent<(MaskEntity, ServerPlayer) -> Unit> = event()
    @JvmField val SHATTER: SimpleEvent<(MaskEntity, ServerPlayer) -> Unit> = event()
    @JvmField val CATCH: SimpleEvent<(MaskEntity, ServerPlayer) -> Unit> = event()

    @JvmField val WORN_TICK: SimpleEvent<(ServerPlayer, MaskType) -> Unit> = event()
    @JvmField val MASKS_REMOVED: SimpleEvent<(ServerPlayer, MaskType) -> Unit> = event()

    private fun <A, B> allow(): SimpleEvent<(A, B) -> Boolean> = SimpleEvent.of { listeners -> { a, b ->
            listeners.all { listener -> listener.invoke(a, b) }
        }
    }

    private fun <A, B> event(): SimpleEvent<(A, B) -> Unit> = SimpleEvent.of { listeners -> { a, b ->
            listeners.forEach { listener -> listener.invoke(a, b) }
        }
    }
}
