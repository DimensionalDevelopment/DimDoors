package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO: copy over all the necessary bits from DimensionalDoorBlock
public class DimensionalPortalBlock extends WaterLoggableBlockWithEntity implements RiftProvider<EntranceRiftBlockEntity> {
	public static final MapCodec<DimensionalPortalBlock> CODEC = simpleCodec(DimensionalPortalBlock::new);

	public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public DimensionalPortalBlock(BlockBehaviour.Properties settings) {
		super(settings);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public EntranceRiftBlockEntity getRift(ServerLevel world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) world.getBlockEntity(pos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EntranceRiftBlockEntity(pos, state);
	}

	@Override
	public @NotNull RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world instanceof ServerLevel level) {
            this.getRift(level, pos, state).teleport(level.getServer(), entity);

            EntranceRiftBlockEntity rift = this.getRift(level, pos, state);

            world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState());
            world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT.get()).ifPresent(newRift -> newRift.setData(rift.getData()));
        }
    }

	public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isTall(BlockState cachedState) {
		return true;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (world.isClientSide) return;
		world.getBlockEntity(pos, ModBlockEntityTypes.ENTRANCE_RIFT.get()).ifPresent(rift -> rift.setPortalDestination((ServerLevel) world));
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return Dummy.checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT.get(), DimensionalPortalBlock::portalTick);
	}

	private static void portalTick(Level world, BlockPos pos, BlockState state, EntranceRiftBlockEntity e) {
		e.tick(world, pos, state);
		if (world.isClientSide() || e.getDestination() != null) {
			return;
		}
		e.setPortalDestination((ServerLevel) world);
	}

    @Override
    public Optional<RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        return Optional.of(getRift(world, pos, state));
    }

    public static final class Dummy extends BaseEntityBlock {
		public static final MapCodec<Dummy> CODEC = simpleCodec(Dummy::new);

		private Dummy(Properties settings) {
			super(settings);
		}

		@Override
		protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
			return CODEC;
		}

		@Nullable
		public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
			return createTickerHelper(givenType, expectedType, ticker);
		}

		@Nullable
		@Override
		public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
			return null;
		}
	}
}
