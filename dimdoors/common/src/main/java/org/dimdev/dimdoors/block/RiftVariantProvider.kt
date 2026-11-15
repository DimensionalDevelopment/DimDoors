package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.blockentity.BlockEntityRift

interface RiftVariantProvider {
    fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState): BlockEntityRift<*>?

    fun getRiftProviderState(state: BlockState): BlockState? = null

    fun revertToBaseVariant(location: Location) {}
}
