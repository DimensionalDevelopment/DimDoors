package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IDimDoor
{
	public void enterDimDoor(World world, int x, int y, int z, Entity entity);
	
	public void placeLink(World world, int x, int y, int z);
	
	public int getDrops();
	
	public TileEntity initDoorTE(World world, int x, int y, int z);
	
	public boolean isDoorOnRift(World world, int x, int y, int z);
	
}
