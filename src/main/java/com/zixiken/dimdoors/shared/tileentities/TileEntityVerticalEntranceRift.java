package com.zixiken.dimdoors.shared.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityVerticalEntranceRift extends TileEntityEntranceRift {

    public boolean doorIsOpen = false;
    public EnumFacing orientation = EnumFacing.SOUTH;
    public byte lockStatus = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        doorIsOpen = nbt.getBoolean("doorIsOpen");
        orientation = EnumFacing.getFront(nbt.getInteger("orientation"));
        lockStatus = nbt.getByte("lockStatus");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("doorIsOpen", doorIsOpen);
        nbt.setInteger("orientation", orientation.getIndex());
        nbt.setByte("lockStatus", lockStatus);
        return nbt;
    }
}
