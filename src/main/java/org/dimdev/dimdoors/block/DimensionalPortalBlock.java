package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.util.TeleportUtil;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
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

public class DimensionalPortalBlock extends Block implements RiftProvider<EntranceRiftBlockEntity> {
	public static DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public DimensionalPortalBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) world.getBlockEntity(pos);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EntranceRiftBlockEntity();
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

		EntranceRiftBlockEntity rift = getRift(world, pos, state);

		world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
		((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(rift.getData());
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}
}
