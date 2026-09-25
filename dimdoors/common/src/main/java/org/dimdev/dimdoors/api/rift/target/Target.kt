package org.dimdev.dimdoors.api.rift.target

interface Target {
    // Allows a target to have a default case and forward everything
    // it doesn't handle to a different target. You should never call
    // this, it's just public because Java doesn't allow protected.
    fun receiveOther(): Target? = null

    fun <T : Target?> `as`(type: Class<T>): T? = if (type.isAssignableFrom(this.javaClass)) {
        type.cast(this)
    } else {
        this.receiveOther()?.`as`<T>(type) ?: DefaultTargets.getDefaultTarget<T>(type)
    }
}
