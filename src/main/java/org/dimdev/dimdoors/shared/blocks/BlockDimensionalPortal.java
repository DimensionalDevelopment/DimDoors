package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public class BlockDimensionalPortal extends BlockDimensionalDoor { // TODO: convert to a more general entrances block (like nether portals)

    public static final String ID = "dimensional_portal";

    public BlockDimensionalPortal() {
        super(Material.PORTAL); // This is the only way to make it collide with water but not other entities, but still have a collision box.
        setHardness(1.0F);
        setLightLevel(0.5F);
        setRegistryName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setDefaultState(super.getDefaultState().withProperty(OPEN, true));
    }

    @Override
    public Item getItem() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        // Patches entities trying to pathfind through this block, however makes them spin like crazy if they end up in this block.
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        // Run server-side only
        if (world.isRemote) return;

        // Check that the teleport timer is 0
        if (entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for 2.5s to avoid duplicate teleports

            // Get the rift tile entity and teleport the entity
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);

            if (successful) entity.timeUntilPortal = 0; // Allow teleportation again

            // Break the entrance if necessary
            if (successful && entity instanceof EntityPlayer && rift.isCloseAfterPassThrough()) {
                world.setBlockToAir(pos);
            }
        }
    }

    @Override
    public TileEntityEntranceRift createNewTileEntity(World world, int meta) {
        TileEntityEntranceRift rift = new TileEntityEntranceRift();
        rift.orientation = getStateFromMeta(meta).getValue(BlockDoor.FACING).getOpposite();
        if (DimDoors.proxy.isClient()) {
            rift.extendUp += 1;
            rift.pushIn = 0.5;
        }
        return rift;
    }
}
