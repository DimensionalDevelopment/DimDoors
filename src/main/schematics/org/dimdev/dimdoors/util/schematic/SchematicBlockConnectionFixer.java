package org.dimdev.dimdoors.util.schematic;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.StairShape;
import org.dimdev.dimdoors.mixin.accessor.RedstoneWireBlockAccessor;

import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

// TODO: probably need to fix tall_grass -> upper, lower
public class SchematicBlockConnectionFixer {
	public static void fixBlocks(Schematic schematic) {
		for (int x = 0; x < schematic.sizeX; x++) {
			for (int y = 0; y < schematic.sizeY; y++) {
				for (int z = 0; z < schematic.sizeZ; z++) {
					BlockState state = schematic.getBlockState(x, y, z);
					if (state != null) {
						Block block = state.getBlock();
						if (block == Blocks.REDSTONE_WIRE) {
							int power = state.get(RedstoneWireBlock.POWER);
							schematic.setBlockState(x, y, z, getRedstonePlacementState(schematic, new BlockPos(x, y, z)).with(RedstoneWireBlock.POWER, power));
						} else if (block instanceof FenceBlock) {
							schematic.setBlockState(x, y, z, getFencePlacementState(schematic, new BlockPos(x, y, z), (FenceBlock) block, state));
						} else if (block instanceof PaneBlock) {
							schematic.setBlockState(x, y, z, getPanePlacementState(schematic, new BlockPos(x, y, z), (PaneBlock) block, state));
						} else if (block instanceof DoorBlock && state.get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
							schematic.setBlockState(x, y, z, getUpperDoorHalfPlacementState(schematic, new BlockPos(x, y, z), (DoorBlock) block, state));
						} else if (block instanceof StairsBlock) {
							schematic.setBlockState(x, y, z, state.with(StairsBlock.SHAPE, getStairShape(state, schematic, new BlockPos(x, y, z))));
						}
					}
				}
			}
		}
	}

	public static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = (Direction)state.get(StairsBlock.FACING);
		BlockState blockState = world.getBlockState(pos.offset(direction));
		if (StairsBlock.isStairs(blockState) && state.get(StairsBlock.HALF) == blockState.get(StairsBlock.HALF)) {
			Direction direction2 = (Direction)blockState.get(StairsBlock.FACING);
			if (direction2.getAxis() != ((Direction)state.get(StairsBlock.FACING)).getAxis() && method_10678(state, world, pos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.OUTER_LEFT;
				}

				return StairShape.OUTER_RIGHT;
			}
		}

		BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
		if (StairsBlock.isStairs(blockState2) && state.get(StairsBlock.HALF) == blockState2.get(StairsBlock.HALF)) {
			Direction direction3 = (Direction)blockState2.get(StairsBlock.FACING);
			if (direction3.getAxis() != ((Direction)state.get(StairsBlock.FACING)).getAxis() && method_10678(state, world, pos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.INNER_LEFT;
				}

				return StairShape.INNER_RIGHT;
			}
		}

