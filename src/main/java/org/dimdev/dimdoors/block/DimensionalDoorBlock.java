package org.dimdev.dimdoors.block;

import io.github.boogiemonster1o1.libcbe.api.ConditionalBlockEntityProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DimensionalDoorBlock extends DoorBlock implements RiftProvider<EntranceRiftBlockEntity>, ConditionalBlockEntityProvider {
	public DimensionalDoorBlock(Settings settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient) {
			return;
		}

		BlockState doorState = world.getBlockState(state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos);

		if (doorState.getBlock() == this && doorState.get(DoorBlock.OPEN)) { // '== this' to check if not half-broken
			this.getRift(world, pos, state).teleport(entity);
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

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EntranceRiftBlockEntity();
	}

	@Override
	public boolean hasBlockEntity(BlockState blockState) {
		return blockState.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState blockState, BlockEntity entity, ItemStack stack) {
		if (entity instanceof EntranceRiftBlockEntity) {
			world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
			((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) entity).getData());
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
}
