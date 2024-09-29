package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.dimdev.dimdoors.screen.TessellatingContainer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.Dummy.checkType;

public class TesselatingLoomBlock extends BaseEntityBlock {
	public static final MapCodec<TesselatingLoomBlock> CODEC = simpleCodec(TesselatingLoomBlock::new);

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final String DISPLAY_NAME = "";

	public TesselatingLoomBlock(Properties builder) {
		super(builder);

		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public void onRemove(BlockState oldState, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.is(newState.getBlock())) {
			BlockEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof TesselatingLoomBlockEntity) {
				final NonNullList<ItemStack> inventory = ((TesselatingLoomBlockEntity) tileEntity).inventory;
				Containers.dropContents(worldIn, pos, inventory);

				worldIn.updateNeighbourForOutputSignal(pos, this);
			}
		}
		super.onRemove(oldState, worldIn, pos, newState, isMoving);
	}

	@Nullable
	public BlockEntityTicker<TesselatingLoomBlockEntity> getTicker(Level level, BlockState blockState, BlockEntityType entityType) {
		return createFurnaceTicker(level, entityType, ModBlockEntityTypes.TESSELATING_LOOM.get());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos bpos, BlockState bstate) {
		return new TesselatingLoomBlockEntity(bpos, bstate);
	}

	protected void openContainer(Level level, BlockPos bpos, ServerPlayer player) {
		BlockEntity be = level.getBlockEntity(bpos);
		if (be instanceof TesselatingLoomBlockEntity provider) {
			MenuRegistry.openExtendedMenu(player, provider, buf -> buf.writeBlockPos(bpos));
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		} // end-if
		else {
			throw new IllegalStateException("Our named container provider is missing!");
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			this.openContainer(level, pos, (ServerPlayer) player);
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
	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
		return TessellatingContainer.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
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
	protected static BlockEntityTicker<TesselatingLoomBlockEntity> createFurnaceTicker(Level level, BlockEntityType<?> entityType, BlockEntityType<TesselatingLoomBlockEntity> entityTypeE) {
		return level.isClientSide() ? null : (BlockEntityTicker<TesselatingLoomBlockEntity>) checkType(entityType, entityTypeE, (level1, blockPos, blockState, blockEntity) -> blockEntity.serverTick());
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}

}
