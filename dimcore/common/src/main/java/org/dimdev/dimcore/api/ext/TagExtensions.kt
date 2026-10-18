package org.dimdev.dimcore.api.ext

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

fun Tag.at(path: List<String>): Tag? = path.fold<String, Tag?>(this) { tag, key ->
    when (tag) {
        is CompoundTag -> tag.get(key)
        is ListTag -> key.toIntOrNull()?.let(tag::getOrNull)
        else -> null
    }
}

fun CompoundTag.putAt(path: List<String>, value: Tag?) {
    if (path.isEmpty()) return
    val key = path.last()

    when (val parent = at(path.dropLast(1))) {
        is CompoundTag -> if (value == null) parent.remove(key) else parent.put(key, value)
        is ListTag -> key.toIntOrNull()?.takeIf { it in parent.indices }?.let { if (value == null) parent.removeAt(it) else parent[it] = value }
        else -> {}
    }
}
