package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
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
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        IBlockState down = worldIn.getBlockState(pos.down());
        if (!worldIn.isRemote && down.getBlock() == this) { //should only teleport when colliding with top part of the door to prevent double teleportation from being triggered
            if (down.getValue(BlockDoor.OPEN)
                    && entityIn instanceof EntityPlayer //@todo remove this so any entity can go through?
                    && entityIn.timeUntilPortal < 1 //to prevent the player from teleporting all over the place we have a 50-tick cooldown
                    && isEntityFacingDoor(down, (EntityLivingBase) entityIn)) {
                toggleDoor(worldIn, pos, false);
                enterDimDoor(worldIn, pos, entityIn);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!checkCanOpen(worldIn, pos, playerIn)) {
            return false;
        }
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            pos = pos.down();
            state = worldIn.getBlockState(pos);
        }

        if (state.getBlock() != this) {
            return false;
        } else {
            state = state.cycleProperty(BlockDoor.OPEN);
            worldIn.setBlockState(pos, state, 2);
            worldIn.markBlockRangeForRenderUpdate(pos, pos.up());
            worldIn.playEvent(playerIn, state.getValue(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);
            return true;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;
    }

    //Called to update the render information on the tile entity. Could probably implement a data watcher,
    //but this works fine and is more versatile I think. 
    public void updateAttachedTile(World world, BlockPos pos) {
        DimDoors.proxy.updateDoorTE(this, world, pos);
    }

    @Override
    public boolean isDoorOnRift(World world, BlockPos pos) { //@todo what does this even mean?
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        updateAttachedTile(worldIn, pos);
    }

    /**
     * only called by clickMiddleMouseButton , and passed to
     * inventory.setCurrentItem (along with isCreative)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(getItemDoor(), 1, 0);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return isUpperDoorBlock(state) ? null : getItemDoor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(getItemDoor(), 1, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { //gets called upon world load as well
        return new TileEntityVerticalEntranceRift();
    }

    @Override
    public void enterDimDoor(World world, BlockPos pos, Entity entity) {
        TileEntityRift riftTile = getRiftTile(world, pos, world.getBlockState(pos));
        riftTile.isTeleporting = true; //flick trigger switch
        riftTile.teleportingEntity = entity;
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
        return state.getValue(BlockDoor.FACING) == EnumFacing.fromAngle(entity.rotationYaw);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityRift origRift = null;
        boolean isTopHalf = state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;
        boolean shouldPlaceRift = false;
        if (isTopHalf) {
            origRift = (TileEntityRift) worldIn.getTileEntity(pos);
            //if (origRift.isPaired()) {
            //    shouldPlaceRift = true;
            //    RiftRegistry.INSTANCE.setLastChangedRift(origRift); // TODO
            //} else {
            //    RiftRegistry.INSTANCE.unregisterRift(origRift.getRiftID());
            //}
        }
        super.breakBlock(worldIn, pos, state);
        if (shouldPlaceRift) {
            worldIn.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift newRift = (TileEntityRift) worldIn.getTileEntity(pos);
            newRift.copyFrom(origRift); //@todo this does not work here, or does it?
        }
    }

    //returns the TileEntityRift that is the tile entity belonging to the door block "state" at this "pos" in the "world"
    public TileEntityRift getRiftTile(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity;
        if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
            tileEntity = world.getTileEntity(pos.up());
        } else {
            tileEntity = world.getTileEntity(pos);
        }
        return (TileEntityRift) tileEntity;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) { //returns whether or not the entire door (2 blocks tall) can be placed at this pos. pos is the position of the bottom half of the door
        IBlockState groundState = worldIn.getBlockState(pos.down());
        IBlockState bottomState = worldIn.getBlockState(pos);
        IBlockState topState = worldIn.getBlockState(pos.up());
        return !(pos.up().getY() > worldIn.getHeight() - 1 || pos.getY() < 1) //top half can never be placed above buildHeight (255), (worldIn.getHeight() should return 256) and bottom half can never be placed below y=1
                && groundState.isSideSolid(worldIn, pos.down(), EnumFacing.UP)
                && canPlaceBottomAt(bottomState) && canPlaceTopAt(topState);
    }

    private boolean canPlaceBottomAt(IBlockState state) {
        return state.equals(Blocks.AIR) || state.getMaterial().isReplaceable();
    }

    private boolean canPlaceTopAt(IBlockState state) {
        return state.getBlock() == ModBlocks.RIFT || state.equals(Blocks.AIR) || state.getMaterial().isReplaceable();
    }
    
    protected int getCloseSound() {
        return blockMaterial == Material.IRON ? 1011 : 1012;
    }

    protected int getOpenSound() {
        return blockMaterial == Material.IRON ? 1005 : 1006;
    }
}