		return StairShape.STRAIGHT;
	}

	private static boolean method_10678(BlockState state, BlockView world, BlockPos pos, Direction dir) {
		BlockState blockState = world.getBlockState(pos.offset(dir));
		return !StairsBlock.isStairs(blockState) || blockState.get(StairsBlock.FACING) != state.get(StairsBlock.FACING) || blockState.get(StairsBlock.HALF) != state.get(StairsBlock.HALF);
	}

	public static BlockState getUpperDoorHalfPlacementState(BlockView world, BlockPos pos, DoorBlock block, BlockState state) {
		BlockState lower = world.getBlockState(pos.down());
		return state.with(DoorBlock.FACING, lower.get(DoorBlock.FACING)).with(DoorBlock.HINGE, lower.get(DoorBlock.HINGE)).with(DoorBlock.OPEN, lower.get(DoorBlock.OPEN)).with(DoorBlock.POWERED, lower.get(DoorBlock.POWERED));
	}

	public static BlockState getPanePlacementState(BlockView world, BlockPos pos, PaneBlock block, BlockState state) {
		BlockPos blockPos2 = pos.north();
		BlockPos blockPos3 = pos.east();
		BlockPos blockPos4 = pos.south();
		BlockPos blockPos5 = pos.west();
		BlockState blockState = world.getBlockState(blockPos2);
		BlockState blockState2 = world.getBlockState(blockPos3);
		BlockState blockState3 = world.getBlockState(blockPos4);
		BlockState blockState4 = world.getBlockState(blockPos5);
		return state
				.with(HorizontalConnectingBlock.NORTH, block.connectsTo(blockState, blockState.isSideSolidFullSquare(world, blockPos2, Direction.SOUTH)))
				.with(HorizontalConnectingBlock.EAST, block.connectsTo(blockState2, blockState2.isSideSolidFullSquare(world, blockPos3, Direction.WEST)))
				.with(HorizontalConnectingBlock.SOUTH, block.connectsTo(blockState3, blockState3.isSideSolidFullSquare(world, blockPos4, Direction.NORTH)))
				.with(HorizontalConnectingBlock.WEST, block.connectsTo(blockState4, blockState4.isSideSolidFullSquare(world, blockPos5, Direction.EAST)));
	}

	public static BlockState getFencePlacementState(BlockView world, BlockPos pos, FenceBlock block, BlockState state) {
		BlockPos blockPos2 = pos.north();
		BlockPos blockPos3 = pos.east();
		BlockPos blockPos4 = pos.south();
		BlockPos blockPos5 = pos.west();
		BlockState blockState = world.getBlockState(blockPos2);
		BlockState blockState2 = world.getBlockState(blockPos3);
		BlockState blockState3 = world.getBlockState(blockPos4);
		BlockState blockState4 = world.getBlockState(blockPos5);
		return state
				.with(HorizontalConnectingBlock.NORTH, block.canConnect(blockState, blockState.isSideSolidFullSquare(world, blockPos2, Direction.SOUTH), Direction.SOUTH))
				.with(HorizontalConnectingBlock.EAST, block.canConnect(blockState2, blockState2.isSideSolidFullSquare(world, blockPos3, Direction.WEST), Direction.WEST))
				.with(HorizontalConnectingBlock.SOUTH, block.canConnect(blockState3, blockState3.isSideSolidFullSquare(world, blockPos4, Direction.NORTH), Direction.NORTH))
				.with(HorizontalConnectingBlock.WEST, block.canConnect(blockState4, blockState4.isSideSolidFullSquare(world, blockPos5, Direction.EAST), Direction.EAST));
	}

	public static BlockState getRedstonePlacementState(BlockView world, BlockPos pos) {
		WireConnection west = getRenderConnectionType(world, pos, Direction.WEST);
		WireConnection east = getRenderConnectionType(world, pos, Direction.EAST);
		WireConnection north = getRenderConnectionType(world, pos, Direction.NORTH);
		WireConnection south = getRenderConnectionType(world, pos, Direction.SOUTH);
		int connectionCount = 0;
		if (west.isConnected()) connectionCount++;
		if (east.isConnected()) connectionCount++;
		if (north.isConnected()) connectionCount++;
		if (south.isConnected()) connectionCount++;

		switch (connectionCount) {
			case 0: // should actually connect to all sides, forming a cross
				return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
			case 1: // should actually connect to the other side as well, forming a line
				if (west.isConnected()) return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, west)
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.NONE);
				if (east.isConnected()) return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, east)
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.NONE);
				if (north.isConnected()) return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, north)
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
				return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.NONE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, south);
			default:
				return Blocks.REDSTONE_WIRE.getDefaultState()
						.with(RedstoneWireBlock.WIRE_CONNECTION_WEST, getRenderConnectionType(world, pos, Direction.WEST))
						.with(RedstoneWireBlock.WIRE_CONNECTION_EAST, getRenderConnectionType(world, pos, Direction.EAST))
						.with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, getRenderConnectionType(world, pos, Direction.NORTH))
						.with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, getRenderConnectionType(world, pos, Direction.SOUTH));
		}
	}

	private static WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
		return ((RedstoneWireBlockAccessor) Blocks.REDSTONE_WIRE).invokeGetRenderConnectionType(world, pos, direction);
	}
}
