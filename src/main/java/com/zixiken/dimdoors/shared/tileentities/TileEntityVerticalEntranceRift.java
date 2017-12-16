package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityVerticalEntranceRift extends TileEntityEntranceRift { // TODO: make builder?

    public boolean doorShouldRender = true;
    public EnumFacing orientation = EnumFacing.SOUTH;
    public byte lockStatus = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        doorShouldRender = nbt.getBoolean("doorShouldRender");
        orientation = EnumFacing.getFront(nbt.getInteger("orientation"));
        lockStatus = nbt.getByte("lockStatus");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("doorShouldRender", doorShouldRender);
        nbt.setInteger("orientation", orientation.getIndex());
        nbt.setByte("lockStatus", lockStatus);
        return nbt;
    }

    @Override
    public void teleportTo(Entity entity) {
        super.teleportTo(entity);
        BlockPos offsetPos = entity.getPosition().offset(orientation);
        entity.setPositionAndRotation(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), orientation.getHorizontalAngle(), 0); // TODO: let TileEntityRift handle rotation?
    }
}
