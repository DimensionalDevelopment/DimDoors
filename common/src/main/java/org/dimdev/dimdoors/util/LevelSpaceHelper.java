package org.dimdev.dimdoors.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.compat.sable.SableLevelSpaceHelper;
import org.dimdev.dimdoors.rift.registry.Rift;

/**
 * Base integration point for alternate level-space behavior.
 *
 * <p>This class provides the default behavior for ordinary Minecraft levels, where level space
 * and world space are equivalent. Specialized implementations may override this behavior for
 * levels whose coordinates, orientation, movement, or availability differ from normal world
 * space.</p>
 *
 * <p>{@link SableLevelSpaceHelper} provides the active implementation used by Sable integration.
 * It handles projection between level space and world space, level-space availability, rift
 * tracking, and teleport-frame conversion.</p>
 *
 * <p>All methods in this base implementation preserve normal DimDoors behavior and leave
 * coordinates, rotations, velocities, block lookups, rift state, and collision data unchanged.</p>
 */
public class LevelSpaceHelper {
    /**
     * Global helper instance used by DimDoors systems that may interact with alternate level spaces.
     *
     * <p>This defaults to the standard no-op implementation and may be replaced by a specialized
     * implementation during compatibility initialization.</p>
     */
    public static LevelSpaceHelper INSTANCE = new LevelSpaceHelper();

    /**
     * Checks whether the level space containing a position is currently unavailable.
     *
     * <p>The base implementation always returns {@code false} because ordinary Minecraft level
     * space is assumed to be available.</p>
     *
     * @param level the server level to inspect
     * @param pos the block position to check
     * @return {@code true} when the corresponding level space cannot currently be accessed
     */
    public boolean isLevelSpaceUnavailable(ServerLevel level, BlockPos pos) {
        return false;
    }

    /**
     * Gets a block entity at a position, allowing specialized implementations to prepare or resolve
     * the corresponding level space before the lookup.
     *
     * <p>The base implementation performs a normal level block-entity lookup.</p>
     *
     * @param level the server level containing the block entity
     * @param pos the block position to query
     * @return the block entity at {@code pos}, or {@code null} when none exists
     */
    public BlockEntity getBlockEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    /**
     * Validates that a teleport destination can be resolved and used.
     *
     * <p>The base implementation does nothing. Specialized implementations may reject destinations
     * whose level space cannot currently be accessed or prepared.</p>
     *
     * @param level the destination level
     * @param pos the destination position
     */
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
    }

    /**
     * Prepares the level space containing a position for rift creation.
     *
     * <p>Specialized implementations may need to resolve, load, or otherwise prepare the
     * corresponding level space before DimDoors places or registers rift data. The base
     * implementation always returns {@code true}.</p>
     *
     * @param level the server level where the rift will be created
     * @param pos the rift block position
     * @return {@code true} when rift creation may continue
     */
    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        return true;
    }

    /**
     * Updates any level-space tracking data associated with a rift.
     *
     * <p>The base implementation does nothing. Specialized implementations may use tracking data to
     * recover the spatial state associated with rifts whose level space can move or become
     * unavailable.</p>
     *
     * @param level the server level containing the rift
     * @param rift the rift whose tracking data should be updated
     */
    public void updateRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    /**
     * Removes any level-space tracking data associated with a rift.
     *
     * <p>The base implementation does nothing.</p>
     *
     * @param level the server level containing the rift
     * @param rift the rift whose tracking data should be removed
     */
    public void removeRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    /**
     * Projects a teleport frame into the destination coordinate space.
     *
     * <p>Specialized implementations may transform the position, angle, and velocity through the
     * destination level-space transform. The base implementation returns the frame unchanged.</p>
     *
     * @param level the destination server level
     * @param location the target rift location, when one is known
     * @param pos the destination position
     * @param angle the destination rotation
     * @param velocity the destination velocity
     * @return the projected teleport frame
     */
    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    /**
     * Converts a teleport frame from the source coordinate space.
     *
     * <p>Specialized implementations may transform the position, angle, and velocity from world
     * space into the source level space before DimDoors projects the destination. The base
     * implementation returns the frame unchanged.</p>
     *
     * @param level the source server level
     * @param sourcePos the source rift/block position
     * @param entity the teleporting entity, when available
     * @param pos the source position
     * @param angle the source rotation
     * @param velocity the source velocity
     * @return the converted source teleport frame
     */
    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    /**
     * Converts entity after-block collision data into the coordinate space expected by DimDoors.
     *
     * <p>Specialized implementations may transform collision bounds and movement positions into the
     * appropriate level space. The base implementation returns the data unchanged.</p>
     *
     * @param entity the entity being checked
     * @param box the collision box
     * @param previousPos the previous position
     * @param currentPos the current position
     * @return converted after-block collision data
     */
    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        return new AfterBlockData(box, previousPos, currentPos);
    }

    /**
     * Collision and movement data used when evaluating after-block behavior.
     *
     * @param box the collision box to evaluate
     * @param previousPos the entity's previous position
     * @param currentPos the entity's current position
     */
    public record AfterBlockData(AABB box, Vec3 previousPos, Vec3 currentPos) {
    }

    /**
     * Position, rotation, and velocity state used during DimDoors teleport projection.
     *
     * <p>A teleport frame may be transformed between the coordinate spaces involved in
     * teleportation. Keeping these values together ensures that position, angle, and velocity are
     * transformed through the same spatial context.</p>
     *
     * @param pos the teleport position
     * @param angle the teleport rotation
     * @param velocity the teleport velocity
     */
    public record TeleportFrame(Vec3 pos, Rotations angle, Vec3 velocity) {
    }
}