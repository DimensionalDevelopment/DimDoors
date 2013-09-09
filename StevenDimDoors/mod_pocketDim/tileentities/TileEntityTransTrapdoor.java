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
