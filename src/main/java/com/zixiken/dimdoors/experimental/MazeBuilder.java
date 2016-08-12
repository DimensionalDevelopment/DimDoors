package com.zixiken.dimdoors.experimental;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import com.zixiken.dimdoors.Point3D;

public class MazeBuilder {
    private MazeBuilder() {
    }

    public static void generate(World world, int x, int y, int z, Random random) {
        MazeDesign design = MazeDesigner.generate(random);
        BlockPos offset = new BlockPos(x - design.width() / 2, y - design.height() - 1, z - design.length() / 2);
        SphereDecayOperation decay = new SphereDecayOperation(random, Blocks.air.setBlockUnbreakable().getDefaultState(), Blocks.stonebrick.setBlockUnbreakable().getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED));

        buildRooms(design.getRoomGraph(), world, offset);
        carveDoorways(design.getRoomGraph(), world, offset, decay, random);

        //placeDoors(design, world, offset);

        applyRandomDestruction(design, world, offset, decay, random);
    }

    private static void applyRandomDestruction(MazeDesign design, World world, BlockPos offset, SphereDecayOperation decay, Random random) {
        //final int DECAY_BOX_SIZE = 8
    }

    private static void buildRooms(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, BlockPos offset) {
        for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes()) {
            PartitionNode room = node.data();
            buildBox(world, offset, room.minCorner(), room.maxCorner(), Blocks.stonebrick.getDefaultState());
        }
    }

    private static void carveDoorways(DirectedGraph<PartitionNode, DoorwayData> roomGraph, World world, BlockPos offset, SphereDecayOperation decay, Random random) {
        char axis;
        BlockPos lower;
        DoorwayData doorway;

        for (IGraphNode<PartitionNode, DoorwayData> node : roomGraph.nodes()) {
            for (IEdge<PartitionNode, DoorwayData> passage : node.outbound()) {
                doorway = passage.data();
                axis = doorway.axis();
                lower = doorway.minCorner();
                carveDoorway(world, axis, offset.add(lower), doorway.volume(), decay, random);
            }
        }
    }

    private static void carveDoorway(World world, char axis, BlockPos pos, BlockPos volume, SphereDecayOperation decay, Random random) {
        final int MIN_DOUBLE_DOOR_SPAN = 10;
        int gap;

        switch (axis) {
            case DoorwayData.X_AXIS:
                if (pos.getZ() >= MIN_DOUBLE_DOOR_SPAN) {
                    gap = (pos.getZ() - 2) / 3;
                    carveDoorAlongX(world, pos.add(0, 1, gap));
                    carveDoorAlongX(world, pos.add(0, 1, pos.getZ() - gap - 1));
                } else if (pos.getZ() > 3) {
                    switch (random.nextInt(3)) {
                        case 0:
                            carveDoorAlongX(world, pos.add(0, 1, (pos.getZ() - 1) / 2));
                            break;
                        case 1:
                            carveDoorAlongX(world, pos.add(0, 1, 2));
                            break;
                        case 2:
                            carveDoorAlongX(world, pos.add(0,1,-3));
                            break;
                    }
                } else {
                    carveDoorAlongX(world, pos.add(0,1,1));
                }
                break;
            case DoorwayData.Z_AXIS:
                if (pos.getX() >= MIN_DOUBLE_DOOR_SPAN) {
                    gap = (pos.getX() - 2) / 3;
                    carveDoorAlongZ(world, pos.add(gap, 1, 0));
                    carveDoorAlongZ(world, pos.add(pos.getX() - gap - 1, 1, 0));
                } else if (pos.getZ() > 3) {
                    switch (random.nextInt(3)) {
                        case 0:
                            carveDoorAlongZ(world, pos.add((volume.getX() - 1) / 2, 1, 0));
                            break;
                        case 1:
                            carveDoorAlongZ(world, pos.add(2,1,0));
                            break;
                        case 2:
                            carveDoorAlongZ(world, pos.add(volume.getX() - 3, 1, 0));
                            break;
                    }
                } else {
                    carveDoorAlongZ(world, pos.add(1,1,0));
                }
                break;

            case DoorwayData.Y_AXIS:
                gap = Math.min(volume.getX(), pos.getZ()) - 2;
                if (gap > 1) {
                    if (gap > 6) {
                        gap = 6;
                    }
                    decay.apply(world, new BlockPos(random.nextInt(volume.getX() - gap - 1) + 1, -1, random.nextInt(pos.getZ() - gap - 1) + 1), new BlockPos(gap, 4, gap));
                } else {
                    carveHole(world, pos.add(1,0,1));
                }
                break;
        }
    }

    private static void carveDoorAlongX(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.air.getDefaultState());
        world.setBlockState(pos.add(0, 1, 0), Blocks.air.getDefaultState());
        world.setBlockState(pos.add(1, 0, 0), Blocks.air.getDefaultState());
        world.setBlockState(pos.add(1, 1, 0), Blocks.air.getDefaultState());
    }

    private static void carveDoorAlongZ(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.air.getDefaultState());
        world.setBlockState(pos.add(0, 1, 0), Blocks.air.getDefaultState());
        world.setBlockState(pos.add(0, 0, 1), Blocks.air.getDefaultState());
        world.setBlockState(pos.add(0, 0, 0), Blocks.air.getDefaultState());
    }

    private static void carveHole(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.air.getDefaultState());
        world.setBlockState(pos.up(), Blocks.air.getDefaultState());
    }


    private static void buildBox(World world, BlockPos offset, BlockPos minCorner, BlockPos maxCorner, IBlockState state) {
        int minX = minCorner.getX() + offset.getX();
        int minY = minCorner.getY() + offset.getY();
        int minZ = minCorner.getZ() + offset.getZ();

        int maxX = maxCorner.getX() + offset.getX();
        int maxY = maxCorner.getY() + offset.getY();
        int maxZ = maxCorner.getZ() + offset.getZ();

        int x, y, z;

        for (x = minX; x <= maxX; x++) {
            for (z = minZ; z <= maxZ; z++) {
                world.setBlockState(new BlockPos(x, minY, z), state);
                world.setBlockState(new BlockPos(x, maxY, z), state);
            }
        }

        for (x = minX; x <= maxX; x++) {
            for (y = minY; y <= maxY; y++) {
                world.setBlockState(new BlockPos(x, y, minZ), state);
                world.setBlockState(new BlockPos(x, y, maxZ), state);
            }
        }

        for (z = minZ; z <= maxZ; z++) {
            for (y = minY; y <= maxY; y++) {
                world.setBlockState(new BlockPos(minX, y, z), state);
                world.setBlockState(new BlockPos(maxX, y, z), state);
            }
        }
    }
}