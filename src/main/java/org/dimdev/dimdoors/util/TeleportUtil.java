package org.dimdev.dimdoors.util;

import java.util.concurrent.ThreadLocalRandom;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

@SuppressWarnings("deprecation")
public final class TeleportUtil {
	public static void teleport(Entity entity, World world, BlockPos pos, float yaw) {
		if (world.isClient) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		teleport(entity, world, Vec3d.ofBottomCenter(pos), yaw);
	}

	public static void teleport(Entity entity, World world, Vec3d pos, float yaw) {
		if (world.isClient) {
			throw new UnsupportedOperationException("Only supported on ServerWorld");
		}

		FabricDimensions.teleport(entity, (ServerWorld) world, new TeleportTarget(pos, entity.getVelocity(), yaw, entity.getPitch(1.0F)));
	}

	public static void teleport(ServerPlayerEntity player, Location location) {
		teleport(player, DimensionalDoorsInitializer.getWorld(location.world), location.pos, 0);
	}

	public static void teleport(ServerPlayerEntity player, RotatedLocation location) {
		teleport(player, DimensionalDoorsInitializer.getWorld(location.world), location.pos, (int) location.yaw);
	}

	public static void teleportRandom(Entity entity, World world, double y) {
		double scale = ThreadLocalRandom.current().nextGaussian() * ThreadLocalRandom.current().nextInt(90);
		teleport(
				entity,
				world,
				entity.getPos()
						.subtract(0, entity.getY(), 0)
						.add(0, y, 0)
						.multiply(scale, 1, scale),
				entity.yaw
		);
	}

	public static void teleportUntargeted(Entity entity, World world) {
		double actualScale = entity.world.getDimension().getCoordinateScale() / world.getDimension().getCoordinateScale();
		teleport(
				entity,
				world,
				entity.getPos().multiply(actualScale, 1, actualScale),
				entity.yaw
		);
	}

	public static void teleportUntargeted(Entity entity, World world, double y) {
		double actualScale = entity.world.getDimension().getCoordinateScale() / world.getDimension().getCoordinateScale();
		teleport(
				entity,
				world,
				entity.getPos()
						.subtract(0, entity.getPos().getY(), 0)
						.add(0, y, 0)
						.multiply(actualScale, 1, actualScale),
				entity.yaw
		);
	}
}
