package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks

object ModBlockEntityTypes : PlatformRegistry.BlockEntityTypePlatformRegistry(DimensionalDoors.getSided()) {
    val DETACHED_RIFT = create("detached_rift", ::DetachedRiftBlockEntity) { ModBlocks.DETACHED_RIFT }
    val ENTRANCE_RIFT = create("entrance_rift", { pos, state -> EntranceRiftBlockEntity.Impl(pos, state) }) { ModBlocks.DIMENSIONAL_PORTAL }
    val DIALING_DOOR = create("dialing_door", ::DialingDoorBlockEntity) { ModBlocks.DIALING_DOOR }
    val TESSELATING_LOOM = create("tesselating_loom", ::TesselatingLoomBlockEntity) { ModBlocks.TESSELATING_LOOM }
    val GENERIC_RIFT = create("generic_rift", { pos: BlockPos, state: BlockState -> RiftBlockEntity.Impl(pos, state) }) { ModBlocks.LIMINAL_TRANSMITTER }


}

