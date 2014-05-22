package StevenDimDoors.mod_pocketDim.core;

import java.io.IOException;
import com.google.gson.stream.JsonReader;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import StevenDimDoors.mod_pocketDim.saving.IPackable;
import StevenDimDoors.mod_pocketDim.saving.PackedDimData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class DDLock
{
	private boolean isLocked;
	private final int lockKey;
	
	
	public DDLock(boolean isLocked, int lockKey)
	{
		this.isLocked = isLocked;
		this.lockKey = lockKey;
	}
	
	public int getLockKey()
	{
		return this.lockKey;
	}
	/**
	 * See if the lock is currently locked. False if there is no lock.
	 * @return
	 */
	public boolean isLocked()
	{
		return this.isLocked;
	}
	
	/**
	 * set the state of the lock. Returns false if there is no lock to set, 
	 * otherwise returns true
	 * @param flag
	 */
	public void lock(boolean flag)
	{
		this.isLocked = flag;
	}
	
	
	/**
	 * see if we could unlock this door if it where locked.
	 * @param link
	 * @param itemStack
	 * @return
	 */
	public boolean canOpen(ItemStack itemStack)
	{
		for(int key :getKeys(itemStack))
		{
			if(this.lockKey == key)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tries to open this lock
	 * @param item
	 * @return
	 */
	public boolean open(ItemStack itemStack)
	{
		return (!this.isLocked)||this.canOpen(itemStack);
	}
	
	/**
	 * sets the key/s to the given key/s
	 * @return 
	 * @return
	 */
	
	/**
	 * gets all the keys stored on a single key item
	 * @return
	 */
	public static int[] getKeys(ItemStack itemStack)
	{
		if (!itemStack.hasTagCompound())
		{
			initNBTTags(itemStack);
		}
		return itemStack.getTagCompound().getIntArray("DDKeys");
	}
	
	/**
	 * adds the key/s to the given key
	 * @return 
	 * @return
	 */
	public static void addKeys(ItemStack itemStack, int[] keysToAdd)
	{
		int[] oldKeys = DDLock.getKeys(itemStack);
		int[] newKeys = new int[keysToAdd.length+oldKeys.length];
		System.arraycopy(oldKeys, 0, newKeys, 0, oldKeys.length);
		System.arraycopy(keysToAdd, 0, newKeys, oldKeys.length, keysToAdd.length);
		setKeys(itemStack,newKeys);
	}
	
	
	/**
	 * sets the key/s to the given key/s
	 * @return 
	 * @return
	 */
	public static void setKeys(ItemStack itemStack, int[] keys)
	{
		if (!itemStack.hasTagCompound())
		{
			initNBTTags(itemStack);
		}
		NBTTagCompound tag = itemStack.getTagCompound();
		tag.setIntArray("DDKeys", keys);
		itemStack.setTagCompound(tag);
	}
	
	/**
	 * Gives the key a new NBTTag
	 * @param itemStack
	 */
	public static void initNBTTags(ItemStack itemStack)
	{
		itemStack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = itemStack.getTagCompound();
		tag.setIntArray("DDKeys", new int[0]);
		tag.setBoolean("HasCreatedLock", false);
		itemStack.setTagCompound(tag);
	}
	
	public static boolean hasCreatedLock(ItemStack key)
	{
		if(isKey(key))
		{
			if(key.hasTagCompound())
			{
				return key.getTagCompound().getBoolean("HasCreatedLock");
			}
			initNBTTags(key);
		}
		return false;
	}
	
	public static boolean isKey(ItemStack key)
	{
		return key.getItem() instanceof ItemDDKey;
	}
	
	

	public static DDLock createLock(ItemStack itemStack, int lockKey2)
	{
		itemStack.getTagCompound().setBoolean("HasCreatedLock", true);
		DDLock.setKeys(itemStack, new int[]{lockKey2});
		return new DDLock(true, lockKey2);
		

	}
	
}
