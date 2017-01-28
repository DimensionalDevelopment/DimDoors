package com.zixiken.dimdoors.shared.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimDoorTransient extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorTransient";

    public BlockDimDoorTransient() {
        super(Material.IRON);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void enterDimDoor(World world, BlockPos pos, Entity entity) {
        // Check that this is the top block of the door
        IBlockState state = world.getBlockState(pos.down());
        if (!world.isRemote && state.getBlock() == this) {
            if (entity instanceof EntityPlayer && isEntityFacingDoor(state, (EntityLivingBase) entity)) {
                // Turn the door into a rift AFTER teleporting the player.
                // The door's orientation may be necessary for the teleport.
                world.setBlockState(pos, ModBlocks.blockRift.getDefaultState());
                world.setBlockToAir(pos.down());
            }
        } else {
            BlockPos up = pos.up();
            if (world.getBlockState(up).getBlock() == this) {
                enterDimDoor(world, up, entity);
            }
        }
    }

    @Override
    public Item getItemDoor() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
