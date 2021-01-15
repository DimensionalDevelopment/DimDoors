package org.dimdev.dimdoors.block.entity;

import java.util.Optional;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
	public EntranceRiftBlockEntity() {
		super(ModBlockEntityTypes.ENTRANCE_RIFT);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag nbt) {
		super.fromTag(state, nbt);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		return tag;
	}

	@Override
	public boolean teleport(Entity entity) {
		boolean status = super.teleport(entity);

		if (this.riftStateChanged && !this.data.isAlwaysDelete()) {
			this.markDirty();
		}

		return status;
	}

	@Override
	public boolean receiveEntity(Entity entity, float yawOffset) {
		Vec3d targetPos = Vec3d.ofCenter(this.pos).add(Vec3d.of(this.getOrientation().getOpposite().getVector()).multiply(ModConfig.INSTANCE.getGeneralConfig().teleportOffset + 0.5));
		TeleportUtil.teleport(entity, this.world, targetPos, yawOffset);
		return true;
	}

	public Direction getOrientation() {
		//noinspection ConstantConditions
		return Optional.of(this.world.getBlockState(this.pos))
				.filter(state -> state.contains(HorizontalFacingBlock.FACING))
				.map(state -> state.get(HorizontalFacingBlock.FACING))
				.orElse(Direction.NORTH);
	}

	/**
	 * Specifies if the portal should be rendered two blocks tall
	 */
	public boolean isTall() {
		return this.getCachedState().getBlock() instanceof DoorBlock;
	}

	@Override
	public boolean isDetached() {
		return false;
	}
}
