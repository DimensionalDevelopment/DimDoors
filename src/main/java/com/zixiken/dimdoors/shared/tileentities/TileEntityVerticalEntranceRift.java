package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.TeleportUtils;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
        TeleportUtils.teleport(entity, new Location(world, pos.offset(orientation)), orientation.getHorizontalAngle(), 0);

        int dim = WorldUtils.getDim(world);
        if (entity instanceof EntityPlayer && DimDoorDimensions.isPocketDimension(dim)) { // TODO
            PocketRegistry.getForDim(dim).allowPlayerAtLocation((EntityPlayer) entity, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
