package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class itemDimDoor extends ItemDoor
{
	private static DDProperties properties = null;

	public itemDimDoor(int par1, Material par2Material)
	{
		super(par1, par2Material);
		this.setMaxStackSize(64);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		if (properties == null)
			properties = DDProperties.instance();
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Place on the block under a rift");
		par3List.add("to activate that rift or place");
		par3List.add("anywhere else to create a");
		par3List.add("pocket dimension.");
	}

	@Override
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


			if(par1ItemStack.getItem() instanceof itemExitDoor)
			{
				var11 = mod_pocketDim.exitDoor;
			}

			else if (par1ItemStack.getItem() instanceof ItemChaosDoor)
			{
				var11 = mod_pocketDim.unstableDoor;
			}
			else if (par1ItemStack.getItem() instanceof itemDimDoor)
			{
				var11 = mod_pocketDim.dimensionalDoor;
			}
			else
			{
				//Do nothing
				return false;
			}

			if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)&&!par3World.isRemote)
			{
				int var12 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

				if (!canPlace(par3World, par4, par5, par6) || !canPlace(par3World, par4, par5+1, par6))
				{
					return false;
				}
				else 
				{
					int offset = 0;
					int idBlock = par3World.getBlockId(par4, par5-1, par6);

					if(Block.blocksList.length>idBlock&&idBlock!=0)
					{
						if(Block.blocksList[idBlock].isBlockReplaceable(par3World, par4, par5-1, par6))
						{
							offset = 1;
						}
					}

					placeDoorBlock(par3World, par4, par5-offset, par6, var12, var11);

					--par1ItemStack.stackSize;
					return true;
				}
			}
			else
			{
				return false;
			}
		}
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
			var21 = 4;
		}
		Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
		return par1World.rayTraceBlocks_do_do(var13, var23, true, false);
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}
		
		MovingObjectPosition hit = 	this.getMovingObjectPositionFromPlayer(player.worldObj, player, false );
		if (hit != null)
		{
			if (world.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == properties.RiftBlockID)
			{
				IDimLink link = PocketManager.getLink(hit.blockX, hit.blockY, hit.blockZ, world.provider.dimensionId);
				if (link != null)
				{
					Block block;
					if (stack.getItem() instanceof itemExitDoor)
					{
						block = mod_pocketDim.exitDoor;
					}
					else if (stack.getItem() instanceof ItemChaosDoor)
					{
						block = mod_pocketDim.unstableDoor;
					}
					else if (stack.getItem() instanceof itemDimDoor)
					{
						block = mod_pocketDim.dimensionalDoor;
					}
					else
					{
						//Do nothing
						return stack;
					}

					int x = hit.blockX;
					int y = hit.blockY;
					int z = hit.blockZ;
					int par7 = 0;
					
					if (player.canPlayerEdit(x, y, z, par7, stack) && player.canPlayerEdit(x, y - 1, z, par7, stack))
					{
						int orientation = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
						
						if (!canPlace(world, x, y, z) || !canPlace(world, x, y - 1, z))
						{
							return stack;
						}
						else
						{
							placeDoorBlock(world, x, y - 1, z, orientation, block);
							if (!player.capabilities.isCreativeMode)
							{
								stack.stackSize--;
							}
						}
					}
				}
			}
		}
		return stack;
	}

	private static boolean canPlace(World world, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);

		return (id == properties.RiftBlockID || id == 0 || Block.blocksList[id].blockMaterial.isReplaceable());
	}
}