package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IDimDoor
{
	/**
	 * A function to enter a dim door and traverse its link, called when a player collides with an open door
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param entity
	 */
	public void enterDimDoor(World world, int x, int y, int z, Entity entity);
	
	/**
	 * called when a door is placed to determine how it will place a link
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void placeLink(World world, int x, int y, int z);
	
	public Item getDoorItem();
	
	public TileEntity initDoorTE(World world, int x, int y, int z);
	
	/**
	 * checks if any of this doors blocks are overlapping with a rift
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isDoorOnRift(World world, int x, int y, int z);
	
}
