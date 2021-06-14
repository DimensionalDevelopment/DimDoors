package org.dimdev.dimdoors.block.entity;

import java.util.Random;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.api.util.TeleportUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.NbtCompound;
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

		if (!world.isClient() && random.nextFloat() < DimensionalDoorsInitializer.getConfig().getGeneralConfig().endermanSpawnChance) {
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
	public NbtCompound serialize(NbtCompound nbt) {
		super.serialize(nbt);
		nbt.putBoolean("closing", this.closing);
		nbt.putBoolean("stablized", this.stabilized);
		nbt.putInt("spawnedEnderManId", this.spawnedEndermanId);
		nbt.putFloat("size", this.size);
		return nbt;
	}

	@Override
	public void deserialize(NbtCompound nbt) {
		super.deserialize(nbt);
		this.closing = nbt.getBoolean("closing");
		this.stabilized = nbt.getBoolean("stablized");
		this.spawnedEndermanId = nbt.getInt("spawnedEnderManId");
		this.size = nbt.getFloat("size");
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
			TeleportUtil.teleport(entity, this.world, this.pos, relativeAngle, velocity);
		return true;
	}

	public void setUnregisterDisabled(boolean unregisterDisabled) {
		this.unregisterDisabled = unregisterDisabled;
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public void setLocked(boolean locked) {
		// NO-OP
	}
}
