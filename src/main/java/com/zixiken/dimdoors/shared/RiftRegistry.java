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
import net.minecraft.nbt.NBTTagList;
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
    private int maximumDungeonDepth = 2;
    private final Map<Integer, Location> riftList; //maps all rifts in the world to their ID //@todo, make this a List of (comparable) locations?
    //@todo somehow remove rifts from this list even if they are removed in creative
    private final Map<Integer, Location> unpairedRiftList; //maps of all rifts in the world that are not paired to their ID
    private final List<Map<Integer, Location>> unpairedDepthRiftList; //List of all "unpairedRiftList s" per Dungeon Depth. Depth 0 is almost anything outside the dungeon dimension
    //@todo, once we have a dungeon dimension this List should be implemented (for determining what doors an unpaired door can link to)
    //when adding any new variables, don't forget to add them to the write and load functions

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

        if (nbt.hasKey("riftList")) {
            NBTTagList riftsNBT = (NBTTagList) nbt.getTag("riftList");
            for (int i = 0; i < riftsNBT.tagCount(); i++) {
                NBTTagCompound riftTag = riftsNBT.getCompoundTagAt(i);
                int riftID = riftTag.getInteger("riftID");
                NBTTagCompound locationTag = riftTag.getCompoundTag("location");
                Location riftLocation = Location.readFromNBT(locationTag);
                riftList.put(riftID, riftLocation);
            }
        }

        if (nbt.hasKey("unpairedRiftList")) {
            NBTTagList riftsNBT = (NBTTagList) nbt.getTag("unpairedRiftList");
            for (int i = 0; i < riftsNBT.tagCount(); i++) {
                NBTTagCompound riftTag = riftsNBT.getCompoundTagAt(i);
                int riftID = riftTag.getInteger("riftID");
                NBTTagCompound locationTag = riftTag.getCompoundTag("location");
                Location riftLocation = Location.readFromNBT(locationTag);
                unpairedRiftList.put(riftID, riftLocation);
            }
        }

        if (nbt.hasKey("unpairedDepthRiftList")) {
            unpairedDepthRiftList.clear(); //because its "maximum depth" (or in other words, "size()") could be re-determined by this action

            NBTTagList riftListsNBT = (NBTTagList) nbt.getTag("unpairedDepthRiftList");
            maximumDungeonDepth = riftListsNBT.tagCount(); //makes sure both are synched
            for (int i = 0; i < riftListsNBT.tagCount(); i++) {
                unpairedDepthRiftList.add(new HashMap());
                NBTTagList riftsNBT = (NBTTagList) riftListsNBT.get(i);
                for (int j = 0; j < riftsNBT.tagCount(); j++) {
                    NBTTagCompound riftTag = riftsNBT.getCompoundTagAt(j);
                    int riftID = riftTag.getInteger("riftID");
                    NBTTagCompound locationTag = riftTag.getCompoundTag("location");
                    Location riftLocation = Location.readFromNBT(locationTag);
                    unpairedDepthRiftList.get(i).put(riftID, riftLocation);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("maximumDungeonDepth", maximumDungeonDepth);
        nbt.setInteger("nextUnusedID", nextRiftID);

        NBTTagList riftsNBT = new NBTTagList();
        for (Map.Entry<Integer, Location> entry : riftList.entrySet()) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", entry.getKey());
            riftTag.setTag("location", Location.writeToNBT(entry.getValue()));
            riftsNBT.appendTag(riftTag);
        }
        nbt.setTag("riftList", riftsNBT);

        NBTTagList unpairedRiftsNBT = new NBTTagList();
        for (Map.Entry<Integer, Location> entry : unpairedRiftList.entrySet()) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", entry.getKey());
            riftTag.setTag("location", Location.writeToNBT(entry.getValue()));
            unpairedRiftsNBT.appendTag(riftTag);
        }
        nbt.setTag("unpairedRiftList", unpairedRiftsNBT);

        NBTTagList unpairedRiftListsNBT = new NBTTagList();
        for (Map<Integer, Location> arrayEntry : unpairedDepthRiftList) {
            NBTTagList unpairedRiftsNBT2 = new NBTTagList();
            for (Map.Entry<Integer, Location> mapEntry : arrayEntry.entrySet()) {
                NBTTagCompound riftTag = new NBTTagCompound();
                riftTag.setInteger("riftID", mapEntry.getKey());
                riftTag.setTag("location", Location.writeToNBT(mapEntry.getValue()));
                unpairedRiftsNBT2.appendTag(riftTag);
            }
            unpairedRiftListsNBT.appendTag(unpairedRiftsNBT2);
        }
        nbt.setTag("unpairedDepthRiftList", unpairedRiftListsNBT);
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

    public void unregisterRift(int riftID) {
        if (riftList.containsKey(riftID)) {
            unpair(riftID);
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
