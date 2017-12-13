package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityHorizontalEntranceRift;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
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
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHorizontalEntranceRift();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // TODO
        super.breakBlock(worldIn, pos, state);
    }

    public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
        return true; // TODO: locking system
    }

    public static boolean isTrapdoorSetLow(IBlockState state) {
        return state.getValue(BlockTrapDoor.HALF) == DoorHalf.BOTTOM;
    }
}
