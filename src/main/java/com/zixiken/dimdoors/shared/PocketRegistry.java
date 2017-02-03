/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

/**
 *
 * @author Robijnvogel
 */
public class PocketRegistry {

    public static final PocketRegistry Instance = new PocketRegistry();

    // Privates
    private int gridSize; //determines how much pockets in their dimension are spaced
    private int maxPocketSize;
    private int privatePocketSize;
    private int publicPocketSize;
    private final Map<EnumPocketType, Integer> nextUnusedIDs;
    private final Map<String, Integer> privatePockets; //maps the UUID's of players to their private pocket's ID (ID for EnumPocketType.PRIVATE in pocketLists)
    private final Map<EnumPocketType, Map<Integer, Pocket>> pocketLists;
    //when adding any new variables, don't forget to add them to the write and load functions
    private final List<Map<Integer, Pocket>> pocketListsPerDepth;

    // Methods
    private PocketRegistry() {
        nextUnusedIDs = new HashMap();
        for (EnumPocketType pocketType : EnumPocketType.values()) {
            nextUnusedIDs.put(pocketType, 0);
        }
        privatePockets = new HashMap();
        pocketLists = new HashMap();
        for (EnumPocketType pocketType : EnumPocketType.values()) {
            pocketLists.put(pocketType, new HashMap());
        }
        pocketListsPerDepth = new ArrayList();
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getMaxPocketSize() {
        return maxPocketSize;
    }

    public int getPrivatePocketSize() {
        return privatePocketSize;
    }

    public int getPublicPocketSize() {
        return publicPocketSize;
    }

    public void reset() {
        for (EnumPocketType pocketType : EnumPocketType.values()) {
            nextUnusedIDs.put(pocketType, 0);
        }
        for (EnumPocketType pocketType : EnumPocketType.values()) {
            pocketLists.get(pocketType).clear();
        }
        gridSize = DDConfig.getPocketGridSize();
        maxPocketSize = DDConfig.getMaxPocketsSize();
        privatePocketSize = DDConfig.getPrivatePocketSize();
        publicPocketSize = DDConfig.getPublicPocketSize();
        PocketSavedData.get(DimDoors.getDefWorld()).markDirty();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("gridSize")) { //if this info has been saved before
            gridSize = nbt.getInteger("gridSize");
            maxPocketSize = nbt.getInteger("maxPocketSize");
            privatePocketSize = nbt.getInteger("privatePocketSize");
            publicPocketSize = nbt.getInteger("publicPocketSize");
            if (nbt.hasKey("nextUnusedIDs")) { //@todo should not be doing this, since all pockets re-register on world-load
                NBTTagCompound nextUnusedIDTagCompound = nbt.getCompoundTag("nextUnusedIDs");
                for (EnumPocketType pocketType : EnumPocketType.values()) {
                    String tagListName = pocketType.toString();
                    if (nextUnusedIDTagCompound.hasKey(tagListName)) {
                        int nextUnusedID = nextUnusedIDTagCompound.getInteger(tagListName);
                        nextUnusedIDs.put(pocketType, nextUnusedID);
                    }
                }
            }
            if (nbt.hasKey("pocketData")) {
                NBTTagCompound pocketsTagCompound = nbt.getCompoundTag("pocketData");
                for (EnumPocketType pocketType : EnumPocketType.values()) {
                    String tagListName = pocketType.toString();
                    if (pocketsTagCompound.hasKey(tagListName)) {
                        NBTTagList pocketTagList = (NBTTagList) pocketsTagCompound.getTag(tagListName);
                        for (int j = 0; j < pocketTagList.tagCount(); j++) {
                            NBTTagCompound pocketTag = pocketTagList.getCompoundTagAt(j);
                            Pocket.readFromNBT(pocketTag); //this also re-registers the pocket @todo, should it?
                        }
                    }
                }
            }
        } else { //load privates from config
            reset();
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("gridSize", gridSize);
        nbt.setInteger("maxPocketSize", maxPocketSize);
        nbt.setInteger("privatePocketSize", privatePocketSize);
        nbt.setInteger("publicPocketSize", publicPocketSize);

        NBTTagCompound nextUnusedIDTagCompound = new NBTTagCompound(); //@todo do not have to do this, since all pockets re-register on world-load
        for (EnumPocketType pocketType : nextUnusedIDs.keySet()) {
            nextUnusedIDTagCompound.setInteger(pocketType.toString(), nextUnusedIDs.get(pocketType));
        }
        nbt.setTag("nextUnusedIDs", nextUnusedIDTagCompound);

        NBTTagCompound pocketsTagCompound = new NBTTagCompound();
        for (EnumPocketType pocketType : pocketLists.keySet()) {
            Map<Integer, Pocket> pocketList = pocketLists.get(pocketType);
            NBTTagList pocketTagList = new NBTTagList();
            for (int i : pocketList.keySet()) {
                pocketTagList.appendTag(Pocket.writeToNBT(pocketList.get(i)));
            }
            pocketsTagCompound.setTag(pocketType.toString(), pocketTagList);
        }
        nbt.setTag("pocketData", pocketsTagCompound);
    }

    public int registerNewPocket(Pocket pocket, EnumPocketType pocketType) {
        pocketLists.get(pocketType).put(nextUnusedIDs.get(pocketType), pocket);
        pocket.setID(nextUnusedIDs.get(pocketType));

        nextUnusedIDs.put(pocketType, nextUnusedIDs.get(pocketType) + 1);
        PocketSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        return nextUnusedIDs.get(pocketType) - 1;
    }

    public void removePocket(int pocketID, EnumPocketType pocketType) { //probably will never ever get used, but meh...
        Map<Integer, Pocket> pocketList = pocketLists.get(pocketType);
        if (pocketList.containsKey(pocketID)) {
            pocketList.remove(pocketID);
            PocketSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
        }
    }

    public Pocket getPocket(int ID, EnumPocketType pocketType) {
        return pocketLists.get(pocketType).get(ID);
    }

    public int getEntranceDoorIDOfNewPocket(EnumPocketType typeID, int depth) {//should return the riftID of the entrance door of the newly generated pocket
        Location shortenedLocation = getGenerationlocation(nextUnusedIDs.get(typeID), typeID); //@todo, we should have different values of "nextUnusedID"  for different pocket-types
        int x = shortenedLocation.getPos().getX();
        int z = shortenedLocation.getPos().getZ();
        Pocket pocket = generateRandomPocketAt(typeID, depth, shortenedLocation); //registers the pocket as well
        int entranceDoorID = pocket.getEntranceDoorID();
        return entranceDoorID;
    }

    private Pocket generateRandomPocketAt(EnumPocketType typeID, int depth, Location shortenedLocation) {
        int shortenedX = shortenedLocation.getPos().getX();
        int shortenedZ = shortenedLocation.getPos().getZ();
        int dimID = shortenedLocation.getDimensionID();

        PocketTemplate pocketTemplate = getRandomPocketTemplate(typeID, depth, maxPocketSize);

        Pocket pocket = pocketTemplate.place(shortenedX, 0, shortenedZ, gridSize, dimID, nextUnusedIDs.get(typeID), depth, typeID);
        return pocket;
    }

    public int getPrivateDimDoorID(String playerUUID) {
        throw new UnsupportedOperationException("Not supported yet."); //@todo
    }

    private Location getGenerationlocation(int nextUnusedID, EnumPocketType typeID) { //typeID is for determining the dimension
        int x = getSimpleX(nextUnusedID, typeID);
        int y = 0;
        int z = getSimpleZ(nextUnusedID, typeID);;
        int dimID = 0; //@todo should be fetched using typeID

        Location location = new Location(x, y, z, dimID);
        return location;
    }

    private PocketTemplate getRandomPocketTemplate(EnumPocketType typeID, int depth, int maxPocketSize) {
        switch (typeID) {
            case PRIVATE:
                return SchematicHandler.Instance.getPersonalPocketSchematic(maxPocketSize);
            case PUBLIC:
                return SchematicHandler.Instance.getPublicPocketSchematic(maxPocketSize);
            case DUNGEON:
            default:
                return SchematicHandler.Instance.getRandomDungeonPocketTemplate(depth, maxPocketSize);
        }
    }

    private int getSimpleX(int ID, EnumPocketType typeID) { //we can get the previous x from the last entry of the PocketRegistry :D
        if (ID == 0) {
            return 0;
        } else {
            int baseX = pocketLists.get(typeID).get(ID - 1).getX();
            int group = getDiffToPreviousGroup(ID);
            if (group % 2 == 0) {//even
                return baseX;
            } else { //uneven
                if (group % 4 == 1) { //power of four + 1
                    return baseX + 1;
                } else { //power of four - 1
                    return baseX - 1;
                }
            }
        }
    }

    private int getSimpleZ(int ID, EnumPocketType typeID) {
        if (ID == 0) {
            return 0;
        } else {
            int baseZ = pocketLists.get(typeID).get(ID - 1).getZ();
            int group = getDiffToPreviousGroup(ID);
            if (group % 2 == 1) {//uneven
                return baseZ;
            } else { //uneven
                if (group % 4 == 0) { //power of four
                    return baseZ - 1;
                } else { //"4-uneven"
                    return baseZ + 1;
                }
            }
        }
    }

    private static int getDiffToPreviousGroup(int ID) {
        int temp = 0;
        int group;
        for (group = 1; temp <= ID; group++) {
            temp += group * 2;
        }
        if (temp - group < ID) {
            group *= 2;
        } else {
            group = (group * 2) - 1;
        }
        return group;
    }
}
