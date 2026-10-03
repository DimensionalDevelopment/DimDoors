package org.dimdev.dimdoors.api.rift.target

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.api.util.BlockPosUtil
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.CoordinateTransformerBlock
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.util.LevelSpaceHelper

object TargetResolver {
    fun <T : Target> target(location: Location?, clazz: Class<T>): T? = location?.run { target(this.world, this.blockPos, clazz) }

    fun <T : Target> target(
        level: ServerLevel,
        pos: BlockPos,
        clazz: Class<T>,
        function: ResolveFunction<T>? = null
    ): T? = BlockPosUtil.nearbyVertical(pos) { p ->
        LevelSpaceHelper.INSTANCE.getBlockEntity(level, p)?.cast(clazz) ?:
        LevelSpaceHelper.INSTANCE.getBlockState(level, p).block.cast(clazz) ?:
        function?.resolve(level, p)?.cast(clazz)
    }

    fun target(location: Location?): Target? = location?.run { target(this.world, this.blockPos, Target::class.java) }

    fun entity(location: Location?): EntityTarget? = location?.run { entity(this.world, this.blockPos) }

    fun entity(level: ServerLevel, pos: BlockPos): EntityTarget? = target(level, pos, EntityTarget::class.java, TargetResolver::blockStateEntity)

    fun interface ResolveFunction<V> {
        fun resolve(level: ServerLevel, pos: BlockPos): V?
    }

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
            is RiftVariantProvider -> block.getRiftProviderState(state)?.takeIf { it.block is CoordinateTransformerBlock }
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