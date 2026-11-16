package org.dimdev.dimdoors.blockentity

interface RecordCodecProvider<V> {
    open fun <T> serialize(serialize: Serialize<T, V>) {}
    open fun <T> deserialize(nbt: Deserialize<T>) {}
}