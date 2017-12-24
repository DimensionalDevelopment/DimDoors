package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import ddutils.Location;
import ddutils.render.RGBA;
import ddutils.TeleportUtils;
import ddutils.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Random;

// TODO: merge horizontal and vertical entrances' render code into one, and support custom sizes
public class TileEntityEntranceRift extends TileEntityRift {
    @Getter private boolean placeRiftOnBreak = false;
    @Getter private boolean closeAfterPassThrough = false;
    @Getter public boolean shouldRender = true;
    @Getter public byte lockStatus = 0;

    // Set by the block, not saved and not synced to the client
    public EnumFacing orientation;
    public int tpOffset = 1; // TODO: float?
    public double extendUp = 0.5; // Use += to set these. TODO: @SideOnly client?
    public double extendDown = 0.5;
    public double extendLeft = 0.5;
    public double extendRight = 0.5;
    public double pushIn = 0.01; // TODO: set to 0, and set on door

    @Override
    public void copyFrom(TileEntityRift oldRift) {
        super.copyFrom(oldRift);
        if (oldRift instanceof TileEntityEntranceRift) {
            TileEntityEntranceRift oldEntranceRift = (TileEntityEntranceRift) oldRift;
            closeAfterPassThrough = oldEntranceRift.closeAfterPassThrough;
        }
        placeRiftOnBreak = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        placeRiftOnBreak = nbt.getBoolean("placeRiftOnBreak");
        closeAfterPassThrough = nbt.getBoolean("closeAfterPassThrough");
        shouldRender = nbt.getBoolean("shouldRender");
        lockStatus = nbt.getByte("lockStatus");

        orientation = EnumFacing.byName(nbt.getString("orientation")); // TODO: avoid having to save these and generate on load based on blockstate
        tpOffset = nbt.getInteger("tpOffset");
        extendUp = nbt.getDouble("extendUp");
        extendDown = nbt.getDouble("extendDown");
        extendLeft = nbt.getDouble("extendLeft");
        extendRight = nbt.getDouble("extendRight");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("placeRiftOnBreak", placeRiftOnBreak);
        nbt.setBoolean("closeAfterPassThrough", closeAfterPassThrough);
        nbt.setBoolean("shouldRender", shouldRender);
        nbt.setByte("lockStatus", lockStatus);

        if (orientation != null) nbt.setString("orientation", orientation.getName()); // TODO: why is this sometimes null on generated transient entrances?
        nbt.setInteger("tpOffset", tpOffset);
        nbt.setDouble("extendUp", extendUp);
        nbt.setDouble("extendDown", extendDown);
        nbt.setDouble("extendLeft", extendLeft);
        nbt.setDouble("extendRight", extendRight);

        return nbt;
    }

    @Override
    public void update() {

    }

    public void setPlaceRiftOnBreak(boolean placeRiftOnBreak) { this.placeRiftOnBreak = placeRiftOnBreak; markDirty(); }
    public void setShouldRender(boolean shouldRender) { this.shouldRender = shouldRender; markDirty(); }
    public void setLockStatus(byte lockStatus) { this.lockStatus = lockStatus; markDirty(); }

    @Override
    public boolean teleport(Entity entity) {
        boolean status = super.teleport(entity);
        if (riftStateChanged && !alwaysDelete) {
            placeRiftOnBreak = true;
            markDirty();
        }
        return status;
    }

    @Override
    public void teleportTo(Entity entity) {
        TeleportUtils.teleport(entity, new Location(world, pos.offset(orientation, tpOffset)), orientation.getHorizontalAngle(), 0);
    }

    public RGBA getEntranceRenderColor(Random rand) { // TODO: custom color
        float red, green, blue;
        switch(world.provider.getDimension()) {
            case -1: // Nether
                red = rand.nextFloat() * 0.5F + 0.4F;
                green = rand.nextFloat() * 0.05F;
                blue = rand.nextFloat() * 0.05F;
                break;
            default:
                red = rand.nextFloat() * 0.5F + 0.1F;
                green = rand.nextFloat() * 0.4F + 0.4F;
                blue = rand.nextFloat() * 0.6F + 0.5F;
                break;
        }
        return new RGBA(red, green, blue, 1);
    }

    @Override
    public boolean isEntrance() {
        return true;
    }
}
