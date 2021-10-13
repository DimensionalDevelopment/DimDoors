package org.dimdev.dimdoors.block;

import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Level;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// TODO: copy over all the necessary bits from DimensionalDoorBlock
public class DimensionalPortalBlock extends Block implements RiftProvider<EntranceRiftBlockEntity> {
	public static DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public DimensionalPortalBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) world.getBlockEntity(pos);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EntranceRiftBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient) {
			return;
		}

		this.getRift(world, pos, state).teleport(entity);

		EntranceRiftBlockEntity rift = this.getRift(world, pos, state);

		world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
		((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());

		/*
		New plan, we use players spawn points as the exit points from limbo, this code will no longer be used.
		DimensionalRegistry.getRiftRegistry().setOverworldRift(entity.getUuid(), new Location((ServerWorld) world, pos));
		LOGGER.log(Level.INFO, "Set overworld rift location");

		 */

	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isTall(BlockState cachedState) {
		return true;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (world.isClient) return;
		((EntranceRiftBlockEntity) world.getBlockEntity(pos)).setPortalDestination((ServerWorld) world);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return Dummy.checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT, DimensionalPortalBlock::portalTick);
	}

	private static void portalTick(World world, BlockPos pos, BlockState state, EntranceRiftBlockEntity e) {
		if (world.isClient || e.getDestination() != null) {
			return;
		}
		e.setPortalDestination((ServerWorld) world);
	}

	private static final class Dummy extends BlockWithEntity {
		protected Dummy(Settings settings) {
			super(settings);
		}

		@Nullable
		protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
			return BlockWithEntity.checkType(givenType, expectedType, ticker);
		}

		@Nullable
		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return null;
		}
	}
}
