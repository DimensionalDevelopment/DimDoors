package org.dimdev.dimdoors.shared.blocks;

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
import net.minecraft.tileentity.TileEntity;
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
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        // Run server-side only
        if (world.isRemote) return;
        // Get the door's state (many door blockstates are available for the bottom door half only)
        IBlockState doorState = world.getBlockState(state.getValue(HALF) == EnumDoorHalf.UPPER ? pos.down() : pos);
        if (doorState.getBlock() != this) return; // The door is in a half-broken state

        // Check that the door is open and the teleport timer is 0
        if (doorState.getValue(BlockDoor.OPEN) && entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for 2.5s to avoid duplicate teleports

            // Get the rift tile entity and teleport the entity
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);

            if (successful) entity.timeUntilPortal = 0; // Allow teleportation again

            // Close the door when player passes through if enabled in config and door isn't powered
            if (successful && entity instanceof EntityPlayer && !entity.isSneaking() && ModConfig.general.closeDoorBehind && !doorState.getValue(POWERED)) {
                toggleDoor(world, pos, false);
            }
        }
    }

    @Override // Open door even if material is iron
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
        return material == Material.IRON ? 1011 : 1012;
    }

    private int getOpenSound() {
        return material == Material.IRON ? 1005 : 1006;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        // Check that door won't exceed world height.
        if (pos.getY() >= world.getHeight() - 1) {
            return false;
        }

        IBlockState stateUnder = world.getBlockState(pos.down());

        // Check that the bottom block is solid
        if (!stateUnder.isSideSolid(world, pos, EnumFacing.UP) || stateUnder.getBlockFaceShape(world, pos.down(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            return false;
        }

        // Check that the bottom block is replaceable or a rift, and that the top block is replaceable
        IBlockState stateBottom = world.getBlockState(pos);
        IBlockState stateTop = world.getBlockState(pos.up());
        return (stateBottom.getBlock().isReplaceable(world, pos) || stateBottom.getBlock() == ModBlocks.RIFT) &&
               stateTop.getBlock().isReplaceable(world, pos);
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

        if (rift.isLeaveRiftOnBreak() || rift.isRegistered() && RiftRegistry.instance().getSources(new Location(rift.getWorld(), rift.getPos())).size() > 0 && !rift.isAlwaysDelete()) {
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
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public TileEntityEntranceRift getRift(World world, BlockPos pos, IBlockState state) {
        // Rift can be in either top or bottom block
        TileEntity bottomEntity;
        TileEntity topEntity;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            bottomEntity = world.getTileEntity(pos);
            topEntity = world.getTileEntity(pos.up());
        } else {
            bottomEntity = world.getTileEntity(pos.down());
            topEntity = world.getTileEntity(pos);
        }

        // TODO: Also notify player in case of error, don't crash
        if (bottomEntity instanceof TileEntityEntranceRift && topEntity instanceof TileEntityEntranceRift) {
            DimDoors.log.error("Dimensional door at " + pos + " in world " + world + " contained two rifts, please report this. Defaulting to bottom.");
            return (TileEntityEntranceRift) bottomEntity;
        } else if (bottomEntity instanceof TileEntityEntranceRift) {
            return (TileEntityEntranceRift) bottomEntity;
        } else if (topEntity instanceof TileEntityEntranceRift) {
            return (TileEntityEntranceRift) topEntity;
        } else {
            throw new RuntimeException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
        }
    }

    public abstract Item getItem();
}
