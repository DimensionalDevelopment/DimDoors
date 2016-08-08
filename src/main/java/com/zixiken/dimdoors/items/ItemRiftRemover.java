package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.DimData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRiftRemover extends Item {
	public static final String ID = "itemRiftRemover";

	public ItemRiftRemover() {
		super();
		setMaxStackSize(1);
		setCreativeTab(DimDoors.dimDoorsCreativeTab);
		setMaxDamage(4);
        setUnlocalizedName(ID);
	}

/*	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		// We invoke PlayerControllerMP.onPlayerRightClick() from here so that Minecraft
		// will invoke onItemUseFirst() on the client side. We'll tell it to pass the
		// request to the server, which will make sure that rift-related changes are
		// reflected on the server.

		if (!world.isRemote) return stack;

		MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, true);
		if (hit != null) {
			BlockPos pos = hit.getBlockPos();
			DimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (world.getBlockState(pos).getBlock() == DimDoors.blockRift && link != null &&
					player.canPlayerEdit(pos, hit.sideHit, stack)) {
				// Invoke onPlayerRightClick()
				FMLClientHandler.instance().getClient().playerController.onPlayerRightClick(
                        (EntityPlayerSP)player, (WorldClient)world, stack, pos, hit.sideHit, hit.hitVec);
			}
		}
		return stack;
	}*/

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
            EnumFacing side, float hitX, float hitY, float hitZ) {
		// We want to use onItemUseFirst() here so that this code will run on the server side,
		// so we don't need the client to send link-related updates to the server.

		// On integrated servers, the link won't be removed immediately because of the rift
		// removal animation. That means we'll have a chance to check for the link before
		// it's deleted. Otherwise the Rift Remover's durability wouldn't drop.

        MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (hit == null) return false;
        pos = hit.getBlockPos();
        DimData dimension = PocketManager.createDimensionData(world);
        DimLink link = dimension.getLink(pos);
        if (world.getBlockState(pos).getBlock() != DimDoors.blockRift || link == null ||
                !player.canPlayerEdit(pos, hit.sideHit, stack)) return false;

        // Tell the rift's tile entity to do its removal animation
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityRift) {
            ((TileEntityRift) tileEntity).shouldClose = true;
            tileEntity.markDirty();
        }

        if (world.isRemote)
            // Tell the server about this
            return false;
        else {
            // Only set the block to air on the server side so that we don't
            // tell the server to remove the rift block before it can use the
            // Rift Remover. Otherwise, it won't know to reduce durability.
            world.setBlockToAir(pos);
            if (!player.capabilities.isCreativeMode)
                stack.damageItem(1, player);
            player.worldObj.playSoundAtEntity(player, DimDoors.MODID + ":riftClose", 0.8f, 1);
            return true;
        }
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		DimDoors.translateAndAdd("info.riftRemover",tooltip);
	}
}
