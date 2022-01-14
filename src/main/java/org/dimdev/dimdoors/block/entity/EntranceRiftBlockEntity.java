package org.dimdev.dimdoors.block.entity;

import java.util.Optional;

import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.api.client.DefaultTransformation;
import org.dimdev.dimdoors.api.client.Transformer;
import org.dimdev.dimdoors.item.RiftKeyItem;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
	private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean locked;

	public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.ENTRANCE_RIFT, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		locked = nbt.getBoolean("locked");
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putBoolean("locked", locked);
		super.writeNbt(nbt);
	}

	@Override
	public boolean teleport(Entity entity) {
		//Sets the location where the player should be teleported back to if they are in limbo and try to escape, to be the entrance of the rift that took them into dungeons.

		if (this.isLocked()) {
			if (entity instanceof LivingEntity) {
				ItemStack stack = ((LivingEntity) entity).getStackInHand(((LivingEntity) entity).getActiveHand());
				Rift rift = this.asRift();

				if (RiftKeyItem.has(stack, rift.getId())) {
					return innerTeleport(entity);
				}

				EntityUtils.chat(entity, new TranslatableText("rifts.isLocked"));
			}
			return false;
		}

		return innerTeleport(entity);
	}

	private boolean innerTeleport(Entity entity) {
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
		Vec3d targetPos = Vec3d.ofCenter(this.pos).add(Vec3d.of(this.getOrientation().getOpposite().getVector()).multiply(DimensionalDoorsInitializer.getConfig().getGeneralConfig().teleportOffset + 0.01/* slight offset to prevent issues due to mathematical inaccuracies*/));
		/*
		Unused code that needs to be edited if there are other ways to get to limbo
		But if it is only dimteleport and going through rifts then this code isn't nessecary
		if(DimensionalRegistry.getRiftRegistry().getOverworldRift(entity.getUuid()) == null) {
			DimensionalRegistry.getRiftRegistry().setOverworldRift(entity.getUuid(), new Location(World.OVERWORLD, ((ServerPlayerEntity)entity).getSpawnPointPosition()));
		}
		 */
		if (block instanceof CoordinateTransformerBlock) {
			CoordinateTransformerBlock transformer = (CoordinateTransformerBlock) block;

			if (transformer.isExitFlipped()) {
				TransformationMatrix3d flipper = TransformationMatrix3d.builder().rotateY(Math.PI).build();

				relativePos = flipper.transform(relativePos);
				relativeAngle = flipper.transform(relativeAngle);
				relativeVelocity = flipper.transform(relativeVelocity);
			}

			TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, this.getPos());
			TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, this.getPos());
			targetPos = transformer.transformOut(transformationBuilder, relativePos);
			relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
			relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
		}

		// TODO: open door

		TeleportUtil.teleport(entity, this.world, targetPos, relativeAngle, relativeVelocity);

		return true;
	}

	public Direction getOrientation() {
		//noinspection ConstantConditions
		return Optional.of(this.world.getBlockState(this.pos))
				.filter(state -> state.contains(HorizontalFacingBlock.FACING))
				.map(state -> state.get(HorizontalFacingBlock.FACING))
				.orElse(Direction.NORTH);
	}

	@Environment(EnvType.CLIENT)
	public Transformer getTransformer() {
		return DefaultTransformation.fromDirection(this.getOrientation());
	}

	public boolean hasOrientation() {
		return this.world != null && this.world.getBlockState(this.pos).contains(HorizontalFacingBlock.FACING);
	}

	/**
	 * Specifies if the portal should be rendered two blocks tall
	 */
	@Environment(EnvType.CLIENT)
	public boolean isTall() {
		return ((RiftProvider<?>) this.getCachedState().getBlock()).isTall(this.getCachedState());
	}

	@Override
	public boolean isDetached() {
		return false;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setPortalDestination(ServerWorld world) {
		if (ModDimensions.isLimboDimension(world)) {
			this.setDestination(ESCAPE_TARGET);
		} else {
			this.setDestination(DefaultDungeonDestinations.getGateway());
			this.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
		}
	}
}
