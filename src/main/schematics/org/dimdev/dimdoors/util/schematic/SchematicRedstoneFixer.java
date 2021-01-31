package org.dimdev.dimdoors.util.schematic;

import org.dimdev.dimdoors.mixin.accessor.RedstoneWireBlockAccessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class SchematicRedstoneFixer {
	public static void fixRedstone(Schematic schematic) {
		for (int x = 0; x < schematic.sizeX; x++) {
			for (int y = 0; y < schematic.sizeY; y++) {
				for (int z = 0; z < schematic.sizeZ; z++) {
					BlockState state = schematic.getBlockState(x, y, z);
					if (state != null && state.getBlock() == Blocks.REDSTONE_WIRE) {
						int power = state.get(RedstoneWireBlock.POWER);
						schematic.setBlockState(x, y, z, getPlacementState(schematic, new BlockPos(x, y, z)).with(RedstoneWireBlock.POWER, power));
					}
				}
			}
		}
	}


	public static BlockState getPlacementState(BlockView world, BlockPos pos) {
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
