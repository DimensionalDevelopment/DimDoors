package org.dimdev.dimdoors.block.entity;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.EulerAngle;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.util.math.TransformationMatrix3d;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
	public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.ENTRANCE_RIFT, pos, state);
	}

	@Override
	public void fromTag(CompoundTag nbt) {
		super.fromTag(nbt);
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
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		BlockState state = this.getWorld().getBlockState(this.getPos());
		Block block = state.getBlock();
		Vec3d targetPos = Vec3d.ofCenter(this.pos).add(Vec3d.of(this.getOrientation().getOpposite().getVector()).multiply(DimensionalDoorsInitializer.getConfig().getGeneralConfig().teleportOffset + 0.5));

		if (block instanceof CoordinateTransformerBlock) {
			CoordinateTransformerBlock transformer = (CoordinateTransformerBlock) block;

			if (transformer.isExitFlipped()) {
				TransformationMatrix3d flipper = TransformationMatrix3d.builder().rotateY(Math.PI).build();

				relativePos = flipper.transform(relativePos);
				relativeAngle = flipper.transform(relativeAngle);
				relativeVelocity = flipper.transform(relativeVelocity);
			}

			relativePos = relativePos.add(new Vec3d(0, 0, 1).multiply(0.6)); // TODO: skip this for Immersive Portals

			TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, this.getPos());
			TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, this.getPos());
			targetPos = transformer.transformOut(transformationBuilder, relativePos);
			relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
			relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
		}

		entity = TeleportUtil.teleport(entity, this.world, targetPos, relativeAngle, relativeVelocity);
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
		}

		return true;
	}

	public Direction getOrientation() {
		//noinspection ConstantConditions
		return Optional.of(this.world.getBlockState(this.pos))
				.filter(state -> state.contains(HorizontalFacingBlock.FACING))
				.map(state -> state.get(HorizontalFacingBlock.FACING))
				.orElse(Direction.NORTH);
	}

	public boolean hasOrientation() {
		return this.world != null && this.world.getBlockState(this.pos).contains(HorizontalFacingBlock.FACING);
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
