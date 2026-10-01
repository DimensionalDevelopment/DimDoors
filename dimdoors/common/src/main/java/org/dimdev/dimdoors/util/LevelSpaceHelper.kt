package org.dimdev.dimdoors.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.Rift

/**
 * Base integration point for alternate level-space behavior.
 * 
 * 
 * Every method here implements the ordinary Minecraft case, where level space and world space are
 * the same thing, and leaves its input unchanged. Implementations override them for levels whose
 * coordinates, orientation, movement, or availability differ — projection between the two spaces,
 * level-space availability, and teleport-frame conversion.
 * 
 * 
 * Compatibility code supplies an implementation by replacing [.INSTANCE] at startup. This
 * class deliberately names none, so that each integration stays self-contained and removable.
 */
open class LevelSpaceHelper {
    /**
     * Gets a block entity, letting an implementation resolve or load its level space beforehand.
     * 
     * @return the block entity at `pos`, or `null` when none exists
     */
    open fun getBlockEntity(level: ServerLevel, pos: BlockPos): BlockEntity? {
        return level.getBlockEntity(pos)
    }

    /**
     * Gets a block state, letting an implementation resolve or load its level space beforehand.
     * 
     * @return the block state at `pos`
     */
    open fun getBlockState(level: ServerLevel, pos: BlockPos): BlockState {
        return level.getBlockState(pos)
    }


    /**
     * Validates that a teleport destination can be resolved and used. Implementations may reject
     * destinations whose level space cannot currently be accessed or prepared.
     */
    open fun validateTeleportDestination(level: ServerLevel, pos: Vec3) {
    }

    /**
     * Prepares the level space containing `pos` before DimDoors places or registers rift data.
     * 
     * @return `true` when rift creation may continue
     */
    open fun prepareRiftCreation(level: ServerLevel, pos: BlockPos): Boolean {
        return true
    }

    /**
     * Projects a teleport frame into the destination coordinate space.
     * 
     * @param location the target rift location, when one is known
     */
    open fun projectTeleportFrame(
        level: ServerLevel,
        location: Location?,
        pos: Vec3,
        angle: Rotations,
        velocity: Vec3
    ): TeleportFrame {
        return TeleportFrame(pos, angle, velocity)
    }

    /**
     * Converts a teleport frame from world space into the source level space, before the destination
     * is projected.
     * 
     * @param entity the teleporting entity, when available
     */
    open fun sourceTeleportFrame(
        level: ServerLevel,
        sourcePos: BlockPos,
        entity: Entity?,
        pos: Vec3,
        angle: Rotations,
        velocity: Vec3
    ): TeleportFrame {
        return TeleportFrame(pos, angle, velocity)
    }

    /**
     * Converts entity after-block collision data into the coordinate space DimDoors expects.
     */
    open fun getAfterBlockData(entity: Entity, box: AABB, previousPos: Vec3, currentPos: Vec3): AfterBlockData {
        return AfterBlockData(box, previousPos, currentPos)
    }

    open fun onRiftAdded(rift: Rift) {
    }

    /**
     * Collision and movement data used when evaluating after-block behavior.
     * 
     * @param box the collision box to evaluate
     * @param previousPos the entity's position before the move
     * @param currentPos the entity's position after it
     */
    data class AfterBlockData(@JvmField val box: AABB, @JvmField val previousPos: Vec3, @JvmField val currentPos: Vec3)

    /**
     * Position, rotation, and velocity for one teleport.
     * 
     * 
     * They travel together so that all three are transformed through the same spatial context
     * rather than drifting apart across coordinate spaces.
     * 
     * @param pos the teleport position
     * @param angle the teleport rotation
     * @param velocity the teleport velocity
     */
    data class TeleportFrame(val pos: Vec3, val angle: Rotations, val velocity: Vec3)
    companion object {
        /** Replaced during compatibility initialization; the default leaves all behavior unchanged.  */
        @JvmField
        var INSTANCE: LevelSpaceHelper = LevelSpaceHelper()
    }
}
