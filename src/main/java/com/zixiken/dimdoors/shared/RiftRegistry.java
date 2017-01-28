/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author Robijnvogel
 */
public class RiftRegistry {

    private DDTileEntityBase lastBrokenRift = null; //@todo, redo this functionality in a more refined way
    public static final RiftRegistry Instance = new RiftRegistry();

    // Privates
    private int nextRiftID;
    private int maximumDungeonDepth = 2; //@todo make this configurable
    private final Map<Integer, Location> riftList; //maps all rifts in the world to their ID //@todo, make this a List of (comparable) locations?
    //@todo somehow remove rifts from this list even if they are removed in creative
    private final List<Integer> unpairedRiftList; //maps of all rifts in the world that are not paired to their ID
    private final List<List<Integer>> unpairedDepthRiftList; //List of all "unpairedRiftList s" per Dungeon Depth. Depth 0 is almost anything outside the dungeon dimension
    //@todo, once we have a dungeon dimension this List should be implemented (for determining what doors an unpaired door can link to)
    //when adding any new variables, don't forget to add them to the write and load functions

    // Methods
    private RiftRegistry() {
        nextRiftID = 0;
        riftList = new HashMap();
        unpairedRiftList = new ArrayList();
        unpairedDepthRiftList = new ArrayList();
        for (int i = 0; i < maximumDungeonDepth; i++) {
            unpairedDepthRiftList.add(new ArrayList());
        }
    }

    public void reset() {
        nextRiftID = 0;
        riftList.clear();
        unpairedRiftList.clear();
        for (List<Integer> dimensionSpecificUnpairedRiftList : unpairedDepthRiftList) {
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
                unpairedRiftList.add(riftID);
            }
        }

        if (nbt.hasKey("unpairedDepthRiftList")) {
            unpairedDepthRiftList.clear(); //because its "maximum depth" (or in other words, "size()") could be re-determined by this action

            NBTTagList riftListsNBT = (NBTTagList) nbt.getTag("unpairedDepthRiftList");
            maximumDungeonDepth = riftListsNBT.tagCount(); //makes sure both are synched
            for (int i = 0; i < riftListsNBT.tagCount(); i++) {
                unpairedDepthRiftList.add(new ArrayList());
                NBTTagList riftsNBT = (NBTTagList) riftListsNBT.get(i);
                for (int j = 0; j < riftsNBT.tagCount(); j++) {
                    NBTTagCompound riftTag = riftsNBT.getCompoundTagAt(j);
                    int riftID = riftTag.getInteger("riftID");
                    unpairedDepthRiftList.get(i).add(riftID);
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
        for (int riftID : unpairedRiftList) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", riftID);
            unpairedRiftsNBT.appendTag(riftTag);
        }
        nbt.setTag("unpairedRiftList", unpairedRiftsNBT);

        NBTTagList unpairedRiftListsNBT = new NBTTagList();
        for (List<Integer> unpairedRiftListAtDepth : unpairedDepthRiftList) {
            NBTTagList unpairedRiftsNBT2 = new NBTTagList();
            for (int riftID : unpairedRiftListAtDepth) {
                NBTTagCompound riftTag = new NBTTagCompound();
                riftTag.setInteger("riftID", riftID);
                unpairedRiftsNBT2.appendTag(riftTag);
            }
            unpairedRiftListsNBT.appendTag(unpairedRiftsNBT2);
        }
        nbt.setTag("unpairedDepthRiftList", unpairedRiftListsNBT);
    }

    public int registerNewRift(DDTileEntityBase rift, int depth) {
        Location riftLocation = Location.getLocation(rift);
        riftList.put(nextRiftID, riftLocation);
        unpairedRiftList.add(nextRiftID);
        registerRiftAtDepth(nextRiftID, depth);
        DimDoors.log(this.getClass(), "Rift registered as ID: " + nextRiftID);
        nextRiftID++;
        RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        return nextRiftID - 1;
    }

    public void unregisterRift(int riftID) {
        if (riftList.containsKey(riftID)) {
            unpair(riftID);
            riftList.remove(riftID);
            unpairedRiftList.remove((Integer) riftID);
            unRegisterRiftAtDepth(riftID);
            RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        }
    }

    void registerRiftAtDepth(int riftID, int depth) {
        if (depth < maximumDungeonDepth) {
            List<Integer> unpairedRiftListAtDepth = unpairedDepthRiftList.get(depth);
            unpairedRiftListAtDepth.add(riftID);
        }
    }

    void unRegisterRiftAtDepth(int riftID) {
        TileEntity tileEntity = riftList.get(riftID).getTileEntity();
        if (tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            int depth = rift.getDepth();
            if (depth < maximumDungeonDepth) {
                List<Integer> unpairedRiftListAtDepth = unpairedDepthRiftList.get(depth);
                unpairedRiftListAtDepth.remove((Integer) riftID);
            }
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
            DimDoors.log(this.getClass(), "RiftRegistry trying to connect rift " + riftID + " to rift " + riftID2 + ".");
            boolean alreadyPaired = rift.pair(riftID2);
            if (!alreadyPaired) {
                DimDoors.log(this.getClass(), "RiftRegistry unregistering rift " + riftID + " from unPairedRiftRegistry.");
                unpairedRiftList.remove((Integer) riftID);
                //@todo remove the riftID from the depth list as well
            }
        }
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
            boolean alreadyUnPaired = rift.unpair();
            if (!alreadyUnPaired) {
                unpairedRiftList.add(riftID);
                //@todo add the riftID from the depth list as well, maybe move this to the tileEntityRift class itself though?
            }
        }
    }

    public void setLastChangedRift(DDTileEntityBase origRift) {
        lastBrokenRift = origRift;
    }

    public DDTileEntityBase getLastChangedRift() {
        return lastBrokenRift;
    }

    public boolean teleportEntityToRift(Entity entity, int pairedRiftID) {
        DimDoors.log(this.getClass(), "RiftID of rift that the entity trying to teleport to is " + pairedRiftID + ".");
        if (pairedRiftID < 0) {
            DimDoors.warn(this.getClass(), "RiftID of rift that entity trying to teleport to seems to be lower than 0 and it shouldn't.");
            return false;
        }
        Location destinationRiftLocation = getRiftLocation(pairedRiftID);
        DDTileEntityBase destinationRift = (DDTileEntityBase) destinationRiftLocation.getTileEntity();
        if (destinationRift == null) {
            DimDoors.warn(this.getClass(), "The rift that an entity is trying to teleport to seems to be null.");
        }
        return TeleportHelper.teleport(entity, destinationRift.getTeleportTargetLocation());
    }

    public int getRandomUnpairedRiftID(int origRiftID) {
        if (!unpairedRiftList.isEmpty()) {
            int numberOfUnpairedRifts = unpairedRiftList.size();
            if (numberOfUnpairedRifts != 1) {//should only be the "original Rift" then
                Random random = new Random();
                int indexOforigRiftID = unpairedRiftList.indexOf(origRiftID);
                int randomRiftIDIndex = random.nextInt(numberOfUnpairedRifts - 1); //-1 because we do not want to include the key of the original rift, so it will not randomly pair to itself
                if (randomRiftIDIndex >= indexOforigRiftID) {
                    randomRiftIDIndex++;
                }
                return unpairedRiftList.get(randomRiftIDIndex);
            }
        }
        return -1;
    }
}
