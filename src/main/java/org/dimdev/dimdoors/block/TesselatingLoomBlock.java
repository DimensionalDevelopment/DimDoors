package org.dimdev.dimdoors.block;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.dimdev.dimdoors.screen.TesselatingScreenHandler;

public class TesselatingLoomBlock extends BaseEntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final String DISPLAY_NAME = "";

	public TesselatingLoomBlock(Properties builder) {
		super(builder);

		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public void onRemove(BlockState oldState, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.is(newState.getBlock())) {
			BlockEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof TesselatingLoomBlockEntity) {
				final NonNullList<ItemStack> inventory = ((TesselatingLoomBlockEntity) tileEntity).inventory;
				for (ItemStack itemStack : inventory)
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack);
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}
		}
		super.onRemove(oldState, worldIn, pos, newState, isMoving);
	}
	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState bstate, BlockEntityType<T> entityType) {
		return createFurnaceTicker(level, entityType, ModBlockEntityTypes.TESSELATING_LOOM);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos bpos, BlockState bstate) {
		return new TesselatingLoomBlockEntity(bpos, bstate);
	}

	protected void openContainer(Level level, BlockPos bpos, Player player) {
		BlockEntity be = level.getBlockEntity(bpos);
		if (be instanceof TesselatingLoomBlockEntity) {
			player.openMenu((MenuProvider) be);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		} // end-if
		else {
			throw new IllegalStateException("Our named container provider is missing!");
		}
	}

	@Override
	public InteractionResult use(final BlockState state, final Level worldIn, final BlockPos pos, final Player player, final InteractionHand handIn, final BlockHitResult hit) {
		if (!worldIn.isClientSide()) {
			this.openContainer(worldIn, pos, player);
		}

		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState bstate, Level level, BlockPos bpos) {
		return TesselatingScreenHandler.getRedstoneSignalFromBlockEntity(level.getBlockEntity(bpos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(Level level, BlockEntityType<T> entityType, BlockEntityType<? extends TesselatingLoomBlockEntity> entityTypeE) {
		return level.isClientSide() ? null : createTickerHelper(entityType, entityTypeE, TesselatingLoomBlockEntity::serverTick);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
