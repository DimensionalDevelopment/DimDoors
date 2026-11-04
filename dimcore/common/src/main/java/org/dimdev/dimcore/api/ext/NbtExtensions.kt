package org.dimdev.dimcore.api.ext

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag


operator fun CompoundTag.set(key: String, value: String) = this.putString(key, value)

operator fun ListTag.set(index: Int, value: Double): Tag = set(index, NbtOps.INSTANCE.createDouble(value))

operator fun CompoundTag.set(key: String, value: Tag) = put(key, value)

operator fun CompoundTag.minusAssign(key: String) = remove(key)