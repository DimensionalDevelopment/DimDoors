package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockDimDoorBase extends BlockDoor implements IDimDoor, ITileEntityProvider {

    public BlockDimDoorBase(Material material) {
        super(material);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        IBlockState down = world.getBlockState(pos.down());
        if (!world.isRemote && down.getBlock() == this) { //@todo should only teleport when colliding with top part of the door?
            if (down.getValue(BlockDoor.OPEN)
                    && entity instanceof EntityPlayer //@todo remove this so any entity can go through?
                    && (entity.timeUntilPortal < 1) //to prevent the player from teleporting all over the place we have a 150-tick cooldown
                    && isEntityFacingDoor(down, (EntityLivingBase) entity)) {
                this.toggleDoor(world, pos, false);
                enterDimDoor(world, pos, entity);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!checkCanOpen(world, pos, player)) {
            return false;
        }

        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            pos = pos.down();
            state = world.getBlockState(pos);
        }

        if (state.getBlock() != this) {
            return false;
        } else {
            state = state.cycleProperty(BlockDoor.OPEN);
            world.setBlockState(pos, state, 2);
            world.markBlockRangeForRenderUpdate(pos, pos.up());
            return true;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;
    }

    //Called to update the render information on the tile entity. Could probably implement a data watcher,
    //but this works fine and is more versatile I think. 
    public BlockDimDoorBase updateAttachedTile(World world, BlockPos pos) {
        DimDoors.proxy.updateDoorTE(this, world, pos);
        return this;
    }

    public boolean isDoorOnRift(World world, BlockPos pos) {
        return true;
    }

    @Override
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random rand) {
        updateAttachedTile(par1World, pos);
    }

    /**
     * only called by clickMiddleMouseButton , and passed to
     * inventory.setCurrentItem (along with isCreative)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this.getItemDoor(), 1, 0);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return isUpperDoorBlock(state) ? null : this.getItemDoor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this.getItemDoor(), 1, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) { //gets called upon world load as well
        return new TileEntityDimDoor();
    }

    @Override
    public void enterDimDoor(World world, BlockPos pos, Entity entity) {
        DDTileEntityBase riftTile = getRiftTile(world, pos, world.getBlockState(pos));
        if (riftTile.tryTeleport(entity)) {
            //player is succesfully teleported
        } else {
            //probably should only happen on personal dimdoors
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
                DimDoors.chat(entityPlayer, "Teleporting failed, but since mod is still in alpha, stuff like that might simply happen.");
            }
        }
    }

    public boolean isUpperDoorBlock(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;
    }

    public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    protected static boolean isEntityFacingDoor(IBlockState state, EntityLivingBase entity) {
        // Although any entity has the proper fields for this check,
        // we should only apply it to living entities since things
        // like Minecarts might come in backwards.
        return (state.getValue(BlockDoor.FACING) == EnumFacing.fromAngle(entity.rotationYaw));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        DDTileEntityBase origRift = null;
        boolean isTopHalf = state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;
        if (isTopHalf) {
            origRift = (DDTileEntityBase) world.getTileEntity(pos);
            RiftRegistry.Instance.setLastChangedRift(origRift);
        }
        super.breakBlock(world, pos, state);
        if (isTopHalf) {
            world.setBlockState(pos, ModBlocks.blockRift.getDefaultState());
            DDTileEntityBase newRift = (DDTileEntityBase) world.getTileEntity(pos);
            newRift.loadDataFrom(origRift);
        }
    }

    //returns the DDTileEntityBase that is the tile entity belonging to the door block "state" at this "pos" in the "world"
    public DDTileEntityBase getRiftTile(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            tileEntity = world.getTileEntity(pos.up());
        } else {
            tileEntity = world.getTileEntity(pos);
        }
        return (DDTileEntityBase) tileEntity;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) { //returns whether or not the entire door (2 blocks tall) can be placed at this pos. pos is the position of the bottom half of the door
        IBlockState groundState = worldIn.getBlockState(pos.down());
        IBlockState bottomState = worldIn.getBlockState(pos);
        IBlockState topState = worldIn.getBlockState(pos.up());
        return pos.up().getY() > worldIn.getHeight() - 1 || pos.getY() < 1 ? false //top half can never be placed above buildHeight (255), (worldIn.getHeight() should return 256) and bottom half can never be placed below y=1
                : groundState.isSideSolid(worldIn, pos.down(), EnumFacing.UP)
                && canPlaceBottomAt(worldIn, pos, bottomState) && canPlaceTopAt(worldIn, pos, topState);
    }

    private boolean canPlaceBottomAt(World worldIn, BlockPos pos, IBlockState state) {
        return (state.equals(Blocks.AIR) || state.getBlock().isReplaceable(worldIn, pos));
    }

    private boolean canPlaceTopAt(World worldIn, BlockPos pos, IBlockState state) {
        return (state.getBlock() == ModBlocks.blockRift || state.equals(Blocks.AIR) || state.getMaterial().isReplaceable());
    }
}
