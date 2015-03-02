package StevenDimDoors.mod_pocketDim.tileentities;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import StevenDimDoors.mod_pocketDim.ServerPacketHandler;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;



public class TileEntityDimDoor extends DDTileEntityBase 
{
	public boolean openOrClosed;
	public int orientation;
	public boolean hasExit;
	public byte lockStatus;
	public boolean isDungeonChainLink;
	public boolean hasGennedPair=false;

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	 public Packet getDescriptionPacket()
	 {
		 if(PocketManager.getLink(xCoord, yCoord, zCoord, worldObj)!=null)
		 {
			 return ServerPacketHandler.createLinkPacket(new ClientLinkData(PocketManager.getLink(xCoord, yCoord, zCoord, worldObj)));
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

	@Override
	public float[] getRenderColor(Random rand)
	{
		float[] rgbaColor = {1,1,1,1};
		if (this.worldObj.provider.dimensionId == mod_pocketDim.NETHER_DIMENSION_ID)
		{
			rgbaColor[0] = rand.nextFloat() * 0.5F + 0.4F;
			rgbaColor[1] = rand.nextFloat() * 0.05F;
			rgbaColor[2] = rand.nextFloat() * 0.05F;
		}
		else
		{
			rgbaColor[0] = rand.nextFloat() * 0.5F + 0.1F;
			rgbaColor[1] = rand.nextFloat() * 0.4F + 0.4F;
			rgbaColor[2] = rand.nextFloat() * 0.6F + 0.5F;
		}
		return rgbaColor;
	}
}
