package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoor;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityDimDoor extends DDTileEntityBase {

    public boolean doorIsOpen = false;
    public EnumFacing orientation = EnumFacing.SOUTH;
    public byte lockStatus = 0;
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        try {
            this.doorIsOpen = nbt.getBoolean("doorIsOpen");
            this.orientation = EnumFacing.getFront(nbt.getInteger("orientation"));
            this.lockStatus = nbt.getByte("lockStatus");
        } catch (Exception e) {
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("doorIsOpen", this.doorIsOpen);
        nbt.setInteger("orientation", this.orientation.getIndex());
        nbt.setByte("lockStatus", lockStatus);
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
        return new Location(this.getWorld().provider.getDimension(), this.getPos().offset(orientation).down());
    }

    @Override
    public boolean tryTeleport(Entity entity) {
        //DimDoors.log(this.getClass(), "Trying to teleport from rift " + getRiftID() + ".");
        if (!isPaired()) {
            //DimDoors.log(this.getClass(), "Trying to find suitable destination rift.");
            int randomPairedRiftID = RiftRegistry.Instance.getRandomUnpairedRiftID(getRiftID());
            if (randomPairedRiftID < 0) {
                DimDoors.warn(this.getClass(), "No suitable destination rift was found.");
                return false;
            }
            RiftRegistry.Instance.pair(getRiftID(), randomPairedRiftID);
            //@todo try to automatically pair this door somehow
        } else {
            //DimDoors.log(this.getClass(), "This rift was already paired correctly.");
        }
        //DimDoors.log(this.getClass(), "Starting teleportation.");
        return RiftRegistry.Instance.teleportEntityToRift(entity, getPairedRiftID()); //this seems to return false...
    }

    public void uponDoorPlacement(@Nullable TileEntity possibleOldRift) {
        if (possibleOldRift instanceof DDTileEntityBase) {
            DDTileEntityBase oldRift = (DDTileEntityBase) possibleOldRift;
            //load data from old rift (that must already have been registered)
            loadDataFrom(oldRift);
        } else {
            //default data and set register this rift in the registry
            register(0); //@todo check if it's in a pocket and register it at that depth instead if applicable
        }
        //storing the orientation inside the tile-entity, because that thing can actually save the orientation in the worldsave, unlike the block itself, which fail at that stuff somehow
        this.orientation = this.getWorld().getBlockState(this.getPos()).getValue(BlockDimDoor.FACING).getOpposite();
    }
}
