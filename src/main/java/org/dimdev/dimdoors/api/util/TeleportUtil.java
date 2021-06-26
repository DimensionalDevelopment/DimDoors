package org.dimdev.dimdoors.api.util;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.*;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.world.ModDimensions;

@SuppressWarnings("deprecation")
public final class TeleportUtil {
	public static  <E extends Entity> E teleport(E entity, World world, BlockPos pos, float yaw) {
		return teleport(entity, world, Vec3d.ofBottomCenter(pos), yaw);
	}

	public static  <E extends Entity> E teleport(E entity, World world, Vec3d pos, float yaw) {
		return teleport(entity, world, pos, new EulerAngle(entity.getPitch(), yaw, 0), entity.getVelocity());
	}

	public static  <E extends Entity> E teleport(E entity, World world, Vec3d pos, EulerAngle angle, Vec3d velocity) {
		if (world.isClient) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		// Some insurance
		float yaw = MathHelper.wrapDegrees(angle.getYaw());
		float pitch = MathHelper.clamp(MathHelper.wrapDegrees(angle.getPitch()), -90.0F, 90.0F);

		if (entity instanceof ServerPlayerEntity) {
			// This is what the vanilla tp command does. Let's hope this works.
			ChunkPos chunkPos = new ChunkPos(new BlockPos(pos));
			((ServerWorld) world).getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
			entity.stopRiding();


			if (entity.world.getRegistryKey().equals(world.getRegistryKey())) {
				((ServerPlayerEntity) entity).networkHandler.requestTeleport(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
			} else {
				entity = FabricDimensions.teleport(entity, (ServerWorld) world, new TeleportTarget(pos, velocity, yaw, pitch));
			}

			((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity.getId(), velocity));
			((ExtendedServerPlayNetworkHandler) ((ServerPlayerEntity) entity).networkHandler).getDimDoorsPacketHandler().syncPocketAddonsIfNeeded(world, new BlockPos(pos));

			if (world.getRegistryKey() == ModDimensions.DUNGEON) {
				((PlayerEntity) entity).incrementStat(ModStats.TIMES_BEEN_TO_DUNGEON);
			}
		} else {
			if (entity.world.getRegistryKey().equals(world.getRegistryKey())) {
				entity.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
			} else {
				entity = FabricDimensions.teleport(entity, (ServerWorld) world, new TeleportTarget(pos, velocity, yaw, pitch));
			}
		}
		entity.setVelocity(velocity);

		return entity;
	}

	public static  <E extends Entity> E teleport(E entity, World world, BlockPos pos, EulerAngle angle, Vec3d velocity) {
		if (world.isClient) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		return teleport(entity, world, Vec3d.ofBottomCenter(pos), angle, velocity);
	}

	public static ServerPlayerEntity teleport(ServerPlayerEntity player, Location location) {
		return teleport(player, DimensionalDoorsInitializer.getWorld(location.world), location.pos, 0);
	}

	public static ServerPlayerEntity teleport(ServerPlayerEntity player, RotatedLocation location) {
		return teleport(player, DimensionalDoorsInitializer.getWorld(location.world), location.pos, (int) location.yaw);
	}
	public static  <E extends Entity> E teleportRandom(E entity, World world, double y) {
		double scale = ThreadLocalRandom.current().nextGaussian() * ThreadLocalRandom.current().nextInt(90);
		return teleport(
				entity,
				world,
				entity.getPos()
						.subtract(0, entity.getY(), 0)
						.add(0, y, 0)
						.multiply(scale, 1, scale),
				entity.getYaw()
		);
	}

	public static  <E extends Entity> E teleportUntargeted(E entity, World world) {
		double actualScale = entity.world.getDimension().getCoordinateScale() / world.getDimension().getCoordinateScale();
		return teleport(
				entity,
				world,
				entity.getPos().multiply(actualScale, 1, actualScale),
				entity.getYaw()
		);
	}

	public static  <E extends Entity> E teleportUntargeted(E entity, World world, double y) {
		double actualScale = entity.world.getDimension().getCoordinateScale() / world.getDimension().getCoordinateScale();
		return teleport(
				entity,
				world,
				entity.getPos()
						.subtract(0, entity.getPos().getY(), 0)
						.add(0, y, 0)
						.multiply(actualScale, 1, actualScale),
				entity.getYaw()
		);
	}
}
