package org.dimdev.dimdoors.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.RiftCurves;

public class DetachedRiftBlockEntity extends RiftBlockEntity {
	private static final RandomSource random = RandomSource.create();

	public boolean closing = false;
	public boolean stabilized = false;
	public int spawnedEndermanId = 0;
	public float size = 0;
	public int curveID = 0;

	private boolean unregisterDisabled = false;

	@Environment(EnvType.CLIENT)
	public double renderAngle;

	public DetachedRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.DETACHED_RIFT.get(), pos, state);
		this.curveID = (int) (Math.random()* RiftCurves.CURVES.size());
	}

	public static void tick(Level world, BlockPos pos, BlockState state, DetachedRiftBlockEntity blockEntity) {
		if (world == null) {
			return;
		}

		if (state.getBlock() != ModBlocks.DETACHED_RIFT.get()) {
			blockEntity.setRemoved();
			return;
		}

		if (!world.isClientSide() && random.nextFloat() < DimensionalDoors.getConfig().getGeneralConfig().endermanSpawnChance) {
			EnderMan enderman = EntityType.ENDERMAN.spawn(
					(ServerLevel) world,
					pos,
					MobSpawnType.STRUCTURE
			);

			if (random.nextDouble() < DimensionalDoors.getConfig().getGeneralConfig().endermanAggressiveChance) {
				if (enderman != null) {
					enderman.setTarget(world.getNearestPlayer(enderman, 50));
				}
			}
		}

		if (blockEntity.closing) {
			if (blockEntity.size > 0) {
				blockEntity.size -= DimensionalDoors.getConfig().getGeneralConfig().riftCloseSpeed;
			} else {
				world.removeBlock(pos, false);
			}
		} else if (!blockEntity.stabilized) {
			blockEntity.size += DimensionalDoors.getConfig().getGeneralConfig().riftGrowthSpeed / (blockEntity.size + 1);
		}
	}

	public void setClosing(boolean closing) {
		this.closing = closing;
		this.setChanged();
	}

	public void setStabilized(boolean stabilized) {
		this.stabilized = stabilized;
		this.setChanged();
	}

	public int getCurveID() {
		return this.curveID;
	}

	@Override
	public CompoundTag serialize(CompoundTag nbt) {
		super.serialize(nbt);
		nbt.putBoolean("closing", this.closing);
		nbt.putBoolean("stablized", this.stabilized);
		nbt.putInt("spawnedEnderManId", this.spawnedEndermanId);
		nbt.putFloat("size", this.size);
		nbt.putInt("curveID", this.curveID);
		return nbt;
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		this.closing = nbt.getBoolean("closing");
		this.stabilized = nbt.getBoolean("stablized");
		this.spawnedEndermanId = nbt.getInt("spawnedEnderManId");
		this.size = nbt.getFloat("size");
		this.curveID = nbt.getInt("curveID");
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
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 velocity) {
		if (this.level instanceof ServerLevel)
			TeleportUtil.teleport(entity, this.level, this.worldPosition, relativeAngle, velocity);
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
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putFloat("size", this.size);
		tag.putInt("curveID", this.curveID);
		return tag;

	}

}
