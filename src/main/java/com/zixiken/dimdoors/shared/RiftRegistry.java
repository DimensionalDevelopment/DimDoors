/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class RiftRegistry {

    private int maximumDungeonDepth = 2;
    private DDTileEntityBase lastBrokenRift = null; //@todo, redo this functionality in a more refined way
    public static final RiftRegistry Instance = new RiftRegistry();

    // Privates
    private int nextRiftID;
    private final Map<Integer, Location> riftList; //maps all rifts in the world to their ID
    //@todo somehow remove rifts from this list even if they are removed in creative
    private final Map<Integer, Location> unpairedRiftList; //maps of all rifts in the world that are not paired to their ID
    private final List<Map<Integer, Location>> unpairedDepthRiftList; //List of all "unpairedRiftList s" per Dungeon Depth. Depth 0 is almost anything outside the dungeon dimension
    //@todo, once we have a dungeon dimension this List should be implemented (for determining what doors an unpaired door can link to)

    // Methods
    private RiftRegistry() {
        nextRiftID = 0;
        riftList = new HashMap();
        unpairedRiftList = new HashMap();
        unpairedDepthRiftList = new ArrayList();
        for (int i = 0; i < maximumDungeonDepth; i++) {
            unpairedDepthRiftList.add(new HashMap());
        }
    }

    public void reset() {
        nextRiftID = 0;
        riftList.clear();
        unpairedRiftList.clear();
        for (Map<Integer, Location> dimensionSpecificUnpairedRiftList : unpairedDepthRiftList) {
            dimensionSpecificUnpairedRiftList.clear();
        }
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
        //@todo code for loading the other rift lists
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("nextUnusedID", nextRiftID);
        NBTTagCompound riftsNBT = new NBTTagCompound();
        for (Map.Entry<Integer, Location> entry : riftList.entrySet()) {
            riftsNBT.setTag("" + entry.getKey(), Location.writeToNBT(entry.getValue()));
        }
        nbt.setTag("riftData", riftsNBT);
        //@todo code for loading the other rift lists
    }

    public int registerNewRift(DDTileEntityBase rift) {
        Location riftLocation = Location.getLocation(rift);
        riftList.put(nextRiftID, riftLocation);
        unpairedRiftList.put(nextRiftID, riftLocation);
        //@todo register the rift per dungeon depth as well
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
        Location location = riftList.get(riftID);
        TileEntity tileEntity = location.getTileEntity(); //@todo this method might need to be in another class?
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.pair(riftID2);
        }
        unpairedRiftList.remove(riftID, location);
    }

    public void unpair(int riftID) {
        if (riftID < 0) {
            return;
        }
        Location location = riftList.get(riftID);
        if (location == null) {
            DimDoors.log(this.getClass(), "RiftID with null location: rift " + riftID);
        }
        TileEntity tileEntity = location.getTileEntity();
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            rift.unpair();
        }
        unpairedRiftList.put(riftID, location);
    }

    public void setLastChangedRift(DDTileEntityBase origRift) {
        lastBrokenRift = origRift;
    }

    public DDTileEntityBase getLastChangedRift() {
        return lastBrokenRift;
    }

    public boolean teleportEntityToRift(Entity entity, int pairedRiftID) {
        if (pairedRiftID < 0) {
            return false;
        }
        Location destinationRiftLocation = getRiftLocation(pairedRiftID);
        DDTileEntityBase destinationRift = (DDTileEntityBase) destinationRiftLocation.getTileEntity();
        return TeleportHelper.teleport(entity, destinationRift.getTeleportTargetLocation());
    }

    public int getRandomUnpairedRiftID(int origRiftID) {
        if (!unpairedRiftList.isEmpty()) {
            int numberOfUnpairedRifts = unpairedRiftList.keySet().size();
            if (numberOfUnpairedRifts != 1) {//should only be the "original Rift" then
                Random random = new Random();
                List<Integer> keys = new ArrayList(unpairedRiftList.keySet());
                int origRiftKey = keys.indexOf(origRiftID);
                int randomRiftKey = random.nextInt(numberOfUnpairedRifts - 1); //-1 because we do not want to include the key of the original rift, so it will not randomly pair to itself
                if (randomRiftKey >= origRiftKey) {
                    randomRiftKey++;
                }
                return keys.get(randomRiftKey);
            }
        }
        return -1;
    }
}
