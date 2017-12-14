package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import net.minecraft.block.Block;
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

public abstract class BlockDimDoorBase extends BlockDoor implements ITileEntityProvider { // TODO: implement RiftProvider

    public BlockDimDoorBase(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) pos = pos.down();
        IBlockState doorState = worldIn.getBlockState(pos);
        if (!(doorState.getBlock() instanceof BlockDoor)) return;
        if (doorState.getValue(BlockDoor.OPEN) && entityIn.timeUntilPortal == 0) {
            entityIn.timeUntilPortal = 50; // 2.5s
            toggleDoor(worldIn, pos, false);
            TileEntityEntranceRift rift = getRift(worldIn, pos, state);
            if (!rift.teleport(entityIn) && entityIn instanceof EntityPlayer) {
                DimDoors.chat((EntityPlayer) entityIn, "Teleporting failed because this entrance has no destinations!");
            } else if (rift.isCloseAfterPassThrough()) { // TODO: move logic to TileEntityEntranceRift?
                worldIn.destroyBlock(pos, false);
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
            state = iblockstate.cycleProperty(OPEN);
            worldIn.setBlockState(blockpos, state, 10);
            worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
            worldIn.playEvent(playerIn, state.getValue(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);
            return true;
        }
    }

    private int getCloseSound()
    {
        return blockMaterial == Material.IRON ? 1011 : 1012;
    }

    private int getOpenSound()
    {
        return blockMaterial == Material.IRON ? 1005 : 1006;
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (hasTileEntity(state)) {
            TileEntityVerticalEntranceRift rift = createNewTileEntity(worldIn, getMetaFromState(state));
            rift.orientation = state.getValue(BlockDoor.FACING).getOpposite();
            worldIn.setTileEntity(pos, rift);
            rift.markDirty();
        }
    }


    @Override
    public TileEntityVerticalEntranceRift createNewTileEntity(World worldIn, int meta) {
        return new TileEntityVerticalEntranceRift();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!hasTileEntity(state)) return;
        TileEntityEntranceRift origRift = getRift(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
        if (origRift.isPlaceRiftOnBreak()) {
            worldIn.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift newRift = (TileEntityRift) worldIn.getTileEntity(pos);
            newRift.copyFrom(origRift);
            worldIn.setBlockState(pos, ModBlocks.RIFT.getDefaultState()); // TODO: send the TileEntity
        }
    }

    public TileEntityEntranceRift getRift(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileEntityRift)) tileEntity = world.getTileEntity(pos.up());
        } else {
            tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileEntityRift)) tileEntity = world.getTileEntity(pos.down());
        }
        return (TileEntityEntranceRift) tileEntity;
    }

    public abstract Item getItem();
}
