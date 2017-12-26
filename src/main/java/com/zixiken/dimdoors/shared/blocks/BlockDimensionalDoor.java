package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.shared.rifts.RiftRegistry;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import ddutils.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
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

public abstract class BlockDimensionalDoor extends BlockDoor implements IRiftProvider<TileEntityEntranceRift> {

    public BlockDimensionalDoor(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) pos = pos.down();
        IBlockState doorState = world.getBlockState(pos);
        if (!(doorState.getBlock() instanceof BlockDoor)) return;

        // Check that it's a door and that the entity portal timer is 0
        if (doorState.getValue(BlockDoor.OPEN) && entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for that entity for 2.5s
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);
            if (successful) entity.timeUntilPortal = 0; // Allow the entity to teleport if successful
            if (successful && entity instanceof EntityPlayer) {
                if (!state.getValue(POWERED)) toggleDoor(world, pos, false); // TODO: config option playerClosesDoorBehind
                if (rift.isCloseAfterPassThrough()) world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!canOpen(worldIn, pos, playerIn)) return false;

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

    private int getCloseSound() {
        return blockMaterial == Material.IRON ? 1011 : 1012;
    }

    private int getOpenSound() {
        return blockMaterial == Material.IRON ? 1005 : 1006;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (canOpen(worldIn, pos, null)) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        }
    }

    public boolean canOpen(World world, BlockPos pos, EntityPlayer player) {
        return true; // TODO: locking system
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER;
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
    public TileEntityEntranceRift createNewTileEntity(World worldIn, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = getStateFromMeta(meta).getValue(BlockDoor.FACING).getOpposite();
        rift.extendUp += 1;
        return rift;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!hasTileEntity(state)) return;
        TileEntityEntranceRift rift = getRift(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
        if (rift.isPlaceRiftOnBreak() || rift.isRegistered() && RiftRegistry.getRiftInfo(rift.getLocation()).getSources().size() > 0 && !rift.isAlwaysDelete()) {
            TileEntityRift newRift = new TileEntityFloatingRift();
            newRift.copyFrom(rift);
            newRift.updateAvailableLinks();
            worldIn.setBlockState(rift.getPos(), ModBlocks.RIFT.getDefaultState());
            worldIn.setTileEntity(rift.getPos(), newRift);
        } else {
            rift.unregister();
        }
    }

    @Override
    public TileEntityEntranceRift getRift(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            return (TileEntityEntranceRift) world.getTileEntity(pos);
        } else {
            return (TileEntityEntranceRift) world.getTileEntity(pos.down());
        }
    }

    public abstract Item getItem();
}
