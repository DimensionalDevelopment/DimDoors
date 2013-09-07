package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IDimDoor
{
	public void enterDimDoor(World world, int x, int y, int z, Entity entity);
	
	public void placeDimDoor(World world, int x, int y, int z);
	
	public int getDrops();
}
