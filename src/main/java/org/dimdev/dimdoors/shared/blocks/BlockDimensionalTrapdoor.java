package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

// TODO: Make this placeable on rifts
public abstract class BlockDimensionalTrapdoor extends BlockTrapDoor implements ITileEntityProvider, IRiftProvider<TileEntityEntranceRift> {

    public BlockDimensionalTrapdoor(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;
        // Check that it's a door and that the entity portal timer is 0
        if (state.getValue(BlockDoor.OPEN) && entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for that entity for 2.5s
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);
            if (successful) entity.timeUntilPortal = 0; // Allow the entity to teleport if successful
            if (successful && entity instanceof EntityPlayer) {
                if (world.getStrongPower(pos) == 0) world.setBlockState(pos, state.withProperty(OPEN, false), 2);
                if (rift.isCloseAfterPassThrough()) world.destroyBlock(pos, false);
            }
        }
    }

    @Override // Open trapdoor even if material is iron
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        state = state.cycleProperty(OPEN);
        world.setBlockState(pos, state, 2);
        playSound(player, world, pos, state.getValue(OPEN));
        return true;
    }

    @Override
    public TileEntityEntranceRift createNewTileEntity(World world, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = EnumFacing.UP;
        if (DimDoors.proxy.isClient()) {
            // Trapdoor is on the ground
            rift.pushIn = -0.01;
        }
        return rift;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        getRift(world, pos, state).unregister();
        super.breakBlock(world, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public TileEntityEntranceRift getRift(World world, BlockPos pos, IBlockState state) {
        return (TileEntityEntranceRift) world.getTileEntity(pos);
    }

    public abstract boolean canBePlacedOnRift();
}
