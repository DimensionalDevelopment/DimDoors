package org.dimdev.dimdoors.api.event

import org.dimdev.dimcore.api.util.SimpleEvent
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar

fun interface RegisterDimensionalDoorProductionsCallback {
    fun register(registrar: DimensionalDoorBlockRegistrar)

    companion object {
        @JvmField
        val EVENT: SimpleEvent<RegisterDimensionalDoorProductionsCallback> = SimpleEvent.of { callbacks -> { registrar ->
                callbacks.forEach { callback -> callback.register(registrar) }
            }
        }
    }
}
