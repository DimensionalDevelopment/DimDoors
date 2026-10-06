package org.dimdev.dimdoors.api.rift.target

import org.dimdev.dimdoors.rift.registry.Vertex

interface Target {
    // Allows a target to have a default case and forward everything
    // it doesn't handle to a different target. You should never call
    // this, it's just public because Java doesn't allow protected.
    fun receiveOther(owner: Vertex): Target? = null

    fun <T : Target?> `as`(type: Class<T>, owner: Vertex): T? = if (type.isAssignableFrom(this.javaClass)) {
        type.cast(this)
    } else {
        this.receiveOther(owner)?.`as`<T>(type, owner) ?: DefaultTargets.getDefaultTarget<T>(type)
    }
}
