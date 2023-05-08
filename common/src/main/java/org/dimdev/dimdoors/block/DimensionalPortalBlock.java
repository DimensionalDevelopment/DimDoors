package org.dimdev.dimdoors.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

// TODO: copy over all the necessary bits from DimensionalDoorBlock
public class DimensionalPortalBlock extends Block implements RiftProvider<EntranceRiftBlockEntity> {
	public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public DimensionalPortalBlock(BlockBehaviour.Properties settings) {
		super(settings);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public EntranceRiftBlockEntity getRift(Level world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) world.getBlockEntity(pos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EntranceRiftBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (world.isClientSide) {
			return;
		}

		this.getRift(world, pos, state).teleport(entity);

		EntranceRiftBlockEntity rift = this.getRift(world, pos, state);

		world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
		((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());

		/*
		New plan, we use players spawn points as the exit points from limbo, this code will no longer be used.
		DimensionalRegistry.getRiftRegistry().setOverworldRift(entity.getUuid(), new Location((ServerWorld) world, pos));
		LOGGER.log(Level.INFO, "Set overworld rift location");

		 */

	}

	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
		return Shapes.block();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isTall(BlockState cachedState) {
		return true;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (world.isClientSide) return;
		((EntranceRiftBlockEntity) world.getBlockEntity(pos)).setPortalDestination((ServerLevel) world);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return Dummy.checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT.get(), DimensionalPortalBlock::portalTick);
	}

	private static void portalTick(Level world, BlockPos pos, BlockState state, EntranceRiftBlockEntity e) {
		if (world.isClientSide() || e.getDestination() != null) {
			return;
		}
		e.setPortalDestination((ServerLevel) world);
	}

	public static final class Dummy extends BaseEntityBlock {
		protected Dummy(Properties settings) {
			super(settings);
		}

		@Nullable
		protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
			return createTickerHelper(givenType, expectedType, ticker);
		}

		@Nullable
		@Override
		public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
			return null;
		}
	}
}
