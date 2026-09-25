package org.dimdev.dimdoors.api.rift.target

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.api.util.BlockPosUtil
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.CoordinateTransformerBlock
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.util.LevelSpaceHelper

object TargetResolver {
    fun <T : Target> target(location: Location?): T? {
        return location?.run { target<T>(this.world, this.blockPos, null) }
    }

    fun <T : Target> target(level: ServerLevel, pos: BlockPos): T? = target<T>(level, pos, null)

    fun <T : Target> target(
        level: ServerLevel,
        pos: BlockPos,
        function: ResolveFunction<T>? = null
    ): T? = BlockPosUtil.nearbyVertical(pos) { p ->
        castOrNull(level, p, LevelSpaceHelper.INSTANCE::getBlockEntity) ?:
        castOrNull(level, p) { level, pos -> LevelSpaceHelper.INSTANCE.getBlockState(level, pos).block } ?:
        castOrNull(level, p, function)
    }


    fun target(location: Location?): Target? = location?.run { target(this.world, this.blockPos) }

    fun entity(location: Location?): EntityTarget? = location?.run { entity(this.world, this.blockPos) }

    fun entity(level: ServerLevel, pos: BlockPos): EntityTarget? = target(level, pos, TargetResolver::blockStateEntity)

    fun interface ResolveFunction<V> {
        fun resolve(level: ServerLevel, pos: BlockPos): V?
    }

    fun <T, V> castOrNull(
        level: ServerLevel,
        pos: BlockPos,
        function: ResolveFunction<V>?,
    ): T? = function?.resolve(level, pos)?.castOrNull()

    fun blockStateEntity(level: ServerLevel, pos: BlockPos): EntityTarget? {
        var pos = pos
        var state = level.getBlockState(pos)

        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below()
            state = level.getBlockState(pos)
        }

        val block = state.block
        return when (block) {
            is CoordinateTransformerBlock -> state
            is RiftVariantProvider -> block.getRiftProviderState(state).takeIf { block.castOrNull<CoordinateTransformerBlock>() != null }
            else -> null
        }?.let {
            EntityTarget { entity: Entity, relPos: Vec3, relAngle: Rotations, relVel: Vec3, location: Location? ->
                EntranceRiftBlockEntity.receiveEntityAt(
                    level,
                    pos,
                    it,
                    entity,
                    relPos,
                    relAngle,
                    relVel,
                    location
                )
            }
        }
    }
}