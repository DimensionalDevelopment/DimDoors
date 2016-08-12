package com.zixiken.dimdoors.world.fortresses;

import java.util.List;
import java.util.Random;

import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import com.zixiken.dimdoors.core.DimData;

public class ComponentNetherGateway extends StructureComponent {
    // Note: In this case, it doesn't really matter which class we extend, since this class will
    // never be passed to Minecraft. We just need an instance to have access to structure-building methods.
    // If Forge supports adding custom fortress structures in the future, then we might have to change
    // our class to extend ComponentNetherBridgeCrossing or something along those lines. ~SenseiKiwi

    public ComponentNetherGateway(int componentType, Random random, StructureBoundingBox bounds, EnumFacing coordBaseMode) {
        super(componentType);

        this.boundingBox = bounds;
        this.coordBaseMode = coordBaseMode;
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherGateway createValidComponent(List components, Random random, int minX, int minY, int minZ, EnumFacing coordBaseMode, int componentType) {
        StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(minX, minY, minZ, -2, 0, 0, 7, 9, 7, coordBaseMode);
        return isAboveGround(bounds) && StructureComponent.findIntersecting(components, bounds) == null ? new ComponentNetherGateway(componentType, random, bounds, coordBaseMode) : null;
    }

    public static ComponentNetherGateway createFromComponent(StructureComponent component, Random random) {
        // Create an instance of our gateway component using the same data as another component,
        // likely a component that we intend to replace during generation
        return new ComponentNetherGateway( component.getComponentType(), random, component.getBoundingBox(), getCoordBaseMode(component));
    }

    private static EnumFacing getCoordBaseMode(StructureComponent component) {
        // This is a hack to get the value of a component's coordBaseMode field.
        // It's essentially the orientation of the component... with a weird name.
        return EnumFacing.getHorizontal(component.createStructureBaseNBT().getInteger("O"));
    }

    /**
     * Checks if the bounding box's minY is > 10
     */
    protected static boolean isAboveGround(StructureBoundingBox par0StructureBoundingBox) {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        // Set all the blocks in the area of the room to air
        this.fillWithBlocks(world, bounds, 0, 2, 0, 6, 6, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        // Set up the platform under the gateway
        this.fillWithBlocks(world, bounds, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);

        // Build the fence at the back of the room
        this.fillWithBlocks(world, bounds, 1, 2, 6, 5, 2, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.fillWithBlocks(world, bounds, 1, 3, 6, 5, 3, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);

        // Build the fences at the sides of the room
        this.fillWithBlocks(world, bounds, 0, 2, 0, 0, 2, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.fillWithBlocks(world, bounds, 0, 3, 0, 0, 3, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);

        this.fillWithBlocks(world, bounds, 6, 2, 0, 6, 2, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.fillWithBlocks(world, bounds, 6, 3, 0, 6, 3, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false);

        // Build the fence portions closest to the entrance
        this.setBlockState(world, Blocks.nether_brick.getDefaultState(), 1, 2, 0, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 1, 3, 0, bounds);

        this.setBlockState(world, Blocks.nether_brick.getDefaultState(), 5, 2, 0, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 5, 3, 0, bounds);

        // Build the first layer of the gateway
        this.fillWithBlocks(world, bounds, 1, 2, 2, 5, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.fillWithBlocks(world, bounds, 1, 2, 1, 5, 2, 1, Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK), Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK), false);

        this.setBlockState(world, Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK), 1, 2, 2, bounds);
        this.setBlockState(world, Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK), 5, 2, 2, bounds);

        // Build the second layer of the gateway
        EnumFacing orientation = EnumFacing.WEST;
        this.fillWithBlocks(world, bounds, 2, 3, 3, 2, 3, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.fillWithBlocks(world, bounds, 4, 3, 3, 4, 3, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false);
        this.setBlockState(world, Blocks.nether_brick.getDefaultState(), 3, 3, 4, bounds);
        this.setBlockState(world, Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, orientation), 3, 3, 5, bounds);

        // Build the third layer of the gateway
        // We add 4 to get the rotated metadata for upside-down stairs
        // because Minecraft only supports metadata rotations for normal stairs -_-
        this.fillWithBlocks(world, bounds, 2, 4, 4, 4, 4, 4, Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, orientation), Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.FACING, orientation), false);

        IBlockState state = Blocks.nether_brick_stairs.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);

        this.setBlockState(world, state, 2, 4, 3, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.EAST), 4, 4, 3, bounds);

        // Build the fourth layer of the gateway
        this.setBlockState(world, Blocks.nether_brick.getDefaultState(), 3, 5, 3, bounds);

        this.setBlockState(world, Blocks.netherrack.getDefaultState(), 2, 5, 3, bounds);
        this.setBlockState(world, state, 1, 5, 3, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.EAST), 2, 5, 2, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.NORTH), 2, 5, 4, bounds);

        this.setBlockState(world, Blocks.netherrack.getDefaultState(), 4, 5, 3, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.WEST), 5, 5, 3, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.EAST), 4, 5, 2, bounds);
        this.setBlockState(world, state.withProperty(BlockStairs.FACING, EnumFacing.NORTH), 4, 5, 4, bounds);

        // Build the top layer of the gateway
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 3, 6, 3, bounds);

        this.setBlockState(world, Blocks.fire.getDefaultState(), 2, 6, 3, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 1, 6, 3, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 2, 6, 2, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 2, 6, 4, bounds);

        this.setBlockState(world, Blocks.fire.getDefaultState(), 4, 6, 3, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 5, 6, 3, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 4, 6, 2, bounds);
        this.setBlockState(world, Blocks.nether_brick_fence.getDefaultState(), 4, 6, 4, bounds);

        // Place the transient door
        BlockPos pos = new BlockPos(this.getYWithOffset(3), this.getXWithOffset(3, 3), this.getZWithOffset(3, 3));

        DimLink link;
        DimData dimension;

        // This function might run multiple times for a single component
        // due to the way Minecraft handles structure generation!
        if (bounds.isVecInside(pos) && bounds.isVecInside(pos.up())) {
            orientation = Blocks.oak_door.getDefaultState().getValue(BlockDoor.FACING);

            dimension = PocketManager.createDimensionData(world);

            link = dimension.getLink(pos.up());
            if (link == null) {
                link = dimension.createLink(pos.up(), LinkType.DUNGEON, orientation);
            }

            ItemDoor.placeDoor(world, pos, orientation, DimDoors.transientDoor);
        }

        for (int i = 0; i <= 6; ++ i) {
            for (int k = 0; k <= 6; ++k) {
                this.setBlockState(world, Blocks.nether_brick.getDefaultState(), i, -1, k, bounds);
            }
        }

        return true;
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {}

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound) {}
}