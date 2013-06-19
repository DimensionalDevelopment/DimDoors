package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRiftBlade extends itemDimDoor
{
	public ItemRiftBlade(int par1, Material par2Material)
	{
		super(par1, par2Material);

		// this.setTextureFile("/PocketBlockTextures.png");
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxStackSize(1);

		//   this.itemIcon=5;
		this.setMaxDamage(500);
		this.hasSubtypes=false;
		//TODO move to proxy
		if (properties == null)
			properties = DDProperties.instance();
	}

	private static DDProperties properties = null;

	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

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

	public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
	{
		par1ItemStack.damageItem(1, par3EntityLiving);
		return true;
	}

	public int getDamageVsEntity(Entity par1Entity)
	{
		return 8;
	}

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

	protected boolean teleportToEntity(ItemStack item, Entity par1Entity, EntityPlayer holder)
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
	public ItemStack onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		return par1ItemStack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	 public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return properties.RiftBladeRiftCreationEnabled ? EnumAction.bow : EnumAction.block;
	}

	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
	{
		//Condition for disabling rift creation
		if (!properties.RiftBladeRiftCreationEnabled)
			return;

		Vec3 var2 = par3EntityPlayer.getLook(1.0F);

		double cooef = -2;
		var2.xCoord*=cooef;
		var2.yCoord*=cooef;
		var2.zCoord*=cooef;
		double var5 = par3EntityPlayer.posX  - var2.xCoord;
		double var9 = par3EntityPlayer.posZ - var2.zCoord;
		double var7 = par3EntityPlayer.posY - var2.yCoord+2;

		int x = MathHelper.floor_double(var5);
		int y = MathHelper.floor_double(var7);
		int z = MathHelper.floor_double(var9);

		int rotation = (int) (MathHelper.floor_double((double)((par3EntityPlayer.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
		LinkData link = new LinkData(par2World.provider.dimensionId, 0, x, y, z, x, y, z, true,rotation);

		if(this.getMaxItemUseDuration(par1ItemStack)-par4>12&&!par2World.isRemote&&this.canPlace(par2World, x, y, z, rotation))
		{

			if(dimHelper.dimList.get(par2World.provider.dimensionId)!=null)
			{
				if(dimHelper.dimList.get(par2World.provider.dimensionId).depth==0)
				{
					dimHelper.instance.createPocket(link,true, false);
				}
			}
			else
			{
				dimHelper.instance.createPocket(link,true, false);
			}
			placeDoorBlock(par2World, x, y-1, z, rotation,  mod_pocketDim.transientDoor);   
		}
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		Boolean didFindThing=false;
		MovingObjectPosition hit = 	this.getMovingObjectPositionFromPlayer(par3EntityPlayer.worldObj, par3EntityPlayer, false );
		if(hit!=null&&!par2World.isRemote)
		{
			if(par2World.getBlockId(hit.blockX, hit.blockY, hit.blockZ)==properties.RiftBlockID)
			{
				LinkData link = dimHelper.instance.getLinkDataFromCoords(hit.blockX, hit.blockY, hit.blockZ, par2World);
				if(link!=null)
				{

					Block var11 = mod_pocketDim.transientDoor;
					int par4 = hit.blockX;
					int par5 = hit.blockY;
					int par6 = hit.blockZ;
					int par7 = 0 ;




					if (par3EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par3EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)&&!par2World.isRemote)
					{
						int var12 = MathHelper.floor_double((double)((par3EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

						if (!this.canPlace(par2World, par4, par5, par6, var12)||!this.canPlace(par2World, par4, par5-1, par6, var12)||dimHelper.instance.getLinkDataFromCoords(par4, par5, par6, par2World)==null)
						{
							return par1ItemStack;
						}
						else 
						{

							placeDoorBlock(par2World, par4, par5-1, par6, var12, var11);
							didFindThing=true;


							par1ItemStack.damageItem(10, par3EntityPlayer);

						}
					}
				}
			}
			else if(par2World.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == properties.TransientDoorID)
			{
				didFindThing=true;
			}

		}




		if(!par3EntityPlayer.worldObj.isRemote)
		{
			List<EntityLiving> list =  par3EntityPlayer.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox( par3EntityPlayer.posX-8,par3EntityPlayer.posY-8, par3EntityPlayer.posZ-8, par3EntityPlayer.posX+8,par3EntityPlayer.posY+8, par3EntityPlayer.posZ+8));
			list.remove(par3EntityPlayer);


			for(EntityLiving ent : list)
			{

				Vec3 var3 = par3EntityPlayer.getLook(1.0F).normalize();
				Vec3 var4 =  par3EntityPlayer.worldObj.getWorldVec3Pool().getVecFromPool(ent.posX -  par3EntityPlayer.posX, ent.boundingBox.minY + (double)((ent.height) / 2.0F) - ( par3EntityPlayer.posY + (double) par3EntityPlayer.getEyeHeight()), ent.posZ -  par3EntityPlayer.posZ);
				double var5 = var4.lengthVector();
				var4 = var4.normalize();
				double var7 = var3.dotProduct(var4);
				if( (var7+.1) > 1.0D - 0.025D / var5 ?  par3EntityPlayer.canEntityBeSeen(ent) : false)
				{
					System.out.println(list.size());
					ItemRiftBlade.class.cast(par1ItemStack.getItem()).teleportToEntity(par1ItemStack,ent, par3EntityPlayer);
					didFindThing=true;
					break;

					//ItemRiftBlade.class.cast(item.getItem()).teleportTo(event.entityPlayer, ent.posX, ent.posY, ent.posZ);
				}
			}


		}
		//	if(dimHelper.dimList.get(par2World.provider.dimensionId)!=null&&!par2World.isRemote&&!didFindThing)
		{

			par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));

		}




		return par1ItemStack;

	}
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

	}
	public int getItemEnchantability()
	{
		return EnumToolMaterial.GOLD.getEnchantability();
	}
	public String func_77825_f()
	{
		return EnumToolMaterial.GOLD.toString();
	}
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
	{
		return true;
	}

	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (par7 != 1)
		{
			return false;
		}
		else
		{
			++par5;
			Block var11;



			var11 = mod_pocketDim.transientDoor;







			if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)&&!par3World.isRemote)
			{
				int var12 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

				if (!this.canPlace(par3World, par4, par5, par6, var12)||dimHelper.instance.getLinkDataFromCoords(par4, par5+1, par6, par3World)==null)
				{
					return false;
				}
				else 
				{

					placeDoorBlock(par3World, par4, par5, par6, var12, var11);


					par1ItemStack.damageItem(10, par2EntityPlayer);
					return true;
				}
			}
			else
			{
				return false;
			}
		}
	}



	@SideOnly(Side.CLIENT)

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	 public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Opens a temporary doors,");
		par3List.add ("special teleport attack,");
		par3List.add ("and rotates existing doors");
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		if(!par2World.isRemote)
		{
			/**
    		//creates the first half of the link on item creation
    		int key= dimHelper.instance.createUniqueInterDimLinkKey();
    		LinkData linkData= new LinkData(par2World.provider.dimensionId,MathHelper.floor_double(par3EntityPlayer.posX),MathHelper.floor_double(par3EntityPlayer.posY),MathHelper.floor_double(par3EntityPlayer.posZ));
    		System.out.println(key);

    		dimHelper.instance.interDimLinkList.put(key, linkData);
    		par1ItemStack.setItemDamage(key);
			 **/
		}
	}
}
