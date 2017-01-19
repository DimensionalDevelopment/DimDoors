/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class RiftRegistry {

    private DDTileEntityBase lastBrokenRift = null; //@todo, redo this functionality in a more refined way
    public static final RiftRegistry Instance = new RiftRegistry();

    // Privates
    private int nextRiftID;
    private final Map<Integer, Location> riftList;

    // Methods
    private RiftRegistry() {
        nextRiftID = 0;
        riftList = new HashMap();
    }

    public void reset() {
        nextRiftID = 0;
        riftList.clear();
        lastBrokenRift = null;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        nextRiftID = nbt.getInteger("nextUnusedID");
        if (nbt.hasKey("riftData")) {
            NBTTagCompound riftsNBT = nbt.getCompoundTag("riftData");
            int i = 0;
            String tag = "" + i;
            while (riftsNBT.hasKey(tag)) {
                NBTTagCompound riftNBT = riftsNBT.getCompoundTag(tag);
                Location riftLocation = Location.readFromNBT(riftNBT);
                riftList.put(i, riftLocation);

                i++;
                tag = "" + i;
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("nextUnusedID", nextRiftID);
        NBTTagCompound riftsNBT = new NBTTagCompound();
        for (Map.Entry<Integer, Location> entry : riftList.entrySet()) {
            riftsNBT.setTag("" + entry.getKey(), Location.writeToNBT(entry.getValue()));
        }
        nbt.setTag("riftData", riftsNBT);
    }

    public int registerNewRift(DDTileEntityBase rift) {
        riftList.put(nextRiftID, Location.getLocation(rift));
        DimDoors.log(this.getClass(), "Rift registered as ID: " + nextRiftID);
        nextRiftID++;
        RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        return nextRiftID - 1;
    }

    public void unregisterRift(int riftID, World world) {
        if (riftList.containsKey(riftID)) {
            riftList.remove(riftID);
            RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        }
    }

    public Location getRiftLocation(int ID) {
        return riftList.get(ID);
    }

    public void pair(int riftID, int riftID2) {
        if (riftID < 0 || riftID2 < 0) {
            return;
        }
        DimDoors.log(this.getClass(), "pairing rift with ID " + riftID + " to rift with ID " + riftID2);
        Location location = riftList.get(riftID);
        TileEntity tileEntity = location.getTileEntity(); //@todo this method might need to be in another class?
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.pair(riftID2);
        }
    }

    public void unpair(int riftID) {
        if (riftID < 0) {
            return;
        }
        Location location = riftList.get(riftID);
        if (location == null) {
            DimDoors.log(this.getClass(), "riftID with null location = " + riftID);
        }
        TileEntity tileEntity = location.getTileEntity();
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.unpair();
        }
    }

    public void setLastChangedRift(DDTileEntityBase origRift) {
        lastBrokenRift = origRift;
    }

    public DDTileEntityBase getLastChangedRift() {
        return lastBrokenRift;
    }

    public void teleportEntityToRift(Entity entity, int pairedRiftID) {
        TeleportHelper.teleport(entity, ((DDTileEntityBase) getRiftLocation(pairedRiftID).getTileEntity()).getTeleportTarget());
    }
}
