package StevenDimDoors.mod_pocketDim.tileentities;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityTransTrapdoor extends TileEntity
{
	public boolean hasRift;

	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
	{
		if (newID == 0 && PocketManager.getLink(x, y, z, world) != null)
		{
			world.setBlock(x, y, z, mod_pocketDim.blockRift.blockID);
		}
		return true;
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity() 
	{
		
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		try
		{
			this.hasRift = nbt.getBoolean("hasRift");
		}
		catch (Exception e)
		{

		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("hasRift", this.hasRift);
	}
}
