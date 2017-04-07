package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoor;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.DDRandomUtils;
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
            DimDoors.warn(this.getClass(), "An error occured while trying to read this object from NBT.");
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
        int otherRiftID;
        if (!isPaired()) {
            otherRiftID = getNewTeleportDestination();
        } else {
            otherRiftID = getPairedRiftID();
        }
        Location tpLocation = RiftRegistry.INSTANCE.getTeleportLocation(otherRiftID);
        RiftRegistry.INSTANCE.validatePlayerPocketEntry(entity, otherRiftID);
        return TeleporterDimDoors.instance().teleport(entity, tpLocation); //@todo this seems to return false?
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
        int otherRiftID;
        Location locationOfThisRift = RiftRegistry.INSTANCE.getRiftLocation(this.riftID);
        if (locationOfThisRift.getDimensionID() == DimDoorDimensions.getPocketDimensionType(EnumPocketType.DUNGEON).getId()) { //if this dimdoor is a pocket Dungeon
            //choose between generating a new pocket or connecting to another door on a similar or close depth
            if (DDRandomUtils.weightedBoolean(20, 80)) { //@todo make this configurable
                otherRiftID = RiftRegistry.INSTANCE.getRandomUnpairedRiftIDAroundDepth(getRiftID(), depth);
                if (otherRiftID < 0) { //ergo: no other rift can be found
                    //@todo, this should rarely happen. Put in an easter egg?
                    otherRiftID = PocketRegistry.INSTANCE.getEntranceDoorIDOfNewPocket(EnumPocketType.DUNGEON, getRandomlyTransFormedDepth(), locationOfThisRift);
                }
            } else {
                otherRiftID = PocketRegistry.INSTANCE.getEntranceDoorIDOfNewPocket(EnumPocketType.DUNGEON, getRandomlyTransFormedDepth(), locationOfThisRift);
            }
        } else {
            otherRiftID = PocketRegistry.INSTANCE.getEntranceDoorIDOfNewPocket(EnumPocketType.PUBLIC, 0, locationOfThisRift); //@todo should this depth be 1 instead?
        }

        if (otherRiftID < 0) {
            DimDoors.warn(this.getClass(), "No suitable destination rift was found. This probably means that a pocket was created without any Doors.");
        } else {
            //@todo (should the other rift get loaded?)
            RiftRegistry.INSTANCE.pair(getRiftID(), otherRiftID);
        }

        return otherRiftID;
    }

    protected int getRandomlyTransFormedDepth() {
        return DDRandomUtils.transformRandomly(depth, DDConfig.getDoorRelativeDepths(), DDConfig.getDoorRelativeDepthWeights());
    }
}
