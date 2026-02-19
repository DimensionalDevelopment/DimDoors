package org.dimdev.dimdoors.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("deprecation")
public final class TeleportUtil {

    private TeleportUtil() {
    }

    public static Entity teleport(Entity entity, Level world, BlockPos pos, float yaw) {
        return teleport(entity, world, Vec3.atBottomCenterOf(pos), yaw);
    }

    public static Entity teleport(Entity entity, Level world, Vec3 pos, float yaw) {
        // NOTE: use XRot (pitch), not X (world coordinate)
        return teleport(entity, world, pos, new Rotations(entity.getXRot(), yaw, 0.0F), entity.getDeltaMovement());
    }

    public static Vec3 clampToWorldBorder(Vec3 original, WorldBorder border) {
        double newX = original.x;
        double newZ = original.z;
        double size = border.getSize() - 1;
        double northBound = border.getMinZ() + 1;
        double southBound = border.getMaxZ() - 1;
        double westBound = border.getMinX() + 1;
        double eastBound = border.getMaxX() - 1;

        if (newZ < northBound) {
            newZ = northBound + Math.abs(newZ % size) + 1;
        } else if (newZ > southBound) {
            newZ = southBound - Math.abs(newZ % size) - 1;
        }

        if (newX < westBound) {
            newX = westBound + Math.abs(newX % size) + 1;
        } else if (newX > eastBound) {
            newX = eastBound - Math.abs(newX % size) - 1;
        }

        return new Vec3(newX, original.y, newZ);
    }

    public static Entity teleport(Entity entity, Level world, Vec3 pos, Rotations angle, Vec3 velocity) {
        if (world.isClientSide()) {
            throw new UnsupportedOperationException("Only supported on ServerWorld");
        }

        // Force cast; we already asserted server side
        ServerLevel serverWorld = (ServerLevel) world;

        // Clamp inside world border
        pos = clampToWorldBorder(pos, serverWorld.getWorldBorder());
        float yaw = Mth.wrapDegrees(angle.getY());
        float pitch = Mth.clamp(Mth.wrapDegrees(angle.getX()), -90.0F, 90.0F);

        // Ensure the target chunk is loaded, vanilla-style
        BlockPos targetPos = BlockPos.containing(pos);
        ChunkPos chunkPos = new ChunkPos(targetPos);
        serverWorld.getChunkSource().addRegionTicket(
                TicketType.POST_TELEPORT,
                chunkPos,
                1,
                entity.getId()
        );

        if (entity instanceof ServerPlayer serverPlayer) {
            entity.stopRiding();

            if (entity.level().dimension().equals(serverWorld.dimension())) {
                // Intra-dimension; this method also does safety checks
                serverPlayer.connection.teleport(pos.x(), pos.y(), pos.z(), yaw, pitch);
            } else {
                // Cross-dimension
                entity = teleport(entity, serverWorld, pos, velocity, yaw, pitch);
            }

            // If you ever re-enable this, make sure it's safe on both loaders
            // serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity.getId(), velocity));

            ServerPacketHandler.syncPocketAddonsIfNeeded(serverPlayer, serverWorld, targetPos);

            if (serverWorld.dimension() == ModDimensions.DUNGEON) {
                serverPlayer.awardStat(ModStats.TIMES_BEEN_TO_DUNGEON);
            }
        } else {
            if (entity.level().dimension().equals(serverWorld.dimension())) {
                entity.moveTo(pos.x(), pos.y(), pos.z(), yaw, pitch);
            } else {
                entity = teleport(entity, serverWorld, pos, velocity, yaw, pitch);
            }
        }

        entity.setDeltaMovement(velocity);
        return entity;
    }

    public static Entity teleport(Entity entity, Level world, BlockPos pos, Rotations angle, Vec3 velocity) {
        if (world.isClientSide()) {
            throw new UnsupportedOperationException("Only supported on ServerWorld");
        }

        return teleport(entity, world, Vec3.atBottomCenterOf(pos), angle, velocity);
    }

    public static Entity teleport(ServerPlayer player, Location location) {
        return teleport(player, DimensionalDoors.getWorld(location.world), location.pos, 0.0F);
    }

    public static Entity teleport(ServerPlayer player, RotatedLocation location) {
        return teleport(player, DimensionalDoors.getWorld(location.world), location.pos, (int) location.yaw);
    }

    public static Entity teleportRandom(Entity entity, Level world, double y) {
        double scale = ThreadLocalRandom.current().nextGaussian() * ThreadLocalRandom.current().nextInt(90);
        return teleport(
                entity,
                world,
                entity.position()
                        .subtract(0.0, entity.getY(), 0.0)
                        .add(0.0, y, 0.0)
                        .multiply(scale, 1.0, scale),
                entity.getYRot()
        );
    }

    public static Entity teleportUntargeted(Entity entity, Level world) {
        double actualScale = entity.level().dimensionType().coordinateScale() / world.dimensionType().coordinateScale();
        return teleport(
                entity,
                world,
                entity.position().multiply(actualScale, 1.0, actualScale),
                entity.getYRot()
        );
    }

    public static Entity teleportUntargeted(Entity entity, Level world, double y) {
        double actualScale = entity.level().dimensionType().coordinateScale() / world.dimensionType().coordinateScale();
        return teleport(
                entity,
                world,
                entity.position()
                        .subtract(0.0, entity.position().y(), 0.0)
                        .add(0.0, y, 0.0)
                        .multiply(actualScale, 1.0, actualScale),
                entity.getYRot()
        );
    }

    public static <E extends Entity> Entity teleport(E entity, ServerLevel world, Vec3 pos, Vec3 velocity, float yaw, float pitch) {
        // At this point the ticket has already been added by the caller.
        return entity.changeDimension(new DimensionTransition(world, pos, velocity, yaw, pitch, e -> {}));
    }
}
