package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRiftBlade extends ItemSword
{
	private final DDProperties properties;

	public ItemRiftBlade(int itemID, EnumToolMaterial material, DDProperties properties)
	{
		super(itemID, material);

		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxStackSize(1);
		this.setMaxDamage(500);
		this.hasSubtypes = false;
		this.properties = properties;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
	{
		if (par2Block.blockID == Block.web.blockID)
		{
			return 15.0F;
		}
		else
		{
			Material material = par2Block.blockMaterial;
			return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.pumpkin ? 1.0F : 1.5F;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		return true;
	}

	@Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
	{
		par1ItemStack.damageItem(1, par3EntityLiving);
		return true;
	}

	@Override
	public int getDamageVsEntity(Entity par1Entity)
	{
		return 7;
	}

	@Override
	public MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
	{
		float var4 = 1.0F;
		float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
		float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
		double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)var4;
		double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par2EntityPlayer.yOffset;
		double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)var4;
		Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 5.0D;
		if (par2EntityPlayer instanceof EntityPlayerMP)
		{
			var21 = 7;
		}
		Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
		return par1World.rayTraceBlocks_do_do(var13, var23, true, false);
	}

	private boolean teleportToEntity(ItemStack item, Entity par1Entity, EntityPlayer holder)
	{
		Vec3 var2 = holder.worldObj.getWorldVec3Pool().getVecFromPool(holder.posX - par1Entity.posX, holder.boundingBox.minY + (double)(holder.height / 2.0F) - par1Entity.posY + (double)par1Entity.getEyeHeight(), holder.posZ - par1Entity.posZ);

		double cooef =( var2.lengthVector()-2.5)/var2.lengthVector();
		var2.xCoord*=cooef;
		var2.yCoord*=cooef;
		var2.zCoord*=cooef;
		double var5 = holder.posX  - var2.xCoord;
		double var9 = holder.posZ - var2.zCoord;
		double var7 =holder.worldObj.getHeightValue(MathHelper.floor_double(var5), MathHelper.floor_double(var9));
		if((Math.abs((holder.posY  - var2.yCoord)-var7)>2))
		{
			var7 = MathHelper.floor_double(holder.posY  - var2.yCoord) ;

			int var14 = MathHelper.floor_double(var5);
			int var15 = MathHelper.floor_double(var7);
			int var16 = MathHelper.floor_double(var9);
			while(!holder.worldObj.isAirBlock(var14, var15, var16))
			{
				var15++;
			}
			var7=var15;
		}

		holder.setPositionAndUpdate(var5, var7, var9);
		holder.playSound("mob.endermen.portal", 1.0F, 1.0F);
		holder.worldObj.playSoundEffect(holder.posX, holder.posY, holder.posZ, "mob.endermen.portal", 1.0F, 1.0F);
		
		return true;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return properties.RiftBladeRiftCreationEnabled ? EnumAction.bow : EnumAction.block;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount)
	{
		//Condition for disabling rift creation
		if (!properties.RiftBladeRiftCreationEnabled)
			return;

		if (world.isRemote)
			return;

		Vec3 var2 = player.getLook(1.0F);

		double cooef = -2;
		var2.xCoord *= cooef;
		var2.yCoord *= cooef;
		var2.zCoord *= cooef;
		double var5 = player.posX - var2.xCoord;
		double var9 = player.posZ - var2.zCoord;
		double var7 = player.posY - var2.yCoord + 2;

		int x = MathHelper.floor_double(var5);
		int y = MathHelper.floor_double(var7);
		int z = MathHelper.floor_double(var9);

		int orientation = (int) (MathHelper.floor_double((double) ((player.rotationYaw + 90) * 4.0F / 360.0F) + 0.5D) & 3);

		//TODO: This looks weird. Shouldn't we aim to only create rifts on maxed-out usage time? i.e. "<= 0"
		if (this.getMaxItemUseDuration(stack) - itemInUseCount > 12 &&
			ItemDimensionalDoor.canPlace(world, x, y, z) && ItemDimensionalDoor.canPlace(world, x, y + 1, z))
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			if (!dimension.isPocketDimension() && dimension.getLink(x, y + 1, z) == null)
			{
				dimension.createLink(x, y + 1, z, LinkTypes.POCKET);
				player.worldObj.playSoundAtEntity(player,"mods.DimDoors.sfx.riftDoor", 0.6f, 1);
				ItemDimensionalDoor.placeDoorBlock(world, x, y, z, orientation, mod_pocketDim.transientDoor);  
			} 
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			@SuppressWarnings("unchecked")
			List<EntityLiving> list =  (List<EntityLiving>) world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(player.posX-8,player.posY-8, player.posZ-8, player.posX+8,player.posY+8, player.posZ+8));
			list.remove(player);

			for (EntityLiving ent : list)
			{
				Vec3 var3 = player.getLook(1.0F).normalize();
				Vec3 var4 =  player.worldObj.getWorldVec3Pool().getVecFromPool(ent.posX -  player.posX, ent.boundingBox.minY + (double)((ent.height) / 2.0F) - ( player.posY + (double) player.getEyeHeight()), ent.posZ -  player.posZ);
				double var5 = var4.lengthVector();
				var4 = var4.normalize();
				double var7 = var3.dotProduct(var4);
				if( (var7+.1) > 1.0D - 0.025D / var5 ?  player.canEntityBeSeen(ent) : false)
				{
					((ItemRiftBlade) stack.getItem()).teleportToEntity(stack, ent, player);
					return stack;
				}
			}

			MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (hit != null)
			{
				if (world.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == properties.RiftBlockID)
				{
					if (PocketManager.getLink(hit.blockX, hit.blockY, hit.blockZ, world) != null)
					{
						Block block = mod_pocketDim.transientDoor;
						int x = hit.blockX;
						int y = hit.blockY;
						int z = hit.blockZ;

						if (player.canPlayerEdit(x, y, z, hit.sideHit, stack) && player.canPlayerEdit(x, y + 1, z, hit.sideHit, stack) && !world.isRemote)
						{
							int orientation = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

							if (BaseItemDoor.canPlace(world, x, y, z) &&
								BaseItemDoor.canPlace(world, x, y - 1, z))
							{
								ItemDimensionalDoor.placeDoorBlock(world, x, y - 1, z, orientation, block);
								player.worldObj.playSoundAtEntity(player,"mods.DimDoors.sfx.riftDoor", 0.6f, 1);
								stack.damageItem(10, player);
							}
						}
					}
					return stack;
				}
			}
			
			//FIXME: Should this be inside or after this IF?
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}

		return stack;
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
	{
		//Don't include a call to super.getIsRepairable()!
    	//That would cause this sword to accept gold as a repair material (since we set material = Gold).
		return mod_pocketDim.itemStableFabric.itemID == par2ItemStack.itemID ? true : false;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y,
		int z, int side, float hitX, float hitY, float hitZ)
	{
		if (BaseItemDoor.tryItemUse(mod_pocketDim.transientDoor, stack, player, world, x, y, z, side, true, false))
		{
			world.playSoundAtEntity(player,"mods.DimDoors.sfx.riftDoor", 0.6f, 1);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Opens a temporary door, has");
		par3List.add("a special teleport attack,");
		par3List.add("and rotates existing doors.");
	}
}
