package com.zixiken.dimdoors.items;

import java.util.HashMap;
import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
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

        doorItemMapping.put(this, this);
        if (vanillaDoor != null) {
            doorItemMapping.put(vanillaDoor, this);
        }
    }

    @Override
    public abstract void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced);

    /**
     * Overriden in subclasses to specify which door block that door item will
     * place
     *
     * @return
     */
    protected abstract BlockDimDoorBase getDoorBlock();

    /**
     * Overriden here to remove vanilla block placement functionality from
     * dimensional doors, we handle this in the EventHookContainer
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return EnumActionResult.FAIL;
    }

    /**
     * Tries to place a door as a dimensional door
     *
     * @param stack
     * @param player
     * @param world
     * @param side
     * @return
     */
    public static boolean tryToPlaceDoor(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        if (world.isRemote) {
            return false;
        }

        // Retrieve the actual door type that we want to use here.
        // It's okay if stack isn't an ItemDoor. In that case, the lookup will
        // return null, just as if the item was an unrecognized door type.
        ItemDoorBase mappedItem = doorItemMapping.get(stack.getItem());
        if (mappedItem == null) {
            return false;
        }
        BlockDimDoorBase doorBlock = mappedItem.getDoorBlock();
        return ItemDoorBase.placeDoorOnBlock(doorBlock, stack, player, world, pos, side)
                || ItemDoorBase.placeDoorOnRift(doorBlock, world, player, stack);
    }

    /**
     * try to place a door block on a block
     *
     * @param doorBlock
     * @param stack
     * @param player
     * @param world
     * @param pos
     * @param side
     * @return
     */
    public static boolean placeDoorOnBlock(Block doorBlock, ItemStack stack, EntityPlayer player,
            World world, BlockPos pos, EnumFacing side) {
        if (world.isRemote) {
            return false;
        }

        // Only place doors on top of blocks - check if we're targeting the top
        // side
        if (side == EnumFacing.UP) {
            Block block = world.getBlockState(pos).getBlock();
            if (!world.getBlockState(pos).equals(Blocks.AIR) && !block.isReplaceable(world, pos)) {
                pos = pos.up();
            }

            BlockPos upPos = pos.up();
            if (canPlace(world, pos) && canPlace(world, upPos) && player.canPlayerEdit(pos, side, stack)
                    && player.canPlayerEdit(upPos, side, stack) && stack.stackSize > 0
                    && stack.getItem() instanceof ItemDoorBase && world.getBlockState(pos.down()).isSideSolid(world, pos, side)) {
                placeDoor(world, pos, EnumFacing.fromAngle(player.rotationYaw), doorBlock, true);
                TileEntity tileEntity = world.getTileEntity(pos.up());
                if (tileEntity instanceof DDTileEntityBase) {
                    DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
                    rift.register();
                }
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * uses a raytrace to try and place a door on a rift
     *
     * @param doorBlock
     * @param world
     * @param player
     * @param stack
     * @return
     */
    public static boolean placeDoorOnRift(Block doorBlock, World world, EntityPlayer player, ItemStack stack) {
        if (world.isRemote) {
            return false;
        }

        RayTraceResult hit = ItemDoorBase.doRayTrace(world, player, true);
        if (hit != null) {
            BlockPos pos = hit.getBlockPos();
            if (world.getBlockState(pos).getBlock() == ModBlocks.blockRift) {
                BlockPos downPos = pos.down();
                if (player.canPlayerEdit(pos, hit.sideHit, stack)
                        && player.canPlayerEdit(downPos, hit.sideHit, stack)
                        && canPlace(world, pos) && canPlace(world, downPos)) {
                    DDTileEntityBase riftOrig = (DDTileEntityBase) world.getTileEntity(pos);
                    placeDoor(world, downPos, EnumFacing.fromAngle(player.rotationYaw), doorBlock, true);
                    TileEntityDimDoor newRift = (TileEntityDimDoor) world.getTileEntity(pos);
                    if (!(stack.getItem() instanceof ItemDoorBase)) { //@todo why does THIS if statement mean that THAT field should be true?
                        newRift.hasGennedPair = true;
                    }
                    newRift.loadDataFrom(riftOrig); //take over the data from the original Rift
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canPlace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);

        return (state.getBlock() == ModBlocks.blockRift || state.equals(Blocks.AIR) || state.getMaterial().isReplaceable());
    }

    /**
     * Copied from minecraft Item.class TODO we probably can improve this
     *
     * @param world
     * @param player
     * @param useLiquids
     * @return
     */
    protected static RayTraceResult doRayTrace(World world, EntityPlayer player, boolean useLiquids) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        double d0 = player.posX;
        double d1 = player.posY + (double) player.getEyeHeight();
        double d2 = player.posZ;
        Vec3d vec3 = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (player instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
        }
        Vec3d vec31 = vec3.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        return world.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }

    public void translateAndAdd(String key, List<String> list) {
        for (int i = 0; i < 10; i++) {
            /*if(StatCollector.canTranslate(key+Integer.toString(i))) {
				String line = StatCollector.translateToLocal(key + Integer.toString(i));
				list.add(line);
			} else */ break;
        }
    }
}
