package StevenDimDoors.mod_pocketDim.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.IDimDoor;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;

public class ItemDDKey extends Item
{
	public static final int TIME_TO_UNLOCK = 30;

	public ItemDDKey()
	{
		super();
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxStackSize(1);

	}

	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{

	}

	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		if (DDLock.hasCreatedLock(par1ItemStack))
		{
			par3List.add(StatCollector.translateToLocal("info.riftkey.bound"));
		}
		else
		{
			par3List.add(StatCollector.translateToLocal("info.riftkey.unbound"));
		}
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		return !DDLock.hasCreatedLock(par1ItemStack);
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9,
			float par10)
	{
		player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));

		return false;
	}

	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float playerX, float playerY,
			float playerZ)
	{
		if (world.isRemote)
		{
			return false;
		}

		if (player.inventory.getCurrentItem() != null)
		{
			return true;
		}
		Block block = world.getBlock(x, y, z);
		// make sure we are dealing with a door
		if (!(block instanceof IDimDoor))
		{
			return false;
		}

		DimLink link = PocketManager.getLink(x, y, z, world);
		// dont do anything to doors without links
		if (link == null)
		{
			return false;
		}

		// what to do if the door has a lock already
		if (link.hasLock())
		{
			if (link.doesKeyUnlock(itemStack))
			{
				if (link.getLockState())
				{
					world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyUnlock", 1F, 1F);
				}
				else
				{
					world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyLock", 1F, 1F);
				}
				PocketManager.getDimensionData(world).lock(link, !link.getLockState());
				PocketManager.getLinkWatcher().update(new ClientLinkData(link));

			}
			else
			{
				world.playSoundAtEntity(player, mod_pocketDim.modid + ":doorLocked", 1F, 1F);
			}
		}
		else
		{
			if (!DDLock.hasCreatedLock(itemStack))
			{
				world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyLock", 1F, 1F);
				PocketManager.getDimensionData(world).createLock(link, itemStack, world.rand.nextInt(Integer.MAX_VALUE));
				PocketManager.getLinkWatcher().update(new ClientLinkData(link));
			}
		}
		return false;
	}

	/**
	 * Handle removal of locks here
	 */
	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int heldTime)
	{
		int j = this.getMaxItemUseDuration(itemStack) - heldTime;
		if (j >= TIME_TO_UNLOCK)
		{
			//Raytrace to make sure we are still looking at a door
			MovingObjectPosition pos = getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if (pos != null && pos.typeOfHit ==  MovingObjectPosition.MovingObjectType.BLOCK)
			{
				//make sure we have a link and it has a lock
				DimLink link = PocketManager.getLink(pos.blockX, pos.blockY, pos.blockZ, player.worldObj);
				if (link != null && link.hasLock())
				{
					//make sure the given key is able to access the lock
					if (link.doesKeyUnlock(itemStack) && !world.isRemote)
					{
						PocketManager.getDimensionData(world).removeLock(link, itemStack);
						world.playSoundAtEntity(player, mod_pocketDim.modid + ":doorLockRemoved", 1F, 1F);

					}
				}
			}
		}
		player.clearItemInUse();

	}

	/**
	 * Raytrace to make sure we are still looking at the right block while preparing to remove the lock
	 */
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		// no need to check every tick, twice a second instead
		if (count % 10 == 0)
		{
			MovingObjectPosition pos = getMovingObjectPositionFromPlayer(player.worldObj, player, true);
			if (pos != null && pos.typeOfHit ==  MovingObjectPosition.MovingObjectType.BLOCK)
			{
				DimLink link = PocketManager.getLink(pos.blockX, pos.blockY, pos.blockZ, player.worldObj);
				if (link != null && link.hasLock())
				{
					if (link.doesKeyUnlock(stack))
					{
						return;
					}
				}
			}
			player.clearItemInUse();
		}
	}

	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.bow;
	}

	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		return par1ItemStack;
	}

	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName(par1ItemStack));
	}
}
