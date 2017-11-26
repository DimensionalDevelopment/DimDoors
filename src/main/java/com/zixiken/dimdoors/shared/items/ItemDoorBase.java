package com.zixiken.dimdoors.shared.items;

import java.util.HashMap;
import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
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
    public abstract void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced);

    /**
     * Overridden in subclasses to specify which door block that door item will
     * place
     *
     * @return
     */
    protected abstract BlockDimDoorBase getDoorBlock();

    //onItemUse gets fired before onItemRightClick and if it returns "success", onItemRightClick gets skipped.
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        RayTraceResult hit = rayTrace(world, player, true);
        if (RayTraceHelper.isRift(hit, world)) {
            EnumActionResult canDoorBePlacedOnGroundBelowRift
                    = tryPlaceDoorOnTopOfBlock(stack, player, world, hit.getBlockPos().down(2), hand,
                            (float) hit.hitVec.x, (float) hit.hitVec.y, (float) hit.hitVec.z); //stack may be changed by this method
            return new ActionResult<>(canDoorBePlacedOnGroundBelowRift, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack); //@todo, should return onItemUse(params) here? will door placement on block not work otherwise?

        //@todo personal and chaos doors can be placed on top of a rift? Should not be possible
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        
        Block block = world.getBlockState(pos).getBlock();
        if (!block.isReplaceable(world, pos)) {
            if (side != EnumFacing.UP) {
                return EnumActionResult.FAIL;
            }
        } else {
            pos = pos.offset(EnumFacing.DOWN); //the bottom part of the door can replace this block, so we will try to place it on the block under it
        }

        return tryPlaceDoorOnTopOfBlock(player.getHeldItem(hand), player, world, pos, hand, hitX, hitY, hitZ);
    }
//pos = position of block, the door gets placed on

    static EnumActionResult tryPlaceDoorOnTopOfBlock(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, float hitX, float hitY, float hitZ) {
        // Retrieve the actual door type that we want to use here.
        // It's okay if stack isn't an ItemDoor. In that case, the lookup will
        // return null, just as if the item was an unrecognized door type.
        ItemDoorBase mappedItem = doorItemMapping.get(stack.getItem());
        if (mappedItem == null) {
            DimDoors.warn(ItemDoorBase.class, "Item " + stack.getItem().toString() + " does not seem to have a valid mapped ItemDoor.");
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
            if (possibleOldRift != null && possibleOldRift instanceof TileEntityRift) {
                TileEntityRift oldRift = (TileEntityRift) possibleOldRift;
                oldRift.placingDoorOnRift = true;
            }
            //place the door
            placeDoor(world, pos, enumfacing, doorBlock, flag);
            SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, playerIn);
            world.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            if (!playerIn.isCreative()) {
                stack.setCount(stack.getCount()-1);
            }

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
