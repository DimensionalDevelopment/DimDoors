package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoor;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
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
        int otherRiftID = -1;
        if (!isPaired()) {
            otherRiftID = getNewTeleportDestination();
        } else {
            otherRiftID = getPairedRiftID();
            //DimDoors.log(this.getClass(), "This rift was already paired correctly.");
        }
        //DimDoors.log(this.getClass(), "Starting teleportation.");
        return RiftRegistry.Instance.teleportEntityToRift(entity, otherRiftID); //@todo this seems to return false?
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

    protected int getNewTeleportDestination() {
        int otherRiftID = -1;
        Location locationOfThisRift = RiftRegistry.Instance.getRiftLocation(this.riftID);
        if (locationOfThisRift.getDimensionID() == DimDoorDimensions.getPocketDimensionType(EnumPocketType.DUNGEON).getId()) { //if this dimdoor is in a pocket Dungeon
            //@todo choose between generating a new pocket or connecting to another door on a similar or close depth
            if (randomBooleanChoice(20, 80)) {
                otherRiftID = RiftRegistry.Instance.getRandomUnpairedRiftIDAroundDepth(getRiftID(), depth);
                if (otherRiftID < 0) {
                    //@todo, this should rarely happen. Put in an easter egg?
                    otherRiftID = PocketRegistry.Instance.getEntranceDoorIDOfNewPocket(EnumPocketType.DUNGEON, getRandomisedDepth(), locationOfThisRift);
                }
            } else {
                otherRiftID = PocketRegistry.Instance.getEntranceDoorIDOfNewPocket(EnumPocketType.DUNGEON, getRandomisedDepth(), locationOfThisRift);
            }
        } else {
            otherRiftID = PocketRegistry.Instance.getEntranceDoorIDOfNewPocket(EnumPocketType.PUBLIC, 0, locationOfThisRift); //@todo should this depth be 1 instead?
        }

        if (otherRiftID < 0) {
            DimDoors.warn(this.getClass(), "No suitable destination rift was found. This probably means that a pocket was created without any Doors.");
        } else {
            //@todo (should the other rift get loaded?)
            RiftRegistry.Instance.pair(getRiftID(), otherRiftID);
        }

        return otherRiftID;
    }

    private boolean randomBooleanChoice(int trueWeight, int falseWeight) { //@todo make this a utility function
        if (trueWeight <= 0 || falseWeight <= 0) {
            throw new IllegalArgumentException("Either of both weights was 0 or lower. Both should be at least 1.");
        }
        Random random = new Random();
        return (random.nextInt(trueWeight + falseWeight) < trueWeight);
    }

    protected int getRandomisedDepth() {
        Random random = new Random();
        int choice = random.nextInt(100);
        return (choice < 20 ? depth - 1 : choice < 50 ? depth : depth + 1); //@todo get rid of hardcoded stuff
    }
}
