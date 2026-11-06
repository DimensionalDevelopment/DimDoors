package org.dimdev.dimdoors.block.entity

interface RecordCodecProvider<V> {

    open fun <T> serialize(serialize: Serialize<T, V>) {}
    open fun <T> deserialize(nbt: Deserialize<T>) {}
}