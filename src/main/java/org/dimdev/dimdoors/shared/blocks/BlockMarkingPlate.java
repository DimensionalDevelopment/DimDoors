package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;

public class BlockMarkingPlate extends Block {
    public static final Material FABRIC = new Material(MapColor.BLACK);
    public static final String ID = "marking_plate";
    public static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.875D, 1.0D);

    public BlockMarkingPlate() {
        super(FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setRegistryName(ID);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setHardness(0.1F);
        setSoundType(SoundType.STONE);
        setLightLevel(0);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }
}
