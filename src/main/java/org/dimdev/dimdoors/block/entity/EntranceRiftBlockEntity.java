package org.dimdev.dimdoors.block.entity;

import java.util.Optional;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.api.client.DefaultTransformation;
import org.dimdev.dimdoors.api.client.Transformer;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.item.RiftKeyItem;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.world.ModDimensions;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
	private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean locked;

	public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.ENTRANCE_RIFT.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		locked = nbt.getBoolean("locked");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		nbt.putBoolean("locked", locked);
		super.saveAdditional(nbt);
	}

	@Override
	public boolean teleport(Entity entity) {
		//Sets the location where the player should be teleported back to if they are in limbo and try to escape, to be the entrance of the rift that took them into dungeons.

		if (this.isLocked()) {
			if (entity instanceof LivingEntity) {
				ItemStack stack = ((LivingEntity) entity).getItemInHand(((LivingEntity) entity).getUsedItemHand());
				Rift rift = this.asRift();

				if (RiftKeyItem.has(stack, rift.getId())) {
					return innerTeleport(entity);
				}

				EntityUtils.chat(entity, MutableComponent.create(new TranslatableContents("rifts.isLocked")));
			}
			return false;
		}

		return innerTeleport(entity);
	}

	private boolean innerTeleport(Entity entity) {
		boolean status = super.teleport(entity);

		if (this.riftStateChanged && !this.data.isAlwaysDelete()) {
			this.setChanged();
		}

		return status;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		Block block = state.getBlock();
		Vec3 targetPos = Vec3.atCenterOf(this.worldPosition).add(Vec3.atLowerCornerOf(this.getOrientation().getOpposite().getNormal()).scale(Constants.CONFIG_MANAGER.get().getGeneralConfig().teleportOffset + 0.01/* slight offset to prevent issues due to mathematical inaccuracies*/));
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

			TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, this.getBlockPos());
			TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, this.getBlockPos());
			targetPos = transformer.transformOut(transformationBuilder, relativePos);
			relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
			relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
		}

		// TODO: open door

		TeleportUtil.teleport(entity, this.level, targetPos, relativeAngle, relativeVelocity);

		return true;
	}

	public Direction getOrientation() {
		//noinspection ConstantConditions
		return Optional.of(this.level.getBlockState(this.worldPosition))
				.filter(state -> state.hasProperty(HorizontalDirectionalBlock.FACING))
				.map(state -> state.getValue(HorizontalDirectionalBlock.FACING))
				.orElse(Direction.NORTH);
	}

	@OnlyIn(Dist.CLIENT)
	public Transformer getTransformer() {
		return DefaultTransformation.fromDirection(this.getOrientation());
	}

	public boolean hasOrientation() {
		return this.level != null && this.level.getBlockState(this.worldPosition).hasProperty(HorizontalDirectionalBlock.FACING);
	}

	/**
	 * Specifies if the portal should be rendered two blocks tall
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean isTall() {
		return ((RiftProvider<?>) this.getBlockState().getBlock()).isTall(this.getBlockState());
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

	public void setPortalDestination(ServerLevel world) {
		if (ModDimensions.isLimboDimension(world)) {
			this.setDestination(ESCAPE_TARGET);
		} else {
			this.setDestination(DefaultDungeonDestinations.getGateway());
			this.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
		}
	}
}
