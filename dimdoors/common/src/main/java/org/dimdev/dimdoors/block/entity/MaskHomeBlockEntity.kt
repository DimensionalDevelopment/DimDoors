package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.entity.mask.MaskEntity
import java.util.UUID

class MaskHomeBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModBlockEntityTypes.MASK_HOME, pos, state) {
    var maskId: UUID? = null
        set(value) {
            field = value
            setChanged()
        }

    fun mask(level: ServerLevel): MaskEntity? = maskId?.let { level.getEntity(it) } as? MaskEntity

    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        maskId?.let { nbt.putUUID("mask", it) }
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        maskId = if (nbt.hasUUID("mask")) nbt.getUUID("mask") else null
    }
}
