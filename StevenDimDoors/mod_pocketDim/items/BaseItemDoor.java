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
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public abstract class BaseItemDoor extends ItemDoor
{
	private static DDProperties properties = null;

	public BaseItemDoor(int itemID, Material material)
	{
		super(itemID, material);
		this.setMaxStackSize(64);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		if (properties == null)
			properties = DDProperties.instance();
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public abstract void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4);

	@Override
	public abstract boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10);
	
	public static boolean tryItemUse(Block doorBlock, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, boolean requireLink, boolean reduceStack)
	{
		//FIXME: Without any sort of this documentation, this condition is like magic -_- ~SenseiKiwi
		if (par7 == 1 && !world.isRemote)
		{
			int blockID = world.getBlockId(x, y, z);
			if (blockID != 0)
			{
				if (!Block.blocksList[blockID].isBlockReplaceable(world, x, y, z))
				{
					y++;
				}
			}

			if (canPlace(world, x, y, z) && canPlace(world, x, y + 1, z) &&
				player.canPlayerEdit(x, y, z, par7, stack) && player.canPlayerEdit(x, y + 1, z, par7, stack) &&
				(!requireLink || PocketManager.getLink(x, y + 1, z, world) != null))
			{
				int orientation = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
				placeDoorBlock(world, x, y, z, orientation, doorBlock);

				if (!player.capabilities.isCreativeMode && reduceStack)
				{
					stack.stackSize--;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public final MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
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
		Vec3 var23 = var13.addVector((double) var18 * var21, (double)var17 * var21, (double)var20 * var21);
		return par1World.rayTraceBlocks_do_do(var13, var23, true, false);
	}

	@Override
	public abstract ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player);

	public boolean tryPlacingDoor(Block doorBlock, World world, EntityPlayer player, ItemStack item)
	{
		if (world.isRemote)
		{
			return false;
		}

		MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(player.worldObj, player, false);
		if (hit != null)
		{
			if (world.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == properties.RiftBlockID)
			{
				DimLink link = PocketManager.getLink(hit.blockX, hit.blockY, hit.blockZ, world.provider.dimensionId);
				if (link != null)
				{
					int x = hit.blockX;
					int y = hit.blockY;
					int z = hit.blockZ;
					int par7 = 0;

					if (player.canPlayerEdit(x, y, z, par7, item) && player.canPlayerEdit(x, y - 1, z, par7, item))
					{
						if (canPlace(world, x, y, z) && canPlace(world, x, y - 1, z))
						{
							int orientation = MathHelper.floor_double(((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
							placeDoorBlock(world, x, y - 1, z, orientation, doorBlock);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean canPlace(World world, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);

		return (id == properties.RiftBlockID || id == 0 || Block.blocksList[id].blockMaterial.isReplaceable());
	}
}