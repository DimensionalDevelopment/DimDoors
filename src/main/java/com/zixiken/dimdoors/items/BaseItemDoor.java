package com.zixiken.dimdoors.items;

import java.util.HashMap;
import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

public abstract class BaseItemDoor extends ItemDoor {
	// Maps non-dimensional door items to their corresponding dimensional door item
	// Also maps dimensional door items to themselves for simplicity
	private static HashMap<ItemDoor, BaseItemDoor> doorItemMapping = new HashMap<ItemDoor, BaseItemDoor>();
	private static DDProperties properties;

	/**
	 * door represents the non-dimensional door this item is associated with. Leave null for none.
	 * @param material
	 * @param vanillaDoor
	 */
	public BaseItemDoor(Block block, ItemDoor vanillaDoor) {
		super(block);
		this.setMaxStackSize(64);
		this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
		if (properties == null)
			properties = DDProperties.instance();
		
		doorItemMapping.put(this, this);
		if (vanillaDoor != null)
			doorItemMapping.put(vanillaDoor, this);
	}

	@Override
	public abstract void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced);

	/**
	 * Overriden in subclasses to specify which door block that door item will
	 * place
	 * 
	 * @return
	 */
	protected abstract BaseDimDoor getDoorBlock();

    /**
	 * Overriden here to remove vanilla block placement functionality from
	 * dimensional doors, we handle this in the EventHookContainer
	 */
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
                             EnumFacing side, float hitX, float hitY, float hitZ) {return false;}

	/**
	 * Tries to place a door as a dimensional door
	 * 
	 * @param stack
	 * @param player
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return
	 */
	public static boolean tryToPlaceDoor(ItemStack stack, EntityPlayer player, World world,
                                         BlockPos pos, EnumFacing side) {
		if (world.isRemote) return false;

		// Retrieve the actual door type that we want to use here.
		// It's okay if stack isn't an ItemDoor. In that case, the lookup will
		// return null, just as if the item was an unrecognized door type.
		BaseItemDoor mappedItem = doorItemMapping.get(stack.getItem());
		if (mappedItem == null) return false;

		BaseDimDoor doorBlock = mappedItem.getDoorBlock();
		if (BaseItemDoor.placeDoorOnBlock(doorBlock, stack, player, world, pos, side)) return true;
		return BaseItemDoor.placeDoorOnRift(doorBlock, world, player, stack);
	}

	/**
	 * try to place a door block on a block
	 * @param doorBlock
	 * @param stack
	 * @param player
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return
	 */
	public static boolean placeDoorOnBlock(Block doorBlock, ItemStack stack, EntityPlayer player,
                                           World world, BlockPos pos, EnumFacing side) {
		if (world.isRemote) return false;

		// Only place doors on top of blocks - check if we're targeting the top
		// side
		if (side == EnumFacing.UP) {
			Block block = world.getBlockState(pos).getBlock();
			if (!block.isAir(world, pos) && !block.isReplaceable(world, pos)) pos = pos.up();

            BlockPos upPos = pos.up();
			if (canPlace(world, pos) && canPlace(world, upPos) && player.canPlayerEdit(pos, side, stack)
					&& (player.canPlayerEdit(upPos, side, stack) && stack.stackSize > 0)
					&& ((stack.getItem() instanceof BaseItemDoor) || PocketManager.getLink(upPos, world) != null)) {
				placeDoorBlock(world, pos, EnumFacing.fromAngle(player.rotationYaw), doorBlock);
				if (!player.capabilities.isCreativeMode) stack.stackSize--;
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
		if (world.isRemote) return false;

		MovingObjectPosition hit = BaseItemDoor.doRayTrace(world, player, true);
		if (hit != null) {
            BlockPos pos = hit.getBlockPos();
			if (world.getBlockState(pos).getBlock() == DimDoors.blockRift) {
				DimLink link = PocketManager.getLink(pos, world.provider.getDimensionId());
				if (link != null) {
                    BlockPos downPos = pos.down();
					if (player.canPlayerEdit(pos, hit.sideHit, stack) &&
                            player.canPlayerEdit(downPos, hit.sideHit, stack) &&
                            canPlace(world, pos) && canPlace(world, downPos)) {
                        placeDoorBlock(world, downPos, EnumFacing.fromAngle(player.rotationYaw), doorBlock);
                        if (!(stack.getItem() instanceof BaseItemDoor))
                            ((TileEntityDimDoor) world.getTileEntity(pos)).hasGennedPair = true;
                        if (!player.capabilities.isCreativeMode) stack.stackSize--;
                        return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean canPlace(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		return (block == DimDoors.blockRift || block.isAir(world, pos) || block.getMaterial().isReplaceable());
	}

	/**
	 * Copied from minecraft Item.class
	 * TODO we probably can improve this
	 * 
	 * @param par1World
	 * @param par2EntityPlayer
	 * @param par3
	 * @return
	 */
    protected static MovingObjectPosition doRayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (playerIn instanceof EntityPlayerMP)
            d3 = ((EntityPlayerMP)playerIn).theItemInWorldManager.getBlockReachDistance();
        Vec3 vec31 = vec3.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
    }
}