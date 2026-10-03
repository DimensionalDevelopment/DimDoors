package org.dimdev.dimdoors.api.util

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.ext.castOrNull

object NbtUtil {
    fun <T> deserialize(data: CompoundTag, codec: MapCodec<T>): T = codec.decoder().parse(NbtOps.INSTANCE, data).getOrThrow()

    fun <T> deserialize(data: Tag, codec: Codec<T>): T = NbtOps.INSTANCE.withParser<T>(codec).apply(data).getOrThrow()

    fun <T> serialize(tag: CompoundTag?, data: T, codec: MapCodec<T>): CompoundTag =
        codec.encoder().encode(data, NbtOps.INSTANCE, tag).getOrThrow() as CompoundTag

    fun <T> serialize(data: T, codec: Codec<T>): Tag = NbtOps.INSTANCE.withEncoder<T>(codec).apply(data).getOrThrow()

    fun asNbtCompound(nbt: Tag?, error: String?): CompoundTag {
        return nbt?.castOrNull<CompoundTag>() ?: throw RuntimeException(error)
    }
}
