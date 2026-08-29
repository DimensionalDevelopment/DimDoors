package org.dimdev.dimdoors.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;

/**
 * Base integration point for alternate level-space behavior.
 *
 * <p>Every method here implements the ordinary Minecraft case, where level space and world space are
 * the same thing, and leaves its input unchanged. Implementations override them for levels whose
 * coordinates, orientation, movement, or availability differ — projection between the two spaces,
 * level-space availability, and teleport-frame conversion.</p>
 *
 * <p>Compatibility code supplies an implementation by replacing {@link #INSTANCE} at startup. This
 * class deliberately names none, so that each integration stays self-contained and removable.</p>
 */
public class LevelSpaceHelper {
    /** Replaced during compatibility initialization; the default leaves all behavior unchanged. */
    public static LevelSpaceHelper INSTANCE = new LevelSpaceHelper();

    /**
     * Gets a block entity, letting an implementation resolve or load its level space beforehand.
     *
     * @return the block entity at {@code pos}, or {@code null} when none exists
     */
    public BlockEntity getBlockEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    /**
     * Validates that a teleport destination can be resolved and used. Implementations may reject
     * destinations whose level space cannot currently be accessed or prepared.
     */
    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
    }

    /**
     * Prepares the level space containing {@code pos} before DimDoors places or registers rift data.
     *
     * @return {@code true} when rift creation may continue
     */
    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        return true;
    }

    /**
     * Projects a teleport frame into the destination coordinate space.
     *
     * @param location the target rift location, when one is known
     */
    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    /**
     * Converts a teleport frame from world space into the source level space, before the destination
     * is projected.
     *
     * @param entity the teleporting entity, when available
     */
    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    /**
     * Converts entity after-block collision data into the coordinate space DimDoors expects.
     */
    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        return new AfterBlockData(box, previousPos, currentPos);
    }

    /**
     * Collision and movement data used when evaluating after-block behavior.
     *
     * @param box the collision box to evaluate
     * @param previousPos the entity's position before the move
     * @param currentPos the entity's position after it
     */
    public record AfterBlockData(AABB box, Vec3 previousPos, Vec3 currentPos) {
    }

    /**
     * Position, rotation, and velocity for one teleport.
     *
     * <p>They travel together so that all three are transformed through the same spatial context
     * rather than drifting apart across coordinate spaces.</p>
     *
     * @param pos the teleport position
     * @param angle the teleport rotation
     * @param velocity the teleport velocity
     */
    public record TeleportFrame(Vec3 pos, Rotations angle, Vec3 velocity) {
    }
}
