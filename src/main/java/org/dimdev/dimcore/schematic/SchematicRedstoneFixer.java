package org.dimdev.dimcore.schematic;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.lang.reflect.Method;

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
        return Blocks.REDSTONE_WIRE.getDefaultState()
                .with(RedstoneWireBlock.WIRE_CONNECTION_WEST, getRenderConnectionType(world, pos, Direction.WEST))
                .with(RedstoneWireBlock.WIRE_CONNECTION_EAST, getRenderConnectionType(world, pos, Direction.EAST))
                .with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, getRenderConnectionType(world, pos, Direction.NORTH))
                .with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, getRenderConnectionType(world, pos, Direction.SOUTH));
    }

    private static WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        try {
            Method m = RedstoneWireBlock.class.getDeclaredMethod("getRenderConnectionType", BlockView.class, BlockPos.class, Direction.class);
            m.setAccessible(true);
            return (WireConnection) m.invoke(Blocks.REDSTONE_WIRE, world, pos, direction);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
