package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry;
import com.zixiken.dimdoors.shared.tileentities.TileEntityEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityFloatingRift;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
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

public abstract class BlockDimDoorBase extends BlockDoor implements ITileEntityProvider {
    // TODO: implement RiftProvider as an interface of both doors and trapdoors

    public BlockDimDoorBase(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote) return;
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) pos = pos.down();
        IBlockState doorState = worldIn.getBlockState(pos);
        if (!(doorState.getBlock() instanceof BlockDoor)) return;
        if (doorState.getValue(BlockDoor.OPEN) && entityIn.timeUntilPortal == 0) {
            entityIn.timeUntilPortal = 50; // 2.5s
            TileEntityEntranceRift rift = getRift(worldIn, pos, state);
            boolean successful = rift.teleport(entityIn);
            if (successful)entityIn.timeUntilPortal = 0;
            if (successful && entityIn instanceof EntityPlayer) {
                if(!state.getValue(POWERED)) toggleDoor(worldIn, pos, false); // TODO: config option playerClosesDoorBehind
                if (rift.isCloseAfterPassThrough()) worldIn.destroyBlock(pos, false);
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (hasTileEntity(state) && !DimDoors.disableRiftSetup) { // TODO: better check for disableRiftSetup (support other plugins such as WorldEdit, support doors being placed while schematics are being placed)
            TileEntityEntranceRift rift = createNewTileEntity(worldIn, getMetaFromState(state));

            // Set the virtual location based on where the door was placed
            VirtualLocation virtualLocation = null;
            if (DimDoorDimensions.isPocketDimension(WorldUtils.getDim(worldIn))) {
                Pocket pocket = PocketRegistry.getForDim(WorldUtils.getDim(worldIn)).getPocketFromLocation(pos.getY(), pos.getY(), pos.getZ());
                if (pocket != null) {
                    virtualLocation = pocket.getVirtualLocation();
                } else {
                    virtualLocation = new VirtualLocation(0, 0, 0, 0, 0); // TODO: door was placed in a pocket dim but outside of a pocket...
                }
            }
            if (virtualLocation == null) {
                virtualLocation = new VirtualLocation(WorldUtils.getDim(worldIn), pos.getX(), pos.getY(), pos.getZ(), 0);
            }
            rift.setVirtualLocation(virtualLocation);

            // Configure the rift to its default functionality
            setupRift(rift);

            // Set the tile entity and register it
            worldIn.setTileEntity(pos, rift);
            rift.markDirty();
            rift.register();
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!hasTileEntity(state)) return;
        TileEntityEntranceRift rift = getRift(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
        if (rift.isPlaceRiftOnBreak() || rift.isRegistered() && RiftRegistry.getRiftInfo(new Location(worldIn, pos)).getSources().size() > 0 && !rift.isAlwaysDelete()) {
            TileEntityRift newRift = new TileEntityFloatingRift();
            newRift.copyFrom(rift);
            newRift.updateAvailableLinks();
            worldIn.setBlockState(rift.getPos(), ModBlocks.RIFT.getDefaultState());
            worldIn.setTileEntity(rift.getPos(), newRift);
        } else {
            rift.unregister();
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

    protected abstract void setupRift(TileEntityEntranceRift rift);
}
