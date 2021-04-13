package org.dimdev.dimdoors.block.door;

import net.minecraft.util.math.Vec3d;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DimensionalDoorBlock extends DoorBlock implements RiftProvider<EntranceRiftBlockEntity>, CoordinateTransformerBlock {
	public DimensionalDoorBlock(Settings settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation") // TODO: change from onEntityCollision to some method for checking if player crossed portal plane
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient) {
			return;
		}

		// TODO: replace with dimdoor cooldown?
		if (entity.hasNetherPortalCooldown()) {
			entity.resetNetherPortalCooldown();
			return;
		}
		entity.resetNetherPortalCooldown();

		BlockPos top = state.get(HALF) == DoubleBlockHalf.UPPER ? pos : pos.up();
		BlockPos bottom = top.down();
		BlockState doorState = world.getBlockState(bottom);

		if (doorState.getBlock() == this && doorState.get(DoorBlock.OPEN)) { // '== this' to check if not half-broken
			this.getRift(world, pos, state).teleport(entity);
			if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().closeDoorBehind) {
				world.setBlockState(top, world.getBlockState(top).with(DoorBlock.OPEN, false));
				world.setBlockState(bottom, world.getBlockState(bottom).with(DoorBlock.OPEN, false));
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		state = state.cycle(OPEN);
		world.setBlockState(pos, state, 10);
		world.syncWorldEvent(player, state.get(OPEN) ? this.material == Material.METAL ? 1005 : 1006 : this.material == Material.METAL ? 1011 : 1012, pos, 0);
		return ActionResult.SUCCESS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return super.canReplace(state, context) || state.getBlock() == ModBlocks.DETACHED_RIFT;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			return null;
		}
		return new EntranceRiftBlockEntity(pos, state);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState blockState, BlockEntity entity, ItemStack stack) {
		if (entity instanceof EntranceRiftBlockEntity) {
			world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
			((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) entity).getData());
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		if (state.get(HALF) == DoubleBlockHalf.LOWER) { // TODO: place a detached rift
		}
	}

	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		BlockEntity bottomEntity;
		BlockEntity topEntity;

		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			bottomEntity = world.getBlockEntity(pos);
			topEntity = world.getBlockEntity(pos.up());
		} else {
			bottomEntity = world.getBlockEntity(pos.down());
			topEntity = world.getBlockEntity(pos);
		}

		// TODO: Also notify player in case of error, don't crash
		if (bottomEntity instanceof EntranceRiftBlockEntity && topEntity instanceof EntranceRiftBlockEntity) {
			LOGGER.warn("Dimensional door at " + pos + " in world " + world + " contained two rifts, please report this. Defaulting to bottom.");
			return (EntranceRiftBlockEntity) bottomEntity;
		} else if (bottomEntity instanceof EntranceRiftBlockEntity) {
			return (EntranceRiftBlockEntity) bottomEntity;
		} else if (topEntity instanceof EntranceRiftBlockEntity) {
			return (EntranceRiftBlockEntity) topEntity;
		} else {
			throw new IllegalStateException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
		}
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseTranslate(Vec3d.ofCenter(pos).add(Vec3d.of(state.get(DoorBlock.FACING).getVector()).multiply(-0.5)))
				.inverseRotate(MathUtil.directionEulerAngle(state.get(DoorBlock.FACING).getOpposite()));
	}

	@Override
	public TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos) {
		return TransformationMatrix3d.builder()
				.inverseRotate(MathUtil.directionEulerAngle(state.get(DoorBlock.FACING).getOpposite()));
	}

	@Override
	public boolean isExitFlipped() {
		return true;
	}

	@Override
	public boolean isTall(BlockState cachedState) {
		return true;
	}
}
