package com.zixiken.dimdoors.tileentities;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


public class TileEntityDimDoor extends DDTileEntityBase
{
	public boolean openOrClosed;
	public EnumFacing orientation;
	public boolean hasExit;
	public byte lockStatus;
	public boolean isDungeonChainLink;
	public boolean hasGennedPair=false;

    public TileEntityDimDoor(World world) {
        super(world);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean("openOrClosed", this.openOrClosed);
		nbt.setBoolean("hasExit", this.hasExit);
		nbt.setInteger("orientation", this.orientation.getIndex());
		nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);
		nbt.setBoolean("hasGennedPair", hasGennedPair);
		return nbt;
	}

	@Override
	public float[] getRenderColor(Random rand) {
		float[] rgbaColor = {1,1,1,1};
		if (this.world.provider.getDimension() == -1) {
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
