package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
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
	public abstract boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ);
	
	public static boolean tryItemUse(Block doorBlock, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, boolean requireLink, boolean reduceStack)
	{
		// Only place doors on top of blocks - check if we're targeting the top side
		if (side == 1 && !world.isRemote)
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
				player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack) &&
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
	public abstract ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player);

	public boolean tryPlacingDoor(Block doorBlock, World world, EntityPlayer player, ItemStack item)
	{
		if (world.isRemote)
		{
			return false;
		}

		MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
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

					if (player.canPlayerEdit(x, y, z, hit.sideHit, item) && player.canPlayerEdit(x, y - 1, z, hit.sideHit, item))
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