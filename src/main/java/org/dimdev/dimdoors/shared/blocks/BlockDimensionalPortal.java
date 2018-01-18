package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BlockDimensionalPortal extends BlockDimensionalDoor { // TODO: convert to a more general entrances block (like nether portals)

    public static final String ID = "dimensional_portal";

    public BlockDimensionalPortal() {
        super(Material.IRON);
        setHardness(1.0F);
        setUnlocalizedName(ID);
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
    public void setupRift(TileEntityEntranceRift rift) {}

    @Override
    public boolean canBePlacedOnRift() {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;

        // Check that the entity portal timer is 0
        if (entity.timeUntilPortal == 0) {
            entity.timeUntilPortal = 50; // Disable another teleport for that entity for 2.5s
            TileEntityEntranceRift rift = getRift(world, pos, state);
            boolean successful = rift.teleport(entity);
            if (successful) entity.timeUntilPortal = 0; // Allow the entity to teleport if successful
            if (successful && entity instanceof EntityPlayer && rift.isCloseAfterPassThrough()) {
                world.destroyBlock(pos, false);
            }
        }
    }
}
