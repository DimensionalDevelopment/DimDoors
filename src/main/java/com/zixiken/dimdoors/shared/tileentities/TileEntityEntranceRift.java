package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.util.RGBA;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

// TODO: merge horizontal and vertical entrances' render code into one, and support custom sizes
public abstract class TileEntityEntranceRift extends TileEntityRift {
    @Getter private boolean placeRiftOnBreak = false;
    @Getter private boolean closeAfterPassThrough = false;

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("placeRiftOnBreak", placeRiftOnBreak);
        nbt.setBoolean("closeAfterPassThrough", closeAfterPassThrough);
        return nbt;
    }

    @Override
    public void update() {

    }

    public void setPlaceRiftOnBreak(boolean placeRiftOnBreak) { this.placeRiftOnBreak = placeRiftOnBreak; markDirty(); }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public boolean teleport(Entity entity) {
        boolean status = super.teleport(entity);
        if (riftStateChanged /*|| TODO: get links from registry */) {
            placeRiftOnBreak = true;
            markDirty();
        }
        return status;
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
}
