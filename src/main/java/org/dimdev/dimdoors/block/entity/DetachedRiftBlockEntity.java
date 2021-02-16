package org.dimdev.dimdoors.block.entity;

import java.util.Random;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DetachedRiftBlockEntity extends RiftBlockEntity {
	private static final Random random = new Random();

	public boolean closing = false;
	public boolean stabilized = false;
	public int spawnedEndermanId = 0;
	public float size = 0;

	private boolean unregisterDisabled = false;

	@Environment(EnvType.CLIENT)
	public double renderAngle;

	public DetachedRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.DETACHED_RIFT, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, DetachedRiftBlockEntity blockEntity) {
		if (world == null) {
			return;
		}

		if (state.getBlock() != ModBlocks.DETACHED_RIFT) {
			blockEntity.markRemoved();
			return;
		}

		if (!world.isClient() && random.nextDouble() < DimensionalDoorsInitializer.getConfig().getGeneralConfig().endermanSpawnChance) {
			EndermanEntity enderman = EntityType.ENDERMAN.spawn(
					(ServerWorld) world,
					null,
					null,
					null,
					pos,
					SpawnReason.STRUCTURE,
					false,
					false
			);

			if (random.nextDouble() < DimensionalDoorsInitializer.getConfig().getGeneralConfig().endermanAggressiveChance) {
				if (enderman != null) {
					enderman.setTarget(world.getClosestPlayer(enderman, 50));
				}
			}
		}

		if (blockEntity.closing) {
			if (blockEntity.size > 0) {
				blockEntity.size -= DimensionalDoorsInitializer.getConfig().getGeneralConfig().riftCloseSpeed;
			} else {
				world.removeBlock(pos, false);
			}
		} else if (!blockEntity.stabilized) {
			blockEntity.size += DimensionalDoorsInitializer.getConfig().getGeneralConfig().riftGrowthSpeed / (blockEntity.size + 1);
		}
	}

	public void setClosing(boolean closing) {
		this.closing = closing;
		this.markDirty();
	}

	public void setStabilized(boolean stabilized) {
		this.stabilized = stabilized;
		this.markDirty();
	}

	@Override
	public CompoundTag serialize(CompoundTag tag) {
		super.serialize(tag);
		tag.putBoolean("closing", this.closing);
		tag.putBoolean("stablized", this.stabilized);
		tag.putInt("spawnedEnderManId", this.spawnedEndermanId);
		tag.putFloat("size", this.size);
		return tag;
	}

	@Override
	public void deserialize(CompoundTag tag) {
		super.deserialize(tag);
		this.closing = tag.getBoolean("closing");
		this.stabilized = tag.getBoolean("stablized");
		this.spawnedEndermanId = tag.getInt("spawnedEnderManId");
		this.size = tag.getFloat("size");
	}

	@Override
	public boolean isDetached() {
		return true;
	}

	@Override
	public void unregister() {
		if (!this.unregisterDisabled) {
			super.unregister();
		}
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d velocity) {
		if (this.world instanceof ServerWorld)
			TeleportUtil.teleport(entity, this.world, this.pos, relativeAngle);
		return true;
	}

	public void setUnregisterDisabled(boolean unregisterDisabled) {
		this.unregisterDisabled = unregisterDisabled;
	}
}
