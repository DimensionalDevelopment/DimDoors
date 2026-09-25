package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.block.entity.Rift
import java.util.*

interface RiftVariantProvider {
    fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState): Rift?

    fun getRiftProviderState(state: BlockState): BlockState? = null

    fun revertToBaseVariant(world: ServerLevel, pos: BlockPos, state: BlockState) {}
}
