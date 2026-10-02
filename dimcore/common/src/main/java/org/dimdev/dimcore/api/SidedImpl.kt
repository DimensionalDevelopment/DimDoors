package org.dimdev.dimcore.api

abstract class SidedImpl<V : SidedImpl<V, T>, T : ModCommon<in V>>(@JvmField protected val common: T) : ISided<V> {
    override fun modId(): String {
        return common.modId
    }

}
