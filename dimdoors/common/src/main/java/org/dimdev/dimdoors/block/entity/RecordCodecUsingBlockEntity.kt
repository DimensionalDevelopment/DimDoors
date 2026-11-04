package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.ext.cast

abstract class RecordCodecUsingBlockEntity<V: RecordCodecUsingBlockEntity<V>>(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) : BlockEntity(type, pos, blockState) {
    public override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) { Serialize.nbt(provider, nbt, this.cast<V>()).also(::serialize) }
    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) { Deserialize.nbt(provider, nbt).also(::deserialize) }
    open fun <T> serialize(serialize: Serialize<T, V>) {}
    open fun <T> deserialize(nbt: Deserialize<T>) {}
}