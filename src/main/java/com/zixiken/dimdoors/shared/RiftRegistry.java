/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoorChaos;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoorPersonal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author Robijnvogel
 */
public class RiftRegistry {

    private DDTileEntityBase lastBrokenRift = null; //@todo, redo this functionality in a more refined way
    public static final RiftRegistry INSTANCE = new RiftRegistry();

    // Privates
    private int nextRiftID;
    private int maximumDungeonDepth = 2; //@todo make this configurable
    private final Map<Integer, Location> rifts; //maps all rifts in the world to their ID //@todo, make this a List of (comparable) locations?
    private final List<Integer> personalDoors; //list of all personal door rifts in the world, by riftID
    private final List<Integer> unpairedRifts; //list of all pairable rifts in the world that are not paired, by riftID
    private final List<List<Integer>> unpairedRiftsPerDepth; //List of all "unpairedRiftList s" per Dungeon Depth. Depth 0 is anything outside the dungeon dimension
    //@todo somehow remove rifts from these lists even if they are removed in creative

    //when adding any new variables, don't forget to add them to the write and load functions
    // Methods
    private RiftRegistry() {
        nextRiftID = 0;
        rifts = new HashMap();
        personalDoors = new ArrayList(); //@todo read from and write to NBT
        unpairedRifts = new ArrayList();
        unpairedRiftsPerDepth = new ArrayList();
        for (int i = 0; i <= maximumDungeonDepth; i++) {
            unpairedRiftsPerDepth.add(new ArrayList());
        }
    }

