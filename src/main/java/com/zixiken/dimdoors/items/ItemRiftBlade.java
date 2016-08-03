package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRiftBlade extends ItemSword {
	public static final String ID = "itemRiftBlade";

	public ItemRiftBlade() {
		super(ToolMaterial.EMERALD);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {return true;}

	private boolean teleportToEntity(ItemStack item, Entity entity, EntityPlayer player) {
		Vec3 vec1 = new Vec3(player.posX-entity.posX,
                player.getEntityBoundingBox().minY+player.height / 2.0F-entity.posY+entity.getEyeHeight(),
                player.posZ-entity.posZ);

		double coef = (vec1.lengthVector()-2.5) / vec1.lengthVector();
        Vec3 vec2 = new Vec3(vec1.xCoord*coef, vec1.yCoord*coef, vec1.zCoord*coef);

		double x = player.posX - vec2.xCoord;
		double y = player.posY - vec2.yCoord;
        double z = player.posZ - vec2.zCoord;

        BlockPos pos = new BlockPos(MathHelper.floor_double(x),
                MathHelper.floor_double(y),
                MathHelper.floor_double(z));
        while(!player.worldObj.isAirBlock(pos)) {pos = pos.up();}

        y = pos.getY();
		player.setPositionAndUpdate(x, y, z);
		player.playSound("mob.endermen.portal", 1.0F, 1.0F);
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 1.0F, 1.0F);
		
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			List<EntityLiving> list =  world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(
                    player.posX-10, player.posY-10, player.posZ-10, player.posX+10, player.posY+10, player.posZ+10));

			for (EntityLiving ent : list) {
				Vec3 vec1 = player.getLook(1.0F).normalize();
				Vec3 vec2 =  new Vec3(ent.posX-player.posX,
                        ent.getEntityBoundingBox().minY+(ent.height) / 2.0F-(player.posY+player.getEyeHeight()),
                        ent.posZ-player.posZ);
				double length = vec2.lengthVector();
				vec2 = vec2.normalize();
				double dotProduct = vec1.dotProduct(vec2);
				if((dotProduct+0.1) > 1.0D - 0.025D/length && player.canEntityBeSeen(ent)) {
					teleportToEntity(stack, ent, player);
					stack.damageItem(3, player);
					return stack;
				}
			}

			MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (hit != null) {
                BlockPos up = hit.getBlockPos(), down = up.down();
				if (world.getBlockState(up).getBlock() == DimDoors.blockRift &&
                        PocketManager.getLink(up, world) != null &&
                        player.canPlayerEdit(up, hit.sideHit, stack) &&
                        player.canPlayerEdit(down, hit.sideHit, stack) &&
                        BaseItemDoor.canPlace(world, up) &&
                        BaseItemDoor.canPlace(world, down)) {
                    ItemDimensionalDoor.placeDoor(world, down, EnumFacing.fromAngle(player.rotationYaw), DimDoors.transientDoor);
                    player.worldObj.playSoundAtEntity(player, DimDoors.MODID + ":riftDoor", 0.6f, 1);
                    stack.damageItem(3, player);
                    return stack;
                }
            }
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}
		return stack;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		//Don't include a call to super.getIsRepairable()!
    	//That would cause this sword to accept diamonds as a repair material (since we set material = Diamond).
		return DimDoors.itemStableFabric == repair.getItem();
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        DimDoors.translateAndAdd("info.riftblade", tooltip);
	}
}
