package StevenDimDoors.mod_pocketDim.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

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