    public void reset() {
        nextRiftID = 0;
        rifts.clear();
        personalDoors.clear();
        unpairedRifts.clear();
        for (List<Integer> dimensionSpecificUnpairedRiftList : unpairedRiftsPerDepth) {
            dimensionSpecificUnpairedRiftList.clear();
        }
        lastBrokenRift = null;
        RiftSavedData.get(DimDoors.getDefWorld()).markDirty();
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
                rifts.put(riftID, riftLocation);
            }
        }
        
        if (nbt.hasKey("personalDoorsList")) {
            NBTTagList persRiftsNBT = (NBTTagList) nbt.getTag("personalDoorsList");
            for (int i = 0; i < persRiftsNBT.tagCount(); i++) {
                NBTTagCompound riftTag = persRiftsNBT.getCompoundTagAt(i);
                int riftID = riftTag.getInteger("riftID");
                personalDoors.add(riftID);
            }
        }

        if (nbt.hasKey("unpairedRiftList")) {
            NBTTagList unpRiftsNBT = (NBTTagList) nbt.getTag("unpairedRiftList");
            for (int i = 0; i < unpRiftsNBT.tagCount(); i++) {
                NBTTagCompound riftTag = unpRiftsNBT.getCompoundTagAt(i);
                int riftID = riftTag.getInteger("riftID");
                unpairedRifts.add(riftID);
            }
        }

        if (nbt.hasKey("unpairedDepthRiftList")) {
            unpairedRiftsPerDepth.clear(); //because its "maximum depth" (or in other words, "size()") could be re-determined by this action

            NBTTagList riftListsNBT = (NBTTagList) nbt.getTag("unpairedDepthRiftList");
            maximumDungeonDepth = riftListsNBT.tagCount(); //makes sure both are synched
            for (int i = 0; i < riftListsNBT.tagCount(); i++) {
                unpairedRiftsPerDepth.add(new ArrayList());
                NBTTagList riftsNBT = (NBTTagList) riftListsNBT.get(i);
                for (int j = 0; j < riftsNBT.tagCount(); j++) {
                    NBTTagCompound riftTag = riftsNBT.getCompoundTagAt(j);
                    int riftID = riftTag.getInteger("riftID");
                    unpairedRiftsPerDepth.get(i).add(riftID);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        lastBrokenRift = null; //@todo this really should not be a part of this method, but I do not know of a better way to guaranteedly dereference it once every so often.
        nbt.setInteger("maximumDungeonDepth", maximumDungeonDepth);
        nbt.setInteger("nextUnusedID", nextRiftID);

        NBTTagList riftsNBT = new NBTTagList();
        for (Map.Entry<Integer, Location> entry : rifts.entrySet()) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", entry.getKey());
            riftTag.setTag("location", Location.writeToNBT(entry.getValue()));
            riftsNBT.appendTag(riftTag);
        }
        nbt.setTag("riftList", riftsNBT);

        NBTTagList personalDoorsNBT = new NBTTagList();
        for (int riftID : personalDoors) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", riftID);
            personalDoorsNBT.appendTag(riftTag);
        }
        nbt.setTag("personalDoorsList", personalDoorsNBT);
        
        NBTTagList unpairedRiftsNBT = new NBTTagList();
        for (int riftID : unpairedRifts) {
            NBTTagCompound riftTag = new NBTTagCompound();
            riftTag.setInteger("riftID", riftID);
            unpairedRiftsNBT.appendTag(riftTag);
        }
        nbt.setTag("unpairedRiftList", unpairedRiftsNBT);

        NBTTagList unpairedRiftListsNBT = new NBTTagList();
        for (List<Integer> unpairedRiftListAtDepth : unpairedRiftsPerDepth) {
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
        final int assignedID = nextRiftID;
        DimDoors.log(this.getClass(), "Starting registering rift as ID: " + assignedID);

        rifts.put(assignedID, riftLocation);
        if (rift instanceof TileEntityDimDoorPersonal || rift instanceof TileEntityDimDoorChaos) {
            if (rift instanceof TileEntityDimDoorPersonal) {
                personalDoors.add(assignedID);
            }
        } else {
            DimDoors.log(this.getClass(), "Registering rift in unpairedRiftRegistry. ID = " + assignedID);
            unpairedRifts.add(assignedID);
            registerUnpairedRiftAtDepth(assignedID, depth);
        }

        nextRiftID++;
        RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        return assignedID;
    }

    public void unregisterRift(int riftID) {
        DimDoors.log(this.getClass(), "unregistering rift " + riftID);
        unpair(riftID);

        unRegisterUnpairedRiftAtDepth(riftID); //@todo, will this crash if it doesn't find that value?
        unpairedRifts.remove((Integer) riftID);
        personalDoors.remove((Integer) riftID);
        rifts.remove((Integer) riftID);
        RiftSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
    }

    void registerUnpairedRiftAtDepth(int riftID, int depth) {
        if (depth < maximumDungeonDepth) {
            List<Integer> unpairedRiftListAtDepth = unpairedRiftsPerDepth.get(depth);
            unpairedRiftListAtDepth.add(riftID);
        }
    }

    void unRegisterUnpairedRiftAtDepth(int riftID) {
        TileEntity tileEntity = rifts.get(riftID).getTileEntity();
        if (tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            int depth = rift.getDepth();
            if (depth < maximumDungeonDepth) {
                List<Integer> unpairedRiftListAtDepth = unpairedRiftsPerDepth.get(depth);
                unpairedRiftListAtDepth.remove((Integer) riftID);
            }
        }
    }

    void unRegisterUnpairedRiftAtDepth(DDTileEntityBase rift) {
        int depth = rift.getDepth();
        if (depth < maximumDungeonDepth) {
            List<Integer> unpairedRiftListAtDepth = unpairedRiftsPerDepth.get(depth);
            unpairedRiftListAtDepth.remove((Integer) rift.getRiftID());
        }
    }

    public Location getRiftLocation(int ID) {
        return rifts.get(ID);
    }

    public void pair(int riftID, int riftID2) {
        if (riftID < 0 || riftID2 < 0) {
            return; //@todo throw a proper error
        }
        Location location = rifts.get(riftID);
        TileEntity tileEntity = location.getTileEntity(); //@todo this method might need to be in another class?
        if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
            DimDoors.log(this.getClass(), "RiftRegistry trying to connect rift " + riftID + " to rift " + riftID2 + ".");
            boolean alreadyPaired = rift.pair(riftID2);
            if (!alreadyPaired) {
                DimDoors.log(this.getClass(), "RiftRegistry unregistering rift " + riftID + " from unPairedRiftRegistry.");
                unpairedRifts.remove((Integer) riftID);
                unRegisterUnpairedRiftAtDepth(riftID);
            }
        }
    }

    public void unpair(int riftID) {
        if (riftID < 0) {
            return;
        }
        Location location = rifts.get(riftID);
        if (location == null) {
            DimDoors.warn(this.getClass(), "RiftID with null location: rift " + riftID);
        } else {
            TileEntity tileEntity = location.getTileEntity();
            if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
                DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
                boolean alreadyUnPaired = rift.unpair();
                if (!alreadyUnPaired) {
                    unpairedRifts.add(riftID);
                    registerUnpairedRiftAtDepth(riftID, rift.getDepth());
                }
            }
        }
    }

    public void setLastChangedRift(DDTileEntityBase origRift) {
        lastBrokenRift = origRift;
    }

    public DDTileEntityBase getLastChangedRift() {
        return lastBrokenRift;
    }

    public boolean teleportEntityToRift(Entity entity, int pairedRiftID) { //@todo implement this code in the sending rift tiles instead
        DimDoors.log(this.getClass(), "RiftID of rift that the entity trying to teleport to is " + pairedRiftID + ".");
        if (pairedRiftID < 0) {
            DimDoors.warn(this.getClass(), "RiftID of rift that entity " + entity + " is trying to teleport to seems to be lower than 0 and it shouldn't.");
            return false;
        }
        Location destinationRiftLocation = getRiftLocation(pairedRiftID);
        DDTileEntityBase destinationRift = (DDTileEntityBase) destinationRiftLocation.getTileEntity();
        if (destinationRift == null) {
            DimDoors.warn(this.getClass(), "The rift that an entity is trying to teleport to seems to be null.");
        }
        return TeleporterDimDoors.instance().teleport(entity, destinationRift.getTeleportTargetLocation());
    }

    //@todo are we ever going to use this method?
    public int getRandomUnpairedRiftID(int origRiftID) {
        if (!unpairedRifts.isEmpty()) {
            int numberOfUnpairedRifts = unpairedRifts.size();
            if (numberOfUnpairedRifts != 1) {//should not only be the "original Rift" then
                Random random = new Random();
                int indexOforigRiftID = unpairedRifts.indexOf(origRiftID);
                int randomRiftIDIndex = random.nextInt(numberOfUnpairedRifts - 1); //-1 because we do not want to include the key of the original rift, so it will not randomly pair to itself
                if (randomRiftIDIndex >= indexOforigRiftID) {
                    randomRiftIDIndex++;
                }
                return unpairedRifts.get(randomRiftIDIndex);
            }
        }
        return -1;
    }

    public int getRandomUnpairedRiftIDAtDepth(int origRiftID, int depth) {
        int returnID = -1;
        if (unpairedRiftsPerDepth.size() > depth) {
            List<Integer> rifts = unpairedRiftsPerDepth.get(depth);
            int numberOfUnpairedRifts = rifts.size();
            if (numberOfUnpairedRifts > 1) {
                DimDoors.log(this.getClass(), "There's more than 1 unpaired rift at this depth.");
                Random random = new Random();
                int indexOforigRiftID = -1;
                int randomRiftIDIndex;
                boolean origRiftIsOnSameDepth = rifts.contains(origRiftID);
                if (origRiftIsOnSameDepth) {
                    indexOforigRiftID = rifts.indexOf(origRiftID);
                    randomRiftIDIndex = random.nextInt(numberOfUnpairedRifts - 1); //-1 because we do not want to include the key of the original rift, so it will not randomly pair to itself
                    if (randomRiftIDIndex >= indexOforigRiftID) {
                        randomRiftIDIndex++;
                    }
                } else {
                    randomRiftIDIndex = random.nextInt(numberOfUnpairedRifts);
                }
                returnID = rifts.get(randomRiftIDIndex);
            }
        }
        DimDoors.log(this.getClass(), "Rift to pair to chosen: returnID = " + returnID);
        return returnID;
    }

    public int getRandomUnpairedRiftIDAroundDepth(int origRiftID, int depth) {
        int returnID = -1;
        if (unpairedRiftsPerDepth.size() > depth) {
            int[] weights = getWeightSizeProducts(unpairedRiftsPerDepth, depth - 2, new int[]{15, 25, 30, 20, 10});
            if (getArraySum(weights) == 0) {
                //@todo there is no unpaired rift around that depth
            } else {
                int chosenDepth = pickRandom(weights) + depth - 2;
                returnID = getRandomUnpairedRiftIDAtDepth(origRiftID, chosenDepth);
            }
        }
        return returnID;
    }

    public int[] getWeightSizeProducts(List<List<Integer>> nestedList, int minListIndex, int[] weights) { //@todo put this in a utility class
        int[] returnArray = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            int listIndex = minListIndex + i;
            if (listIndex > 0 && listIndex < nestedList.size()) {
                returnArray[i] = nestedList.get(listIndex).size() * weights[i];
            } else {
                returnArray[i] = 0;
            }
        }
        return returnArray;
    }

    private int getArraySum(int[] integers) { //@todo put this in a utility class
        int returnValue = 0;
        for (int i : integers) {
            returnValue += i;
        }
        return returnValue;
    }

    private int pickRandom(int[] integers) { //@todo put this in a utility class
        Random random = new Random();
        int pointer = random.nextInt(getArraySum(integers));
        for (int i = 0; i < integers.length; i++) {
            if (pointer < integers[i]) {
                return i;
            }
            pointer -= integers[i];
        }
        return -1; //should not be reachable if implementation is correct and getArraySum(integers) does not return 0
    }

    public int getRandomNonPersonalRiftID() {
        List<Integer> nonPersonalRiftIDs = new ArrayList(rifts.keySet());
        for (int persRiftID : personalDoors) {
            //DimDoors.log(this.getClass(), "Removing personal rift: " + persRiftID + " from nonPersonalRiftIDs. nPRI size = " + nonPersonalRiftIDs.size());
            nonPersonalRiftIDs.remove((Integer) persRiftID);
            //DimDoors.log(this.getClass(), "Removed personal rift: " + persRiftID + " from nonPersonalRiftIDs. nPRI size = " + nonPersonalRiftIDs.size());
        }
        if (nonPersonalRiftIDs.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(nonPersonalRiftIDs.size());
            return nonPersonalRiftIDs.get(index);
        }
        return -1;
    }

    public Location getTeleportLocation(int riftId) {
        if (riftId < 0) {
            DimDoors.warn(this.getClass(), "RiftID of rift that entity is trying to teleport to seems to be lower than 0 and it shouldn't.");
            return null;
        }
        Location destinationRiftLocation = getRiftLocation(riftId);
        DDTileEntityBase destinationRift = (DDTileEntityBase) destinationRiftLocation.getTileEntity();
        if (destinationRift == null) {
            DimDoors.warn(this.getClass(), "The rift that an entity is trying to teleport to seems to be null. RiftID: " + riftId + ". Expecting to crash in 3... 2... 1..");
        }
        return destinationRift.getTeleportTargetLocation();
    }

    public void validatePlayerPocketEntry(Entity entity, int riftID) {
        if (entity instanceof EntityPlayer && riftID >= 0) {
            Location riftLocation = getRiftLocation(riftID);
            if (riftLocation != null) {
                TileEntity tileEntity = riftLocation.getTileEntity();
                if (tileEntity != null && tileEntity instanceof DDTileEntityBase) {
                    DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
                    EntityPlayer player = (EntityPlayer) entity;
                    rift.validatePlayerPocketEntry(player);
                }
            }
        }
    }

    public void unregisterLastChangedRift() {
        if (lastBrokenRift != null) {
            RiftRegistry.INSTANCE.unregisterRift(lastBrokenRift.getRiftID());
            //@todo The rest is all pretty Crude. The only reason why this is needed, is because Vanilla Minecraft keeps destroying the rift blocks, before they can place down their TileEntities, if a player breaks them in creative.
            RiftRegistry.INSTANCE.unRegisterUnpairedRiftAtDepth(lastBrokenRift);
            lastBrokenRift.unpair();
            lastBrokenRift = null;
        }
    }

    public int getPocketID(int riftID) {
        DDTileEntityBase rift = (DDTileEntityBase) rifts.get(riftID).getTileEntity();
        return rift.getPocketID();
    }
}
