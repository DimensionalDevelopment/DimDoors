package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.block.AfterMoveCollidableBlock
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock
import org.dimdev.dimdoors.api.entity.LastPositionProvider
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.RiftUtils

interface TraversableRiftBlock<T : EntranceRiftBlockEntity<*>> : RiftProvider<T>, ExplosionConvertibleBlock, AfterMoveCollidableBlock, CustomBreakHandling, CoordinateTransformerBlock {
    override fun customDestroy(level: Level, pos: BlockPos, state: BlockState, i: Int, j: Int): Boolean {
        val blockEntity = getRift(level, pos, state) ?: return false

        blockEntity.detach()
        return true
    }

    fun onCollision(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        entity: Entity,
        previousPos: Vec3,
        currentPos: Vec3
    ): InteractionResult {
        var state = state
        var pos = pos
        pos = getRiftPos(world, pos, state)

        state = world.getBlockState(pos)

        if (!validStateForTraversal(state)) return InteractionResult.PASS

        val rift = this.getRift(world, pos, state)

        if (rift == null || rift.hasTraversed(world, previousPos, currentPos)) {
            return InteractionResult.PASS
        }

        // TODO: replace with dimdoor cooldown?
        if (entity.isOnPortalCooldown) return InteractionResult.PASS

        entity.setPortalCooldown()

        if (!rift.teleport(entity)) return InteractionResult.PASS

        postTraverseEffect(world, pos, state, rift)

        return InteractionResult.SUCCESS
    }

    fun postTraverseEffect(level: Level, pos: BlockPos, state: BlockState, rift: Rift) {}

    fun validStateForTraversal(state: BlockState): Boolean = true

    fun getPortalPlane(state: BlockState, pos: BlockPos): RiftUtils.PortalPlane

    fun getVisualBlockState(state: BlockState): BlockState = state

    fun closeRift(level: Level, pos: BlockPos, state: BlockState)


    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) {
        val entity = level.getBlockEntity(pos)

        if (entity is EntranceRiftBlockEntity<*>) {
            level.setBlock(pos, ModBlocks.DETACHED_RIFT.value().defaultBlockState(), 3)

            level.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).ifPresent { detachedRiftBlockEntity -> detachedRiftBlockEntity.copyFrom(entity) }
        }
    }

    fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) {
        if (world.isClientSide || entity is ServerPlayer) {
            return
        }

        onCollision(state, world, pos, entity, (entity as LastPositionProvider).lastPos, entity.position())
    }

    override fun onAfterMovePlayerCollision(
        state: BlockState,
        world: ServerLevel,
        pos: BlockPos,
        player: ServerPlayer,
        previousPos: Vec3,
        currentPos: Vec3
    ) {
        onCollision(state, world, pos, player, previousPos, currentPos)
    }
}
