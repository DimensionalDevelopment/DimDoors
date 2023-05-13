package org.dimdev.dimdoors.api.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("deprecation")
public final class TeleportUtil {
	public static  <E extends Entity> E teleport(E entity, Level world, BlockPos pos, float yaw) {
		return teleport(entity, world, Vec3.atBottomCenterOf(pos), yaw);
	}

	public static  <E extends Entity> E teleport(E entity, Level world, Vec3 pos, float yaw) {
		return teleport(entity, world, pos, new Rotations((float) entity.getX(), yaw, 0), entity.getDeltaMovement());
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

	public static  <E extends Entity> E teleport(E entity, Level world, Vec3 pos, Rotations angle, Vec3 velocity) {
		if (world.isClientSide()) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		// Some insurance
		pos = clampToWorldBorder(pos, world.getWorldBorder());
		float yaw = Mth.wrapDegrees(angle.getY());
		float pitch = Mth.clamp(Mth.wrapDegrees(angle.getX()), -90.0F, 90.0F);

		if (entity instanceof ServerPlayer serverPlayer) {
			// This is what the vanilla tp command does. Let's hope this works.
			ChunkPos chunkPos = new ChunkPos(new BlockPos(new Vec3i((int) pos.x, (int) pos.y, (int) pos.z)));
			((ServerLevel) world).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
			entity.stopRiding();

			if (entity.level.dimension().equals(world.dimension())) {
				serverPlayer.connection.teleport(pos.x(), pos.y(), pos.z(), yaw, pitch);
			} else {
				entity = teleport(entity, (ServerLevel) world, new PortalInfo(pos, velocity, yaw, pitch));
			}

			serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity.getId(), velocity));
			((ExtendedServerPlayNetworkHandler) (serverPlayer.connection)).getDimDoorsPacketHandler().syncPocketAddonsIfNeeded(world, new BlockPos((int) pos.x, (int) pos.y, (int) pos.z));

			if (world.dimension() == ModDimensions.DUNGEON) {
				serverPlayer.awardStat(ModStats.TIMES_BEEN_TO_DUNGEON.get());
			}
		} else {
			if (entity.level.dimension().equals(world.dimension())) {
				entity.moveTo(pos.x(), pos.y(), pos.z(), yaw, pitch);
			} else {
				entity = teleport(entity, (ServerLevel) world, new PortalInfo(pos, velocity, yaw, pitch));
			}
		}
		entity.setDeltaMovement(velocity);

		return entity;
	}

	public static  <E extends Entity> E teleport(E entity, Level world, BlockPos pos, Rotations angle, Vec3 velocity) {
		if (world.isClientSide()) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		return teleport(entity, world, Vec3.atBottomCenterOf(pos), angle, velocity);
	}

	public static ServerPlayer teleport(ServerPlayer player, Location location) {
		return teleport(player, DimensionalDoors.getWorld(location.world), location.pos, 0);
	}

	public static ServerPlayer teleport(ServerPlayer player, RotatedLocation location) {
		return teleport(player, DimensionalDoors.getWorld(location.world), location.pos, (int) location.yaw);
	}

	public static  <E extends Entity> E teleportRandom(E entity, Level world, double y) {
		double scale = ThreadLocalRandom.current().nextGaussian() * ThreadLocalRandom.current().nextInt(90);
		return teleport(
				entity,
				world,
				entity.position()
						.subtract(0, entity.getY(), 0)
						.add(0, y, 0)
						.multiply(scale, 1, scale),
				entity.getYRot()
		);
	}
	public static  <E extends Entity> E teleportUntargeted(E entity, Level world) {
		double actualScale = entity.level.dimensionType().coordinateScale() / world.dimensionType().coordinateScale();
		return teleport(
				entity,
				world,
				entity.position().multiply(actualScale, 1, actualScale),
				entity.getYRot()
		);
	}

	public static  <E extends Entity> E teleportUntargeted(E entity, Level world, double y) {
		double actualScale = entity.level.dimensionType().coordinateScale() / world.dimensionType().coordinateScale();
		return teleport(
				entity,
				world,
				entity.position()
						.subtract(0, entity.position().y(), 0)
						.add(0, y, 0)
						.multiply(actualScale, 1, actualScale),
				entity.getYRot()
		);
	}

	@ExpectPlatform
	public static <E extends Entity> E teleport(E entity, ServerLevel world, PortalInfo portalInfo) {
		throw new RuntimeException();
	}
}
