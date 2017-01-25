package com.zixiken.dimdoors.shared.items;

import java.util.HashMap;
import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import static net.minecraft.item.ItemDoor.placeDoor;
import net.minecraft.tileentity.TileEntity;

public abstract class ItemDoorBase extends ItemDoor {
    // Maps non-dimensional door items to their corresponding dimensional door item
    // Also maps dimensional door items to themselves for simplicity

    private static HashMap<ItemDoor, ItemDoorBase> doorItemMapping = new HashMap<ItemDoor, ItemDoorBase>();

    /**
     * door represents the non-dimensional door this item is associated with.
     * Leave null for none.
     *
     * @param vanillaDoor
     */
    public ItemDoorBase(Block block, ItemDoor vanillaDoor) {
        super(block);
        this.setMaxStackSize(64);
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);

        doorItemMapping.put(this, this); //@todo Why?
        if (vanillaDoor != null) {
            doorItemMapping.put(vanillaDoor, this);
        }
    }

    @Override
    public abstract void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced);

    /**
     * Overridden in subclasses to specify which door block that door item will
     * place
     *
     * @return
     */
    protected abstract BlockDimDoorBase getDoorBlock();

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        }
        RayTraceResult hit = rayTrace(worldIn, playerIn, true);
        if (RayTraceHelper.isRift(hit, worldIn)) {
            EnumActionResult canDoorBePlacedOnGroundBelowRift
                    = tryPlaceDoorOnTopOfBlock(stack, playerIn, worldIn, hit.getBlockPos().down(2), hand,
                            (float) hit.hitVec.xCoord, (float) hit.hitVec.yCoord, (float) hit.hitVec.zCoord); //stack may be changed by this method
            return new ActionResult(canDoorBePlacedOnGroundBelowRift, stack);
        }
        return new ActionResult(EnumActionResult.FAIL, stack); //@todo, should return onItemUse(params) here? will door placement on block not work otherwise?
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.FAIL; //@todo, is this needed, or does this always get called from the onItemRightClick(params) method?
        }

        Block block = world.getBlockState(pos).getBlock();
        if (block.isReplaceable(world, pos) && block != Blocks.AIR) { //why are we even checking for Blocks.AIR here? How would one use an item on an air block. It has no hitbox, does it?
            pos = pos.offset(EnumFacing.DOWN); //the bottom part of the door can replace this block, so we will try to place it on the block under it
            block = world.getBlockState(pos).getBlock(); //update the block to be placed on
            side = EnumFacing.UP; //make sure that the next if-statement returns true
        }

        if (side != EnumFacing.UP || block == Blocks.AIR) { //only place the door if the item is "used" on the top of the block it is to be placed on and we might as well check if that block is air or not even though I do not think it is needed (isSideSolid gets checked later)
            return EnumActionResult.FAIL;
        } else {
            return tryPlaceDoorOnTopOfBlock(stack, playerIn, world, pos, hand, hitX, hitY, hitZ);
        }
    }
//pos = position of block, the door gets placed on

    static EnumActionResult tryPlaceDoorOnTopOfBlock(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, float hitX, float hitY, float hitZ) {
        // Retrieve the actual door type that we want to use here.
        // It's okay if stack isn't an ItemDoor. In that case, the lookup will
        // return null, just as if the item was an unrecognized door type.
        ItemDoorBase mappedItem = doorItemMapping.get(stack.getItem());
        if (mappedItem == null) {
            return EnumActionResult.FAIL;
        }
        pos = pos.up(); //change pos to position the bottom half of the door gets placed at
        BlockDimDoorBase doorBlock = mappedItem.getDoorBlock();
        if (playerIn.canPlayerEdit(pos, EnumFacing.UP, stack) && playerIn.canPlayerEdit(pos.up(), EnumFacing.UP, stack)
                && doorBlock.canPlaceBlockAt(world, pos)) {
            //calculate what side the door should be facing
            EnumFacing enumfacing = EnumFacing.fromAngle((double) playerIn.rotationYaw);
            int i = enumfacing.getFrontOffsetX();
            int j = enumfacing.getFrontOffsetZ();
            boolean flag = i < 0 && hitZ < 0.5F || i > 0 && hitZ > 0.5F || j < 0 && hitX > 0.5F || j > 0 && hitX < 0.5F; //Vanilla Minecraft code not consistently using EnumFacing
            //fetch "the" tile entity at the top block of where the door is going to be placed
            TileEntity possibleOldRift = world.getTileEntity(pos.up());
            //place the door
            placeDoor(world, pos, enumfacing, doorBlock, flag);
            SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, playerIn);
            world.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            --stack.stackSize;

            //fetch the TileEntityDimDoor at the top block of where the door has just been placed
            TileEntityDimDoor newTileEntityDimDoor = (TileEntityDimDoor) world.getTileEntity(pos.up());
            //set the tile-entity's initial data
            newTileEntityDimDoor.uponDoorPlacement(possibleOldRift);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
