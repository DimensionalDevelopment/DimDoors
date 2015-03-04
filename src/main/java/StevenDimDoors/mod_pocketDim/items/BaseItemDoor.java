package StevenDimDoors.mod_pocketDim.items;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;

public abstract class BaseItemDoor extends ItemDoor
{
	// Maps non-dimensional door items to their corresponding dimensional door item
	// Also maps dimensional door items to themselves for simplicity
	private static HashMap<ItemDoor, BaseItemDoor> doorItemMapping = new HashMap<ItemDoor, BaseItemDoor>();
	private static DDProperties properties = null;

	/**
	 * door represents the non-dimensional door this item is associated with. Leave null for none.
	 * @param material
	 * @param vanillaDoor
	 */
	public BaseItemDoor(Material material, ItemDoor vanillaDoor)
	{
		super( material);
		this.setMaxStackSize(64);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		if (properties == null)
			properties = DDProperties.instance();
		
		doorItemMapping.put(this, this);
		if (vanillaDoor != null)
		{
			doorItemMapping.put(vanillaDoor, this);
		}
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public abstract void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4);

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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

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
	public static boolean tryToPlaceDoor(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side)
	{
		if (world.isRemote)
		{
			return false;
		}
		// Retrieve the actual door type that we want to use here.
		// It's okay if stack isn't an ItemDoor. In that case, the lookup will
		// return null, just as if the item was an unrecognized door type.
		BaseItemDoor mappedItem = doorItemMapping.get(stack.getItem());
		if (mappedItem == null)
		{
			return false;
		}
		BaseDimDoor doorBlock = mappedItem.getDoorBlock();
		if (BaseItemDoor.placeDoorOnBlock(doorBlock, stack, player, world, x, y, z, side))
		{
			return true;
		}
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
	public static boolean placeDoorOnBlock(Block doorBlock, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side)
	{
		if (world.isRemote)
		{
			return false;
		}
		// Only place doors on top of blocks - check if we're targeting the top
		// side
		if (side == 1 && !world.isRemote)
		{
			Block block = world.getBlock(x, y, z);
			if (!block.isAir(world, x, y, z))
			{
				if (!block.isReplaceable(world, x, y, z))
				{
					y++;
				}
			}

			if (canPlace(world, x, y, z) && canPlace(world, x, y + 1, z) && player.canPlayerEdit(x, y, z, side, stack)
					&& (player.canPlayerEdit(x, y + 1, z, side, stack) && stack.stackSize > 0)
					&&((stack.getItem() instanceof BaseItemDoor) || PocketManager.getLink(x, y + 1, z, world) != null))
			{
				int orientation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;
				placeDoorBlock(world, x, y, z, orientation, doorBlock);

				if (!player.capabilities.isCreativeMode)
				{
					stack.stackSize--;
				}
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
	public static boolean placeDoorOnRift(Block doorBlock, World world, EntityPlayer player, ItemStack stack)
	{
		if (world.isRemote)
		{
			return false;
		}

		MovingObjectPosition hit = BaseItemDoor.doRayTrace(player.worldObj, player, true);
		if (hit != null)
		{
			if (world.getBlock(hit.blockX, hit.blockY, hit.blockZ) == mod_pocketDim.blockRift)
			{
				DimLink link = PocketManager.getLink(hit.blockX, hit.blockY, hit.blockZ, world.provider.dimensionId);
				if (link != null)
				{
					int x = hit.blockX;
					int y = hit.blockY;
					int z = hit.blockZ;

					if (player.canPlayerEdit(x, y, z, hit.sideHit, stack) && player.canPlayerEdit(x, y - 1, z, hit.sideHit, stack))
					{
						if (canPlace(world, x, y, z) && canPlace(world, x, y - 1, z))
						{
							int orientation = MathHelper.floor_double(((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
							placeDoorBlock(world, x, y - 1, z, orientation, doorBlock);
							if (!(stack.getItem() instanceof BaseItemDoor))
							{
								((TileEntityDimDoor) world.getTileEntity(x, y, z)).hasGennedPair = true;
							}
							if (!player.capabilities.isCreativeMode)
							{
								stack.stackSize--;
							}
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
		Block block = world.getBlock(x, y, z);

		return (block == mod_pocketDim.blockRift || block.isAir(world, x, y, z) || block.getMaterial().isReplaceable());
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
	protected static MovingObjectPosition doRayTrace(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
	{
		float f = 1.0F;
		float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
		float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
		double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double) f;
		double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double) f
				+ (double) (par1World.isRemote ? par2EntityPlayer.getEyeHeight() - par2EntityPlayer.getDefaultEyeHeight() : par2EntityPlayer.getEyeHeight()); 
		double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double) f;
		Vec3 vec3 = Vec3.createVectorHelper (d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if (par2EntityPlayer instanceof EntityPlayerMP)
		{
			d3 = ((EntityPlayerMP) par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return par1World.rayTraceBlocks(vec3, vec31, par3);
	}
}