package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sk89q.worldedit.math.convolution.HeightMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.UnravelUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Random;
import java.util.UUID;

import static org.dimdev.dimdoors.api.util.EntityUtils.chat;

public class EscapeTarget extends VirtualTarget implements EntityTarget { // TODO: createRift option
	private static final Logger LOGGER = LogManager.getLogger();
	private static ResourceKey<net.minecraft.world.level.Level> targetWorldResourceKey;

	public static final Codec<EscapeTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("canEscapeLimbo").forGetter(target -> target.canEscapeLimbo)
	).apply(instance, EscapeTarget::new));

	protected final boolean canEscapeLimbo;

	public EscapeTarget(boolean canEscapeLimbo) {
		this.canEscapeLimbo = canEscapeLimbo;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location2) {
		if (!ModDimensions.isPocketDimension(entity.level()) && !(ModDimensions.isLimboDimension(entity.level()))) {
//			chat(entity, Component.translatable("rifts.destinations.escape.not_in_pocket_dim")); TODO: Decide a proper alternate to spam
			return false;
		}
		if (ModDimensions.isLimboDimension(entity.level()) && !this.canEscapeLimbo) {
//			chat(entity, Component.translatable("rifts.destinations.escape.cannot_escape_limbo")); TODO: Decide a proper alternate to spam
			return false;
		}


		if (entity.level().isClientSide)
			return false;
		if (entity instanceof ServerPlayer player) { //TODO: Determine what other entity types should do when escaping.
//			Location destLoc = DimensionalRegistry.getRiftRegistry().get.getOverworldRift(uuid);

			ServerLevel destLevel = null;
			BlockPos destPos = null;

			if (DimensionalDoors.getConfig().getLimboConfig().tryPlayerBedSpawn) {
				var level = DimensionalDoors.getWorld(player.getRespawnDimension());

				if(level != null) {
					destLevel = level;
					destPos = player.getRespawnPosition();
				}
			}


			if(destLevel == null) {
				var targetWorld = DimensionalDoors.getConfig().getLimboConfig().escapeTargetWorld;
				destLevel = DimensionalDoors.getServer().overworld();

				if(targetWorld != null) {
					var level = DimensionalDoors.getWorld(targetWorld);

					if(level != null) {
						destLevel = level;
					}
				}

				if(DimensionalDoors.getConfig().getLimboConfig().defaultToWorldSpawn) {
					destPos = destLevel.getSharedSpawnPos();
				} else {
					destPos = player.blockPosition();
				}
			}

			/*
			if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || this.canEscapeLimbo) {
				//Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos())).projectToWorld(false);
				TeleportUtil.teleport(entity, destLoc.getWorld(), destLoc.getBlockPos(), relativeAngle, relativeVelocity);
			} else {
				if (destLoc == null) {
					chat(entity, MutableText.of(new TranslatableTextContent("rifts.destinations.escape.did_not_use_rift"));
				} else {
					chat(entity, MutableText.of(new TranslatableTextContent("rifts.destinations.escape.rift_has_closed"));
				}
				if (ModDimensions.LIMBO_DIMENSION != null) {
					TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, new BlockPos(this.location.getX(), this.location.getY(), this.location.getZ()), relativeAngle, relativeVelocity);
				}
			}
			 */

			var destLoc = randomizeLimboReturn(destLevel, destPos, DimensionalDoors.getConfig().getLimboConfig().limboReturnDistanceMin, DimensionalDoors.getConfig().getLimboConfig().limboReturnDistanceMax); //todo add minimum radius

			if (destLoc != null && this.canEscapeLimbo) {
				Location location = destLoc; //VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, destLoc.pos)).projectToWorld(false); //TODO Fix world projection.

				var level = location.getWorld();
				entity = TeleportUtil.teleport(entity, level, location.getBlockPos(), relativeAngle, relativeVelocity);
				entity.fallDistance = -500;
				level.setBlockAndUpdate(location.getBlockPos(), Blocks.AIR.defaultBlockState());
				level.setBlockAndUpdate(location.getBlockPos().offset(0, 1, 0), Blocks.AIR.defaultBlockState());

				if(DimensionalDoors.getConfig().getLimboConfig().decaySurroundings) {
					RandomSource random = RandomSource.create();
					BlockPos.withinManhattan(location.pos.offset(0, -3, 0), 3, 2, 3).forEach((pos1 -> {
						if (random.nextFloat() < (1 / ((float) location.pos.distSqr(pos1))) * DimensionalDoors.getConfig().getLimboConfig().limboBlocksCorruptingExitWorldAmount) {
							Block block = level.getBlockState(pos1).getBlock();
							if (UnravelUtil.unravelBlocksMap.containsKey(block))
								level.setBlockAndUpdate(pos1, UnravelUtil.unravelBlocksMap.get(block).defaultBlockState());
							else if (UnravelUtil.whitelistedBlocksForLimboRemoval.contains(block)) {
								level.setBlockAndUpdate(pos1, ModBlocks.UNRAVELLED_FABRIC.get().defaultBlockState());
							}
						}
					}));
				}
			} else {
				if (destLoc == null) {
					chat(entity, Component.translatable("rifts.destinations.escape.did_not_use_rift"));
				} else {
					chat(entity, Component.translatable("rifts.destinations.escape.rift_has_closed"));
				}
				if (ModDimensions.LIMBO_DIMENSION != null) {
					entity = TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, new BlockPos(this.location.getX(), this.location.getY(), this.location.getZ()), relativeAngle, relativeVelocity);
					entity.fallDistance = -500;
				}
			}
			return true;

		} else {
			return false; // No escape info for that entity
		}
	}

	@Override
	public VirtualTargetType getType() {
		return VirtualTargetType.ESCAPE.get();
	}

	@Override
	public VirtualTarget copy() {
		return new EscapeTarget(canEscapeLimbo);
	}

	public static Location randomizeLimboReturn(ServerLevel level, BlockPos pos, int minRange, int maxRange) {
		if(level == null || pos == null) return null;

		if(minRange == 0 && maxRange == 0) return new Location(level, pos);

		return new Location(
				level,
				Location.getHeightmapPosSafe(level, randomizeCoord(pos.getX(), minRange, maxRange), randomizeCoord(pos.getZ(), minRange, maxRange))
		);
	}

	public static int randomizeCoord(int coord, int minRange, int maxRange) {
		Random random = new Random();

		if (minRange > maxRange) {
			throw new IllegalArgumentException("minRange cannot be greater than maxRange");
		}

		int offset = minRange + random.nextInt((maxRange - minRange) + 1);
		boolean isPositive = random.nextBoolean();

		return isPositive ? coord + offset : coord - offset;
	}
}
