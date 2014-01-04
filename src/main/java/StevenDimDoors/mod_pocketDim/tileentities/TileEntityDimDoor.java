package StevenDimDoors.mod_pocketDim.tileentities;

import StevenDimDoors.mod_pocketDim.ServerPacketHandler;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDimDoor extends TileEntity 
{
	public boolean openOrClosed;
	public int orientation;
	public boolean hasExit;
	public boolean isDungeonChainLink;
	public boolean hasGennedPair=false;

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
	 public Packet getDescriptionPacket()
	 {
		 if(PocketManager.getLink(xCoord, yCoord, zCoord, worldObj)!=null)
		 {
			 return ServerPacketHandler.createLinkPacket(PocketManager.getLink(xCoord, yCoord, zCoord, worldObj).link());
		 }
		 return null;
	 }

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		try
		{
			this.openOrClosed = nbt.getBoolean("openOrClosed");
			this.orientation = nbt.getInteger("orientation");
			this.hasExit = nbt.getBoolean("hasExit");
			this.isDungeonChainLink = nbt.getBoolean("isDungeonChainLink");
			this.hasGennedPair = nbt.getBoolean("hasGennedPair");
		}
		catch (Exception e) // ???
		{

		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		nbt.setBoolean("openOrClosed", this.openOrClosed);
		nbt.setBoolean("hasExit", this.hasExit);
		nbt.setInteger("orientation", this.orientation);
		nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);
		nbt.setBoolean("hasGennedPair", hasGennedPair);
	}

	
}
