package org.dimdev.dimdoors.shared.blocks;

import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockDimensionalTrapdoor extends BlockTrapDoor implements ITileEntityProvider, IRiftProvider<TileEntityEntranceRift> {

    public BlockDimensionalTrapdoor(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;

        // Check that it's a door and that the entity portal timer is 0
        if (state.getValue(BlockDoor.OPEN) && entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for that entity for 2.5s
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);
            if (successful) entity.timeUntilPortal = 0; // Allow the entity to teleport if successful
            if (successful && entity instanceof EntityPlayer) {
                if (world.getStrongPower(pos) == 0) world.setBlockState(pos, state.withProperty(OPEN, false), 2); // TODO: config option playerClosesDoorBehind
                if (rift.isCloseAfterPassThrough()) world.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!canOpen(worldIn, pos, playerIn)) return false;

        state = state.cycleProperty(OPEN);
        worldIn.setBlockState(pos, state, 2);
        playSound(playerIn, worldIn, pos, state.getValue(OPEN));
        return true;
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
    public TileEntityEntranceRift createNewTileEntity(World worldIn, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = EnumFacing.UP;
        return rift;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        getRift(world, pos, state).unregister();
        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntityEntranceRift getRift(World world, BlockPos pos, IBlockState state) {
        return (TileEntityEntranceRift) world.getTileEntity(pos);
    }
}
