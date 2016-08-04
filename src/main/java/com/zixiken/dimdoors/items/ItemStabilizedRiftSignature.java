package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.NewDimData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStabilizedRiftSignature extends ItemRiftSignature {
	public static final String ID = "itemStableRiftSignature";

	public ItemStabilizedRiftSignature() {
		super();
        setUnlocalizedName(ID);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
		    EnumFacing side, float hitX, float hitY, float hitZ) {
		// Return false on the client side to pass this request to the server
		if (world.isRemote) return false;

		// Adjust Y so the rift is at head level, depending on the presence of certain blocks
		pos = adjustYForSpecialBlocks(world, pos);
		if (!player.canPlayerEdit(pos, side, stack)) return true;

        EnumFacing orientation = EnumFacing.fromAngle(player.rotationYaw);
		// Check if the Stabilized Rift Signature has been initialized
		Point4DOrientation source = getSource(stack);
		if (source != null) {
			// Yes, it's initialized.
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.createDimensionData(world);
			DimLink link = sourceDimension.createLink(source.getPoint().toBlockPos(), LinkType.NORMAL,
                    source.getOrientation());
            DimLink reverse = destinationDimension.getLink(pos);
			
			// Check whether the SRS is being used to restore one of its previous
			// link pairs. In other words, the SRS is being used on a location
			// that already has a link pointing to the SRS's source, with the
			// intention of overwriting the source-side link to point there.
			// Those benign redirection operations will be handled for free.
			
			if (reverse != null && source.getPoint().equals(reverse.destination()))
				// Only the source-to-destination link is needed.
				destinationDimension.setLinkDestination(link, pos);
			else {
				// Check if the player is in creative mode,
				// or if the player can pay with an Ender Pearl to create a rift.
				if (!player.capabilities.isCreativeMode && !player.inventory.consumeInventoryItem(Items.ender_pearl)) {
					DimDoors.sendChat(player, "You don't have any Ender Pearls!");
					// I won't do this, but this is the chance to localize chat 
					// messages sent to the player; look at ChatMessageComponent 
					// and how MFR does it with items like the safari net launcher
					return true;
				}
	
				// Create links connecting the two endpoints.
				reverse = destinationDimension.createLink(pos, LinkType.NORMAL, orientation);
				destinationDimension.setLinkDestination(link, pos);
				sourceDimension.setLinkDestination(reverse, source.getPoint().toBlockPos());
	
				// Try placing a rift at the destination point
				DimDoors.blockRift.tryPlacingRift(world, pos);
			}
			
			// Try placing a rift at the source point
			// We don't need to check if sourceWorld is null - that's already handled.
			World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
			
			DimDoors.blockRift.tryPlacingRift(sourceWorld, source.getPoint().toBlockPos());
			DimDoors.sendChat(player, "Rift Created");
			world.playSoundAtEntity(player, "mods.DimDoors.sfx.riftEnd", 0.6f, 1);
		} else {
			// The link signature has not been used. Store its current target as the first location. 
			setSource(stack, pos, orientation, PocketManager.createDimensionData(world));
			DimDoors.sendChat(player, "Location Stored in Stabilized Rift Signature");
			world.playSoundAtEntity(player, "mods.DimDoors.sfx.riftStart", 0.6f, 1);
		}
		return true;
	}

	public static boolean useFromDispenser(ItemStack stack, World world, BlockPos pos) {
		// Stabilized Rift Signatures can only be used from dispensers to restore
		// a previous link pair. The operation would be free for a player, so
		// dispensers can also perform it for free. Otherwise, the item does nothing.
		if (world.isRemote) return false;
		
		// Adjust Y so the rift is at head level, depending on the presence of certain blocks
		pos = adjustYForSpecialBlocks(world, pos);
		Point4DOrientation source = getSource(stack);
		
		// The SRS must have been initialized
		if (source != null) {
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.createDimensionData(world);
			DimLink reverse = destinationDimension.getLink(pos);
			DimLink link;
			
			// Check whether the SRS is being used to restore one of its previous
			// link pairs. In other words, the SRS is being used on a location
			// that already has a link pointing to the SRS's source, with the
			// intention of overwriting the source-side link to point there.
			if (reverse != null && source.getPoint().equals(reverse.destination())) {
				// Only the source-to-destination link is needed.
				link = sourceDimension.createLink(source.getPoint().toBlockPos(), LinkType.NORMAL,
                        source.getOrientation());
				destinationDimension.setLinkDestination(link, pos);
				
				// Try placing a rift at the source point
				// We don't need to check if sourceWorld is null - that's already handled.
				World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
				DimDoors.blockRift.tryPlacingRift(sourceWorld, source.getPoint().toBlockPos());
				
				// This call doesn't seem to be working...
				world.playSoundEffect(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,
                        "mods.DimDoors.sfx.riftEnd", 0.6f, 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		Point4DOrientation source = getSource(stack);
		if (source != null) {
            tooltip.add(StatCollector.translateToLocalFormatted("info.riftSignature.bound",
                    source.getX(), source.getY(), source.getZ(), source.getDimension()));
		} else DimDoors.translateAndAdd("info.riftSignature.stable", tooltip);
	}
}
