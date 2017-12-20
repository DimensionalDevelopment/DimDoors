package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimTrapdoor extends BlockTrapDoor implements ITileEntityProvider {

    public static final String ID = "dimensional_trapdoor";

    public BlockDimTrapdoor() {
        super(Material.WOOD);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setHardness(1.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        // TODO
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return checkCanOpen(worldIn, pos, playerIn) && super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (checkCanOpen(worldIn, pos, null)) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        }
    }

    public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
        return true; // TODO: locking system
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = EnumFacing.UP;
        return rift;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        // TODO
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // TODO
        super.breakBlock(worldIn, pos, state);
    }
}
