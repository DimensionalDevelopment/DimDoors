package org.dimdev.dimdoors.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;

public class TesselatingLoomBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final String DISPLAY_NAME = "";

	public TesselatingLoomBlock(Settings builder) {
		super(builder);

		this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void onStateReplaced(BlockState oldState, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.isOf(newState.getBlock())) {
			BlockEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof TesselatingLoomBlockEntity) {
				final DefaultedList<ItemStack> inventory = ((TesselatingLoomBlockEntity) tileEntity).inventory;
				for (ItemStack itemStack : inventory)
					ItemScatterer.spawn(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack);
				worldIn.updateComparators(pos, this);
			}
		}
		super.onStateReplaced(oldState, worldIn, pos, newState, isMoving);
	}
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState bstate, BlockEntityType<T> entityType) {
		return createFurnaceTicker(level, entityType, ModBlockEntityTypes.TESSELATING_LOOM);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos bpos, BlockState bstate) {
		return new TesselatingLoomBlockEntity(bpos, bstate);
	}

	protected void openContainer(World level, BlockPos bpos, PlayerEntity player) {
		BlockEntity be = level.getBlockEntity(bpos);
		if (be instanceof TesselatingLoomBlockEntity) {
			player.openHandledScreen((NamedScreenHandlerFactory) be);
			player.incrementStat(Stats.INTERACT_WITH_FURNACE);
		} // end-if
		else {
			throw new IllegalStateException("Our named container provider is missing!");
		}
	}

	@Override
	public ActionResult onUse(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockHitResult hit) {
		if (!worldIn.isClient()) {
			this.openContainer(worldIn, pos, player);
		}

		return ActionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState bstate, World level, BlockPos bpos) {
		return TesselatingScreenHandler.calculateComparatorOutput(level.getBlockEntity(bpos));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(World level, BlockEntityType<T> entityType, BlockEntityType<? extends TesselatingLoomBlockEntity> entityTypeE) {
		return level.isClient() ? null : checkType(entityType, entityTypeE, TesselatingLoomBlockEntity::serverTick);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
