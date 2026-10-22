package org.dimdev.dimcore.api.event

import org.dimdev.dimcore.api.util.SimpleEvent

object DebugTextEvents {
    @JvmField val SYSTEM: SimpleEvent<(MutableList<String>) -> Unit> = SimpleEvent.of { listeners -> { list -> listeners.forEach { listener -> listener.invoke(list) } } }
    @JvmField val GAME: SimpleEvent<(MutableList<String>) -> Unit> = SimpleEvent.of { listeners -> { list -> listeners.forEach { listener -> listener.invoke(list) } } }
}