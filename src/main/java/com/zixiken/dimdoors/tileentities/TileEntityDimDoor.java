package com.zixiken.dimdoors.tileentities;

import com.zixiken.dimdoors.shared.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityDimDoor extends DDTileEntityBase {

    public boolean doorIsOpen = false;
    public EnumFacing orientation = EnumFacing.SOUTH;
    public boolean hasExit = false;
    public byte lockStatus = 1;
    public boolean isDungeonChainLink = false;
    public boolean hasGennedPair = false;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        try {
            this.doorIsOpen = nbt.getBoolean("doorIsOpen");
            this.orientation = EnumFacing.getFront(nbt.getInteger("orientation"));
            this.hasExit = nbt.getBoolean("hasExit");
            this.isDungeonChainLink = nbt.getBoolean("isDungeonChainLink");
            this.hasGennedPair = nbt.getBoolean("hasGennedPair");
        } catch (Exception e) {
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("doorIsOpen", this.doorIsOpen);
        nbt.setBoolean("hasExit", this.hasExit);
        nbt.setInteger("orientation", this.orientation.getIndex());
        nbt.setBoolean("isDungeonChainLink", isDungeonChainLink);
        nbt.setBoolean("hasGennedPair", hasGennedPair);
        return nbt;
    }

    @Override
    public float[] getRenderColor(Random rand) {
        float[] rgbaColor = {1, 1, 1, 1};
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

    @Override
    public Location getTeleportTargetLocation() {
        return new Location(this.getWorld().provider.getDimension(), this.getPos().offset(orientation));
    }

    @Override
    public boolean tryTeleport(Entity entity) {
        if (!isPaired()) {
            int randomPairedRiftID =  RiftRegistry.Instance.getRandomUnpairedRiftID(getRiftID());
            if (randomPairedRiftID < 0) {
                return false;
            }
            RiftRegistry.Instance.pair(getRiftID(),randomPairedRiftID);
            //@todo try to automatically pair this door somehow
        }
        return RiftRegistry.Instance.teleportEntityToRift(entity, getPairedRiftID());
    }
}
