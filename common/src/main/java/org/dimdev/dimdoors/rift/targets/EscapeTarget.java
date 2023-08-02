package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
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

import java.util.UUID;

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
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		if (!ModDimensions.isPocketDimension(entity.level) && !(ModDimensions.isLimboDimension(entity.level))) {
			chat(entity, Component.translatable("rifts.destinations.escape.not_in_pocket_dim"));
			return false;
		}
		if (ModDimensions.isLimboDimension(entity.level) && !this.canEscapeLimbo) {
			chat(entity, Component.translatable("rifts.destinations.escape.cannot_escape_limbo"));
			return false;
		}
		if (entity.getLevel().isClientSide)
			return false;
		UUID uuid = entity.getUUID();
		if (uuid != null) {
			//Location destLoc = DimensionalRegistry.getRiftRegistry().getOverworldRift(uuid);
			if (entity.level.getPlayerByUUID(uuid) == null) {
				LOGGER.log(Level.ERROR, "Tried to get player for escape target from uuid, but player does not exist, uh oh");
				return false;
			}
			LOGGER.log(Level.INFO, "sending player from limbo to their spawnpoint, good luck!");
			Location destLoc;
			if (((ServerPlayer) entity.level.getPlayerByUUID(uuid)).getRespawnPosition() != null) {
				destLoc = new Location(((ServerPlayer) entity.level.getPlayerByUUID(uuid)).getRespawnDimension(), ((ServerPlayer) entity.level.getPlayerByUUID(uuid)).getRespawnPosition());
			} else {
				destLoc = new Location(DimensionalDoors.getServer().overworld(), DimensionalDoors.getServer().overworld().getSharedSpawnPos());

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


			if (destLoc != null && this.canEscapeLimbo) {
				Location location = destLoc; //VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, destLoc.pos)).projectToWorld(false); //TODO Fix world projection.
				entity = TeleportUtil.teleport(entity, location.getWorld(), location.getBlockPos(), relativeAngle, relativeVelocity);
				entity.fallDistance = 0;
				RandomSource random = RandomSource.create();
				BlockPos.withinManhattan(location.pos.offset(0, -3, 0), 3, 2, 3).forEach((pos1 -> {
					if (random.nextFloat() < (1 / ((float) location.pos.distSqr(pos1))) * DimensionalDoors.getConfig().getLimboConfig().limboBlocksCorruptingOverworldAmount) {
						Block block = location.getWorld().getBlockState(pos1).getBlock();
						if (UnravelUtil.unravelBlocksMap.containsKey(block))
							location.getWorld().setBlockAndUpdate(pos1, UnravelUtil.unravelBlocksMap.get(block).defaultBlockState());
						else if (UnravelUtil.whitelistedBlocksForLimboRemoval.contains(block)) {
							location.getWorld().setBlockAndUpdate(pos1, ModBlocks.UNRAVELLED_FABRIC.get().defaultBlockState());
						}
					}
				}));
			} else {
				if (destLoc == null) {
					chat(entity, Component.translatable("rifts.destinations.escape.did_not_use_rift"));
				} else {
					chat(entity, Component.translatable("rifts.destinations.escape.rift_has_closed"));
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
		return VirtualTargetType.ESCAPE.get();
	}

	@Override
	public VirtualTarget copy() {
		return new EscapeTarget(canEscapeLimbo);
	}

	public static CompoundTag toNbt(EscapeTarget virtualTarget) {
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("canEscapeLimbo", virtualTarget.canEscapeLimbo);
		return nbt;
	}

	public static EscapeTarget fromNbt(CompoundTag nbt) {
		return new EscapeTarget(nbt.getBoolean("canEscapeLimbo"));
	}
}
