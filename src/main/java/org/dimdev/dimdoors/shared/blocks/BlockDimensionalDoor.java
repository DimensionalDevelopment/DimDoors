package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.Random;

public abstract class BlockDimensionalDoor extends BlockDoor implements IRiftProvider<TileEntityEntranceRift> {

    public BlockDimensionalDoor(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;

        IBlockState doorState = world.getBlockState(state.getValue(HALF) == EnumDoorHalf.UPPER ? pos.down() : pos); // .down() because only the bottom block has open=true

        // Check that it's a door and that the entity portal timer is 0
        if (doorState.getBlock().equals(this) && doorState.getValue(BlockDoor.OPEN) && entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for that entity for 2.5s
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);
            if (successful) entity.timeUntilPortal = 0; // Allow the entity to teleport if successful
            if (successful && entity instanceof EntityPlayer) {
                if (ModConfig.general.closeDoorBehind && !state.getValue(POWERED)) toggleDoor(world, pos, false);
                if (rift.isCloseAfterPassThrough()) world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!canOpen(world, pos, player)) return false;

        BlockPos blockpos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate = pos.equals(blockpos) ? state : world.getBlockState(blockpos);

        if (iblockstate.getBlock() != this) {
            return false;
        } else {
            state = iblockstate.cycleProperty(OPEN);
            world.setBlockState(blockpos, state, 10);
            world.markBlockRangeForRenderUpdate(blockpos, pos);
            world.playEvent(player, state.getValue(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);
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
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (canOpen(world, pos, null)) {
            super.neighborChanged(state, world, pos, block, fromPos);
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
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        // Any door block can be placed on rifts now, items will enforce restrictions now.
        if (pos.getY() >= world.getHeight() - 1) {
            return false;
        } else {
            IBlockState state = world.getBlockState(pos.down());
            return (state.isSideSolid(world, pos, EnumFacing.UP)
                    || state.getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID)
                    && (world.getBlockState(pos).getBlock().isReplaceable(world, pos) || world.getBlockState(pos).getBlock().equals(ModBlocks.RIFT))
                    && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(getItem());
    }

    @Override
    public TileEntityEntranceRift createNewTileEntity(World world, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = getStateFromMeta(meta).getValue(BlockDoor.FACING).getOpposite();
        if (DimDoors.proxy.isClient()) {
            rift.extendUp += 1;
        }
        return rift;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!hasTileEntity(state)) return;
        TileEntityEntranceRift rift = getRift(world, pos, state);
        super.breakBlock(world, pos, state);
        if (world.isRemote) return;
        if (rift == null) {
            DimDoors.log.error("Rift tile entity was null when breaking block at " + new Location(world, pos) + ", please report this error.");
        }
        if (rift.isLeaveScarWhenClosed() || rift.isRegistered() && RiftRegistry.instance().getSources(new Location(rift.getWorld(), rift.getPos())).size() > 0 && !rift.isAlwaysDelete()) {
            world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            // This isn't run when we change the block from within block break
            TileEntityFloatingRift newRift = (TileEntityFloatingRift) world.getTileEntity(pos);
            newRift.copyFrom(rift);
            newRift.updateType();
        } else {
            rift.unregister();
        }
    }

    // Let vanilla handle breaking the block. If we break it here, the rift we place will later be broken.
    @Override public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {}

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
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
