package org.dimdev.dimdoors.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.Rift;

/**
 * Base integration point for optional Sable support.
 *
 * <p>This class is intentionally a no-op implementation. It is used when Sable is not active,
 * not present, or when DimDoors code needs to call Sable-aware behavior without hard-depending on
 * Sable's runtime classes.</p>
 *
 * <p>{@link ActiveSableHelper} overrides this class when Sable support is available. The active
 * implementation handles projection between Minecraft world space and Sable sub-level local space,
 * sub-level loading, rift tracking points, and teleport-frame conversion.</p>
 *
 * <p>All methods in this base class should preserve normal DimDoors behavior. They must not alter
 * coordinates, rotations, velocities, block lookups, rift state, or collision data unless an active
 * Sable implementation is installed.</p>
 */
public class SableHelper {
    /**
     * Global helper instance used by DimDoors systems that need optional Sable behavior.
     *
     * <p>This defaults to the no-op helper and is replaced with an active implementation when Sable
     * compatibility is initialized.</p>
     */
    public static SableHelper INSTANCE = new SableHelper();

    /**
     * Projects a block position out of Sable sub-level local space into its displayed world-space
     * position.
     *
     * <p>The base implementation returns {@code pos} unchanged.</p>
     *
     * @param level the level containing the position
     * @param pos the position to project
     * @return the projected world-space position, or {@code pos} when Sable is inactive
     */
    public BlockPos projectFrom(Level level, BlockPos pos) {
        return pos;
    }

    /**
     * Projects a vector position out of Sable sub-level local space into its displayed world-space
     * position.
     *
     * <p>The base implementation returns {@code pos} unchanged.</p>
     *
     * @param level the level containing the position
     * @param pos the position to project
     * @return the projected world-space position, or {@code pos} when Sable is inactive
     */
    public Vec3 projectFrom(Level level, Vec3 pos) {
        return pos;
    }

    /**
     * Projects a world-space position into the local space of the Sable sub-level containing it.
     *
     * <p>The base implementation returns {@code pos} unchanged.</p>
     *
     * @param level the server level containing the position
     * @param pos the world-space position to project
     * @return the local sub-level position, or {@code pos} when Sable is inactive
     */
    public Vec3 projectTo(ServerLevel level, Vec3 pos) {
        return pos;
    }

    /**
     * Checks whether a position is inside an occupied Sable plot whose live plot chunk holder is
     * missing.
     *
     * <p>The base implementation always returns {@code false} because no Sable plot grid is active.</p>
     *
     * @param level the server level to inspect
     * @param pos the block position to check
     * @return {@code true} only when an active Sable implementation detects a missing plot holder
     */
    public boolean isMissingSablePlotHolder(ServerLevel level, BlockPos pos) {
        return false;
    }

    /**
     * Gets a block entity at a position, allowing active Sable implementations to load/materialize
     * the containing sub-level before the lookup.
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
     * Validates that a teleport destination is usable.
     *
     * <p>The base implementation does nothing. Active Sable implementations may throw when the
     * target is inside an occupied Sable plot that cannot be materialized.</p>
     *
     * @param level the destination level
     * @param pos the destination position
     */
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
    }

    /**
     * Prepares a position for rift creation.
     *
     * <p>Active Sable implementations may need to materialize a sub-level or create a local plot
     * chunk before DimDoors places or registers rift data. The base implementation always returns
     * {@code true}.</p>
     *
     * @param level the server level where the rift will be created
     * @param pos the rift block position
     * @return {@code true} when rift creation may continue
     */
    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        return true;
    }

    /**
     * Updates any Sable tracking point associated with a rift.
     *
     * <p>The base implementation does nothing. Active Sable implementations use tracking points to
     * recover the pose of unloaded or moved sub-levels that contain rifts.</p>
     *
     * @param level the server level containing the rift
     * @param rift the rift whose tracking point should be updated
     */
    public void updateRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    /**
     * Removes any Sable tracking point associated with a rift.
     *
     * <p>The base implementation does nothing.</p>
     *
     * @param level the server level containing the rift
     * @param rift the rift whose tracking point should be removed
     */
    public void removeRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    /**
     * Projects a teleport frame into the destination coordinate space.
     *
     * <p>Active Sable implementations use this when the destination may be inside a Sable sub-level.
     * The position, angle, and velocity are transformed through the destination sub-level pose when
     * needed. The base implementation returns the frame unchanged.</p>
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
     * <p>Active Sable implementations use this when the source is inside a Sable sub-level. The
     * position, angle, and velocity are converted from world space into the source sub-level's local
     * space before DimDoors projects the destination. The base implementation returns the frame
     * unchanged.</p>
     *
     * @param level the source server level
     * @param sourcePos the source rift/block position
     * @param entity the teleporting entity, when available
     * @param pos the source position
     * @param angle the source rotation
     * @param velocity the source velocity
     * @return the source-local teleport frame
     */
    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    /**
     * Converts entity after-block collision data into the coordinate space expected by DimDoors.
     *
     * <p>Active Sable implementations may project collision boxes and movement positions into a
     * tracked sub-level's local space. The base implementation returns the data unchanged.</p>
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
     * Collision/movement data used when evaluating after-block behavior.
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
     * <p>A teleport frame may be transformed from world space into Sable local space or from Sable
     * local space back into world space. Keeping these values together prevents position, angle, and
     * velocity from being transformed through different coordinate contexts.</p>
     *
     * @param pos the teleport position
     * @param angle the teleport rotation
     * @param velocity the teleport velocity
     */
    public record TeleportFrame(Vec3 pos, Rotations angle, Vec3 velocity) {

    }
}
