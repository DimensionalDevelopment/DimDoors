package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.util.Point4D;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRiftSignature extends Item {
    public static final String ID = "itemRiftSignature";

	public ItemRiftSignature() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		hasSubtypes = true;
		setCreativeTab(DimDoors.dimDoorsCreativeTab);
		setUnlocalizedName(ID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		//Make the item glow if it has one endpoint stored
		return (stack.getItemDamage() != 0);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
		    EnumFacing side, float hitX, float hitY, float hitZ) {
		// We must use onItemUseFirst() instead of onItemUse() because Minecraft checks
		// whether the user is in creative mode after calling onItemUse() and undoes any
		// damage we might set to indicate the rift sig has been activated. Otherwise,
		// we would need to rely on checking NBT tags for hasEffect() and that function
		// gets called constantly. Avoiding NBT lookups reduces our performance impact.

		// Return false on the client side to pass this request to the server
		if (world.isRemote) return false;

		pos = adjustYForSpecialBlocks(world, pos);

		if (!player.canPlayerEdit(pos, side, stack)) return true;

		Point4DOrientation source = getSource(stack);

		EnumFacing orientation = EnumFacing.fromAngle(player.rotationYaw);
		if (source != null) {
			// The link was used before and already has an endpoint stored.
			// Create links connecting the two endpoints.
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.getDimensionData(world);

			DimLink link = sourceDimension.createLink(source.getPoint().toBlockPos(), LinkType.NORMAL,
                    source.getOrientation());
			DimLink reverse = destinationDimension.createLink(pos, LinkType.NORMAL, orientation);

			destinationDimension.setLinkDestination(link, pos);
			sourceDimension.setLinkDestination(reverse, source.getPoint().toBlockPos());

			// Try placing a rift at the destination point
			DimDoors.blockRift.tryPlacingRift(world, pos);

			// Try placing a rift at the source point
			// We don't need to check if sourceWorld is null - that's already handled.
			World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
			DimDoors.blockRift.tryPlacingRift(sourceWorld, source.getPoint().toBlockPos());

			if (!player.capabilities.isCreativeMode) stack.stackSize--;
			clearSource(stack);
			DimDoors.sendChat(player, "Rift Created");
			world.playSoundAtEntity(player, DimDoors.MODID + ":riftEnd", 0.6f, 1);
		} else {
			//The link signature has not been used. Store its current target as the first location. 
			setSource(stack, pos.getX(), pos.getY(), pos.getZ(), orientation, PocketManager.createDimensionData(world));
			DimDoors.sendChat(player,("Location Stored in Rift Signature"));
			world.playSoundAtEntity(player, DimDoors.MODID + ":riftStart", 0.6f, 1);
		}
		return true;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		Point4DOrientation source = getSource(stack);
		if (source != null)
			tooltip.add(StatCollector.translateToLocalFormatted("info.riftSignature.bound",
                    source.getX(), source.getY(), source.getZ(), source.getDimension()));
		else DimDoors.translateAndAdd("info.riftSignature.unbound", tooltip);
	}

	/**
	 * Makes the rift placement account for replaceable blocks and doors.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return the adjusted y coord
	 */
	public static BlockPos adjustYForSpecialBlocks(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == null) return pos.up(2);
		if (block.isReplaceable(world, pos))
            // Move block placement so its directly over things like snow
            return pos.up();
		if (block instanceof BaseDimDoor) {
			if (((BaseDimDoor)block).isUpperDoorBlock(world.getBlockState(pos))) return pos;
			else return pos.up(); //Place rift on the correct place on the door
		}
		return pos.up(2);
	}
	
	public static void setSource(ItemStack itemStack, int x, int y, int z,
            EnumFacing orientation, NewDimData dimension) {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("linkX", x);
		tag.setInteger("linkY", y);
		tag.setInteger("linkZ", z);
		tag.setInteger("orientation", orientation.getIndex());
		tag.setInteger("linkDimID", dimension.id());

		itemStack.setTagCompound(tag);
		itemStack.setItemDamage(1);
	}

	public static void clearSource(ItemStack itemStack) {
		//Don't just set the tag to null since there may be other data there (e.g. for renamed items)
		NBTTagCompound tag = itemStack.getTagCompound();
		tag.removeTag("linkX");
		tag.removeTag("linkY");
		tag.removeTag("linkZ");
		tag.removeTag("orientation");
		tag.removeTag("linkDimID");
		itemStack.setItemDamage(0);
	}

	public static Point4DOrientation getSource(ItemStack itemStack) {
		if (itemStack.getItemDamage() != 0) {
			if (itemStack.hasTagCompound()) {
				NBTTagCompound tag = itemStack.getTagCompound();

				Integer x = tag.getInteger("linkX");
				Integer y = tag.getInteger("linkY");
				Integer z = tag.getInteger("linkZ");
				EnumFacing orientation = EnumFacing.VALUES[tag.getInteger("orientation")];
				Integer dimID = tag.getInteger("linkDimID");

                return new Point4DOrientation(x, y, z, orientation, dimID);
			}
			// Mark the item as uninitialized if its source couldn't be read
			itemStack.setItemDamage(0);
		}
		return null;
	}
	
	static class Point4DOrientation {
		private Point4D point;
		private EnumFacing orientation;
		
		Point4DOrientation(int x, int y, int z, EnumFacing orientation, int dimID) {
			this.point = new Point4D(new BlockPos(x, y, z), dimID);
			this.orientation = orientation;
		}
		
		int getX() {return point.getX();}
		
		int getY() {return point.getY();}
		
		int getZ() {return point.getZ();}
		
		int getDimension() {return point.getDimension();}
		
		EnumFacing getOrientation() {return orientation;}
		
		Point4D getPoint() {return point;}
	}
}

