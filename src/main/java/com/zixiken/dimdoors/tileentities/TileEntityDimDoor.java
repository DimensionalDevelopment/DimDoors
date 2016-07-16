package com.zixiken.dimdoors.tileentities;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.nbt.NBTTagCompound;

import com.zixiken.dimdoors.watcher.ClientLinkData;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;


public class TileEntityDimDoor extends DDTileEntityBase
{
	public boolean openOrClosed;
	public EnumFacing orientation;
	public boolean hasExit;
	public byte lockStatus;
	public boolean isDungeonChainLink;
	public boolean hasGennedPair=false;

	@Override
	 public Packet getDescriptionPacket() {
         NBTTagCompound tag = new NBTTagCompound();
         writeToNBT(tag);
		 if(PocketManager.getLink(pos, worldObj)!=null) {
             ClientLinkData linkData = new ClientLinkData(PocketManager.getLink(pos, worldObj));
             NBTTagCompound link = new NBTTagCompound();
             linkData.writeToNBT(link);
             tag.setTag("Link", link);
		 }
         return new S35PacketUpdateTileEntity(pos, 1, tag);
	 }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readFromNBT(tag);

        if (tag.hasKey("Link")) {
            ClientLinkData linkData = ClientLinkData.readFromNBT(tag.getCompoundTag("Link"));
            PocketManager.getLinkWatcher().onCreated(linkData);
        }
    }

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		try {
			this.openOrClosed = nbt.getBoolean("openOrClosed");
			this.orientation = EnumFacing.getFront(nbt.getInteger("orientation"));
			this.hasExit = nbt.getBoolean("hasExit");
			this.isDungeonChainLink = nbt.getBoolean("isDungeonChainLink");
			this.hasGennedPair = nbt.getBoolean("hasGennedPair");
		} catch (Exception e) {}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean("openOrClosed", this.openOrClosed);
		nbt.setBoolean("hasExit", this.hasExit);
		nbt.setInteger("orientation", this.orientation.getIndex());
		nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);
		nbt.setBoolean("hasGennedPair", hasGennedPair);
	}

	@Override
	public float[] getRenderColor(Random rand) {
		float[] rgbaColor = {1,1,1,1};
		if (this.worldObj.provider.getDimensionId() == DimDoors.NETHER_DIMENSION_ID) {
			rgbaColor[0] = rand.nextFloat() * 0.5F + 0.4F;
			rgbaColor[1] = rand.nextFloat() * 0.05F;
			rgbaColor[2] = rand.nextFloat() * 0.05F;
		} else {
			rgbaColor[0] = rand.nextFloat() * 0.5F + 0.1F;
			rgbaColor[1] = rand.nextFloat() * 0.4F + 0.4F;
			rgbaColor[2] = rand.nextFloat() * 0.6F + 0.5F;
		}

		return rgbaColor;
	}
}
