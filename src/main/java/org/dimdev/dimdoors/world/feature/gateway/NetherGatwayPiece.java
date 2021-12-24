package org.dimdev.dimdoors.world.feature.gateway;

import java.util.Collections;
import java.util.Random;

import com.google.common.collect.ImmutableSet;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherGatwayPiece extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 7;
    private static final int SIZE_Y = 8;
    private static final int SIZE_Z = 9;
    private boolean hasGateway;

    public NetherGatwayPiece(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherGatwayPiece(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, nbt);
        this.hasGateway = nbt.getBoolean("Mob");
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Gateway", this.hasGateway);
    }

    public static NetherFortressGenerator.BridgePlatform create(StructurePiecesHolder holder, int x, int y, int z, int chainLength, Direction orientation) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, SIZE_X, SIZE_Y, 9, orientation);
        if (!NetherFortressGenerator.BridgePlatform.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.BridgePlatform(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
        BlockPos.Mutable blockPos;
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, SIZE_X, SIZE_X, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 0, 0, 5, 1, SIZE_X, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 1, 5, 2, SIZE_X, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 2, 5, 3, SIZE_X, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 4, 3, 5, 4, SIZE_X, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 3, 0, 5, SIZE_Y, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 5, 3, 6, 5, SIZE_Y, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 5, SIZE_Y, 5, 5, SIZE_Y, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        BlockState blockState = Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true).with(FenceBlock.EAST, true);
        BlockState blockState2 = Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true).with(FenceBlock.SOUTH, true);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 1, 6, 3, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 5, 6, 3, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true).with(FenceBlock.NORTH, true), 0, 6, 3, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true).with(FenceBlock.NORTH, true), 6, 6, 3, chunkBox);
        this.fillWithOutline(world, chunkBox, 0, 6, 4, 0, 6, SIZE_X, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 6, 6, 4, 6, 6, SIZE_X, blockState2, blockState2, false);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true).with(FenceBlock.SOUTH, true), 0, 6, SIZE_Y, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true).with(FenceBlock.SOUTH, true), 6, 6, SIZE_Y, chunkBox);
        this.fillWithOutline(world, chunkBox, 1, 6, SIZE_Y, 5, 6, SIZE_Y, blockState, blockState, false);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 1, SIZE_X, SIZE_Y, chunkBox);
        this.fillWithOutline(world, chunkBox, 2, SIZE_X, SIZE_Y, 4, SIZE_X, SIZE_Y, blockState, blockState, false);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 5, SIZE_X, SIZE_Y, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 2, SIZE_Y, SIZE_Y, chunkBox);
        this.addBlock(world, blockState, 3, SIZE_Y, SIZE_Y, chunkBox);
        this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 4, SIZE_Y, SIZE_Y, chunkBox);
        if (!this.hasGateway && chunkBox.contains(blockPos = this.offsetPos(3, 5, 5))) {
            this.hasGateway = true;

            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + 1, 5 + 3, 5 + 0, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + -1, 5 + 3, 5 + 0, chunkBox);

            // Build the columns around the door
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + -1, 5 + 2, 5 + 0, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + 1, 5 + 2, 5 + 0, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + -1, 5 + 1, 5 + 0, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 3 + 1, 5 + 1, 5 + 0, chunkBox);

            world.setBlockState(blockPos, ModBlocks.DIMENSIONAL_PORTAL.getDefaultState(), Block.NOTIFY_LISTENERS);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof RiftBlockEntity) {
                ((RiftBlockEntity)blockEntity).setData(
                        new RiftData()
                                .setProperties(LinkProperties.builder()
                                        .entranceWeight(0.0f)
                                        .groups(ImmutableSet.of(0,1))
                                        .floatingWeight(0.0f)
                                        .linksRemaining(1)
                                        .oneWay(false)
                                        .build())
                                .setDestination(RandomTarget.builder()
                                        .acceptedGroups(Collections.singleton(0))
                                        .weightMaximum(100.0)
                                        .coordFactor(1.0)
                                        .noLinkBack(false)
                                        .positiveDepthFactor(80)
                                        .negativeDepthFactor(10000)
                                        .noLink(false)
                                        .build())
                );
            }
        }
        for (int blockPos2 = 0; blockPos2 <= 6; ++blockPos2) {
            for (int blockEntity = 0; blockEntity <= 6; ++blockEntity) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), blockPos2, -1, blockEntity, chunkBox);
            }
        }
    }
}