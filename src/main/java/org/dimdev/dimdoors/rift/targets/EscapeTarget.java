package org.dimdev.dimdoors.rift.targets;

import java.util.Random;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.UnravelUtil;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import static org.dimdev.dimdoors.api.util.EntityUtils.chat;

public class EscapeTarget extends VirtualTarget implements EntityTarget { // TODO: createRift option
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Codec<EscapeTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("canEscapeLimbo").forGetter(target -> target.canEscapeLimbo)
	).apply(instance, EscapeTarget::new));

	protected final boolean canEscapeLimbo;

	public EscapeTarget(boolean canEscapeLimbo) {
		this.canEscapeLimbo = canEscapeLimbo;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		if (!ModDimensions.isPocketDimension(entity.world) && !(ModDimensions.isLimboDimension(entity.world))) {
			chat(entity, new TranslatableText("rifts.destinations.escape.not_in_pocket_dim"));
			return false;
		}
		if (ModDimensions.isLimboDimension(entity.world) && !this.canEscapeLimbo) {
			chat(entity, new TranslatableText("rifts.destinations.escape.cannot_escape_limbo"));
			return false;
		}
		if (entity.getEntityWorld().isClient)
			return false;
		UUID uuid = entity.getUuid();
		if (uuid != null) {
			//Location destLoc = DimensionalRegistry.getRiftRegistry().getOverworldRift(uuid);
			if (entity.world.getPlayerByUuid(uuid) == null) {
				LOGGER.log(Level.ERROR, "Tried to get player for escape target from uuid, but player does not exist, uh oh");
				return false;
			}
			LOGGER.log(Level.INFO, "sending player from limbo to their spawnpoint, good luck!");
			Location destLoc;
			if (((ServerPlayerEntity) entity.world.getPlayerByUuid(uuid)).getSpawnPointPosition() != null) {
				destLoc = new Location(((ServerPlayerEntity) entity.world.getPlayerByUuid(uuid)).getSpawnPointDimension(), ((ServerPlayerEntity) entity.world.getPlayerByUuid(uuid)).getSpawnPointPosition());
			} else {
				destLoc = new Location(World.OVERWORLD, new BlockPos(0, 0, 0));
				destLoc = new Location(destLoc.getWorld(), destLoc.getWorld().getSpawnPos());
			}


			/*
			if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || this.canEscapeLimbo) {
				//Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos())).projectToWorld(false);
				TeleportUtil.teleport(entity, destLoc.getWorld(), destLoc.getBlockPos(), relativeAngle, relativeVelocity);
			} else {
				if (destLoc == null) {
					chat(entity, new TranslatableText("rifts.destinations.escape.did_not_use_rift"));
				} else {
					chat(entity, new TranslatableText("rifts.destinations.escape.rift_has_closed"));
				}
				if (ModDimensions.LIMBO_DIMENSION != null) {
					TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, new BlockPos(this.location.getX(), this.location.getY(), this.location.getZ()), relativeAngle, relativeVelocity);
				}
			}
			 */


			if (destLoc != null && this.canEscapeLimbo) {
				Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, destLoc.pos)).projectToWorld(false);
				entity = TeleportUtil.teleport(entity, location.getWorld(), location.getBlockPos(), relativeAngle, relativeVelocity);
				entity.fallDistance = 0;
				Random random = new Random();
				BlockPos.iterateOutwards(location.pos.add(0, -3, 0), 3, 2, 3).forEach((pos1 -> {
					if (random.nextFloat() < (1 / ((float) location.pos.getSquaredDistance(pos1))) * DimensionalDoorsInitializer.getConfig().getLimboConfig().limboBlocksCorruptingOverworldAmount) {
						Block block = location.getWorld().getBlockState(pos1).getBlock();
						if (UnravelUtil.unravelBlocksMap.containsKey(block))
							location.getWorld().setBlockState(pos1, UnravelUtil.unravelBlocksMap.get(block).getDefaultState());
						else if (UnravelUtil.whitelistedBlocksForLimboRemoval.contains(block)) {
							location.getWorld().setBlockState(pos1, ModBlocks.UNRAVELLED_FABRIC.getDefaultState());
						}
					}
				}));
			} else {
				if (destLoc == null) {
					chat(entity, new TranslatableText("rifts.destinations.escape.did_not_use_rift"));
				} else {
					chat(entity, new TranslatableText("rifts.destinations.escape.rift_has_closed"));
				}
				if (ModDimensions.LIMBO_DIMENSION != null) {
					entity = TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, new BlockPos(this.location.getX(), this.location.getY(), this.location.getZ()), relativeAngle, relativeVelocity);
					entity.fallDistance = 0;
				}
			}
			return true;


		} else {
			return false; // No escape info for that entity
		}
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.ESCAPE;
	}

	public static NbtCompound toNbt(EscapeTarget virtualTarget) {
		NbtCompound nbt = new NbtCompound();
		nbt.putBoolean("canEscapeLimbo", virtualTarget.canEscapeLimbo);
		return nbt;
	}

	public static EscapeTarget fromNbt(NbtCompound nbt) {
		return new EscapeTarget(nbt.getBoolean("canEscapeLimbo"));
	}
}
