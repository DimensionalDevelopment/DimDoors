package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockDimDoorBase extends BlockDoor implements ITileEntityProvider {

    public BlockDimDoorBase(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) pos = pos.down();
        IBlockState doorState = worldIn.getBlockState(pos);
        if (doorState.getValue(BlockDoor.OPEN) && entityIn.timeUntilPortal == 0) {
            entityIn.timeUntilPortal = 50; // 2.5s
            toggleDoor(worldIn, pos, false);
            TileEntityRift rift = getRiftTile(worldIn, pos, worldIn.getBlockState(pos));
            if(!rift.teleport(entityIn) && entityIn instanceof EntityPlayer) {
                DimDoors.chat((EntityPlayer) entityIn, "Teleporting failed because this entrance has no destinations!");
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!checkCanOpen(worldIn, pos, playerIn)) {
            return false;
        }

        BlockPos blockpos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() != this) {
            return false;
        } else {
            super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
            return true;
        }
    }

    public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) { // TODO: locking system
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER;
    }

    // Called to update the render information on the tile entity. Could probably implement a data watcher,
    // but this works fine and is more versatile I think.
    public void updateAttachedTile(World world, BlockPos pos) { // TODO
        DimDoors.proxy.updateDoorTE(this, world, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        updateAttachedTile(worldIn, pos);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) { // TODO: use BLOCK_ITEM map
        return new ItemStack(getItem());
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityVerticalEntranceRift();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!hasTileEntity(state)) return;

        TileEntityEntranceRift origRift = (TileEntityEntranceRift) worldIn.getTileEntity(pos);
        super.breakBlock(worldIn, pos, state);
        if (origRift.isPlaceRiftOnBreak()) {
            worldIn.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift newRift = (TileEntityRift) worldIn.getTileEntity(pos);
            newRift.copyFrom(origRift); // TODO: make sure this works
        }
    }

    public TileEntityEntranceRift getRiftTile(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            tileEntity = world.getTileEntity(pos.up());
        } else {
            tileEntity = world.getTileEntity(pos);
        }
        return (TileEntityEntranceRift) tileEntity;
    }

    public abstract Item getItem();
}
