package org.dimdev.dimdoors.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.client.DefaultTransformation;
import org.dimdev.dimdoors.api.client.Transformer;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.item.RiftKeyItem;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.Optional;

import static net.minecraft.world.level.block.DoorBlock.*;
import static org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock.WATERLOGGED;

public class EntranceRiftBlockEntity extends RiftBlockEntity<DimensionalDoorBlock> {
	private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean locked;

	public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.ENTRANCE_RIFT.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		locked = tag.getBoolean("locked");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		nbt.putBoolean("locked", locked);
		super.saveAdditional(nbt, registries);
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

				EntityUtils.chat(entity, Component.translatable("rifts.isLocked"));
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
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		Block block = state.getBlock();
		Vec3 targetPos = Vec3.atCenterOf(this.getBlockPos()).add(Vec3.atLowerCornerOf(this.getOrientation().getOpposite().getNormal()).scale(DimensionalDoors.getConfig().getGeneralConfig().teleportOffset + 0.01/* slight offset to prevent issues due to mathematical inaccuracies*/));
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

			//TODO:offset entity one block infront of door

			relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
			relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
		}

		// TODO: open door
		Direction direction = getOrientation().getOpposite();



		targetPos= targetPos.add((double) direction.getNormal().getX() /2, (double) direction.getNormal().getY() /2, (double) direction.getNormal().getZ() /2);

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

	@Environment(EnvType.CLIENT)
	public Transformer getTransformer() {
		return DefaultTransformation.fromDirection(this.getOrientation());
	}

	public boolean hasOrientation() {
		return this.level != null && this.level.getBlockState(this.worldPosition).hasProperty(HorizontalDirectionalBlock.FACING);
	}

	/**
	 * Specifies if the portal should be rendered two blocks tall
	 */
	@Environment(EnvType.CLIENT)
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

	public void generateDetached(Level world) {
		var blockState = getBlockState();
		var pos = getBlockPos();
		world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
		((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(getData());
	}

	@Override
	protected void onClose(Level level, BlockPos pos) {
		var state = level.getBlockState(pos);
		var block = state.getBlock();

		if(block instanceof DimensionalDoorBlock dimensionalDoorBlock) {
			var base = dimensionalDoorBlock.baseBlock();

			if (base instanceof DoorBlock doorBlock) {
				var newState = doorBlock.defaultBlockState()
						.setValue(FACING, state.getValue(FACING))
						.setValue(OPEN, state.getValue(OPEN))
						.setValue(HINGE, state.getValue(HINGE))
						.setValue(POWERED, state.getValue(POWERED))
						.setValue(HALF, DoubleBlockHalf.UPPER);

				level.removeBlock(pos, false);
				level.setBlockAndUpdate(pos.above(), newState);
				level.setBlockAndUpdate(pos.above(), newState.setValue(HALF, DoubleBlockHalf.LOWER));
			}
		} else if(block instanceof DimensionalPortalBlock) {
			level.removeBlock(pos, false);
		}
	}

	@Override
	protected Class<DimensionalDoorBlock> blockClass() {
		return DimensionalDoorBlock.class;
	}

	@Override
	public boolean stablized() {
		return true;
	}
}
