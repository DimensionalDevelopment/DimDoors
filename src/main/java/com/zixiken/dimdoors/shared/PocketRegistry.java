/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.util.DDRandomUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 *
 * @author Robijnvogel
 */
public class PocketRegistry {

    public static final PocketRegistry INSTANCE = new PocketRegistry();

    // Privates
    //Need to be saved:
    private int gridSize; //determines how much pockets in their dimension are spaced
    private int maxPocketSize;
    private int privatePocketSize;
    private int publicPocketSize;
    private final Map<EnumPocketType, Integer> nextUnusedIDs;
    private final Map<String, Integer> privatePockets; //maps the UUID's of players to their private pocket's ID (ID for EnumPocketType.PRIVATE in pocketLists)
    private final Map<EnumPocketType, Map<Integer, Pocket>> pocketLists;
    private final List<Map<Integer, Pocket>> pocketListsPerDepth; //@todo not being used or saved yet.

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
            if (nbt.hasKey("nextUnusedIDs")) {
                NBTTagCompound nextUnusedIDTagCompound = nbt.getCompoundTag("nextUnusedIDs");
                for (EnumPocketType pocketType : EnumPocketType.values()) {
                    String tagListName = pocketType.toString();
                    if (nextUnusedIDTagCompound.hasKey(tagListName)) {
                        int nextUnusedID = nextUnusedIDTagCompound.getInteger(tagListName);
                        nextUnusedIDs.put(pocketType, nextUnusedID);
                    }
                }
            }
            if (nbt.hasKey("privatePockets")) {
                NBTTagCompound privatePocketsTagCompound = nbt.getCompoundTag("privatePockets");
                privatePockets.clear();
                for (String UUID : privatePocketsTagCompound.getKeySet()) {
                    privatePockets.put(UUID, privatePocketsTagCompound.getInteger(UUID));
                }
            }
            if (nbt.hasKey("pocketData")) {
                NBTTagCompound pocketsTagCompound = nbt.getCompoundTag("pocketData");
                pocketLists.clear();
                for (EnumPocketType pocketType : EnumPocketType.values()) {
                    String tagListName = pocketType.toString();
                    if (pocketsTagCompound.hasKey(tagListName)) {
                        Map<Integer, Pocket> pocketList = new HashMap();
                        NBTTagList pocketTagList = (NBTTagList) pocketsTagCompound.getTag(tagListName);
                        for (int j = 0; j < pocketTagList.tagCount(); j++) { //@todo this defeats the purpose of a Map over a List (pocketList)
                            NBTTagCompound pocketTag = pocketTagList.getCompoundTagAt(j);
                            pocketList.put(j, Pocket.readFromNBT(pocketTag));
                        }
                        pocketLists.put(pocketType, pocketList);
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

        NBTTagCompound nextUnusedIDTagCompound = new NBTTagCompound();
        for (EnumPocketType pocketType : nextUnusedIDs.keySet()) {
            nextUnusedIDTagCompound.setInteger(pocketType.toString(), nextUnusedIDs.get(pocketType));
        }
        nbt.setTag("nextUnusedIDs", nextUnusedIDTagCompound);

        NBTTagCompound privatePocketsTagCompound = new NBTTagCompound();
        for (String UUID : privatePockets.keySet()) {
            privatePocketsTagCompound.setInteger(UUID, privatePockets.get(UUID));
        }
        nbt.setTag("privatePockets", privatePocketsTagCompound);

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

    public void registerNewPocket(Pocket pocket, EnumPocketType pocketType) {
        int assignedID = nextUnusedIDs.get(pocketType);
        pocketLists.get(pocketType).put(assignedID, pocket);
        pocket.setID(assignedID);

        nextUnusedIDs.put(pocketType, assignedID + 1); //increase the counter
        PocketSavedData.get(DimDoors.getDefWorld()).markDirty(); //Notify that this needs to be saved on world save
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

    public int getEntranceDoorIDOfNewPocket(EnumPocketType typeID, int depth, Location origRiftLocation) {//should return the riftID of the entrance door of the newly generated pocket
        Pocket pocket = generateRandomPocketAt(typeID, depth, origRiftLocation);
        int entranceDoorID = pocket.getEntranceDoorID();
        return entranceDoorID;
    }

    private Pocket generateRandomPocketAt(EnumPocketType typeID, int depth, Location origRiftLocation) {
        //Correcting the depth. Just in case...
        if (typeID == EnumPocketType.DUNGEON) {
            if (depth <= 0) {
                depth = 1;
            } else if (depth > DDConfig.getMaxDungeonDepth()) {
                depth = DDConfig.getMaxDungeonDepth();
            }
        } else {
            depth = 0;
        }

        //Fetching the pocket template
        PocketTemplate pocketTemplate = getRandomPocketTemplate(typeID, depth, maxPocketSize);

        return generatePocketAt(typeID, depth, origRiftLocation, pocketTemplate);
    }

    public Pocket generatePocketAt(EnumPocketType typeID, int depth, Location origRiftLocation, PocketTemplate pocketTemplate) {
        //Getting the physical grid-location and the Overworld coordinates
        Location shortenedLocation = getGenerationlocation(nextUnusedIDs.get(typeID), typeID);
        int shortenedX = shortenedLocation.getPos().getX();
        int shortenedZ = shortenedLocation.getPos().getZ();
        int dimID = shortenedLocation.getDimensionID();
        Location depthZeroLocation;
        if (typeID == EnumPocketType.DUNGEON) {
            depthZeroLocation = DDRandomUtils.transformLocationRandomly(DDConfig.getOwCoordinateOffsetBase(), DDConfig.getOwCoordinateOffsetPower(), depth, origRiftLocation);
        } else if (typeID == EnumPocketType.PUBLIC) {
            depthZeroLocation = DDRandomUtils.transformLocationRandomly(DDConfig.getOwCoordinateOffsetBase(), DDConfig.getOwCoordinateOffsetPower(), 1, origRiftLocation);
        } else { //PRIVATE
            depthZeroLocation = origRiftLocation;
        }

        Pocket pocket = pocketTemplate.place(shortenedX, 0, shortenedZ, gridSize, dimID, nextUnusedIDs.get(typeID), depth, typeID, depthZeroLocation);
        registerNewPocket(pocket, typeID);
        return pocket;
    }

    public int getPrivateDimDoorID(String playerUUID) {
        if (!privatePockets.containsKey(playerUUID)) {
            //generate a new private pocket
            int doorID = getEntranceDoorIDOfNewPocket(EnumPocketType.PRIVATE, 0, new Location(0, 0, 0, 0)); //Location doesn't really matter in this case
            privatePockets.put(playerUUID, doorID);
            return doorID;
        }
        return privatePockets.get(playerUUID);
    }

    private Location getGenerationlocation(final int nextUnusedID, EnumPocketType typeID) {
        int x;
        int y = 0;
        int z;
        int dimID = DimDoorDimensions.getPocketDimensionType(typeID).getId();
        if (nextUnusedID == 0) {
            x = 0;
            z = 0;
        } else {
            int radius = (int) Math.sqrt(nextUnusedID); //casting to int rounds down the double resulting from taking the square root
            int radiusNumber = nextUnusedID - (radius * radius);
            double splitter = ((double) radiusNumber) / ((double) radius); //always between 0 and 2
            DimDoors.log(this.getClass(), "id is " + nextUnusedID);
            DimDoors.log(this.getClass(), "Radius is " + radius);
            DimDoors.log(this.getClass(), "Radius number is " + radiusNumber);
            DimDoors.log(this.getClass(), "Splitter is " + splitter);
            x = splitter <= 1.0 ? radius : radius - (radiusNumber - radius);
            z = splitter >= 1.0 ? radius : radiusNumber;
        }
        Location location = new Location(dimID, x, y, z);
        return location;
    }

    private PocketTemplate getRandomPocketTemplate(EnumPocketType typeID, int depth, int maxPocketSize) {
        switch (typeID) {
            case PRIVATE:
                return SchematicHandler.INSTANCE.getPersonalPocketTemplate();
            case PUBLIC:
                return SchematicHandler.INSTANCE.getPublicPocketTemplate();
            case DUNGEON:
            default:
                return SchematicHandler.INSTANCE.getRandomDungeonPocketTemplate(depth, maxPocketSize);
        }
    }

    /*
    private int getSimpleX(int ID) {
        //@todo check for smaller than 0
        if (ID == 0) {
            return 0;
        } else {
            int baseX = getSimpleX(ID - 1);
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

    private int getSimpleZ(int ID) {
        if (ID == 0) {
            return 0;
        } else {
            int baseZ = getSimpleZ(ID - 1);
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
     */
    public int getPocketIDFromCoords(Location location) {
        final int dimID = location.getDimensionID();
        if (DimDoorDimensions.isPocketDimensionID(dimID)) {
            int x = location.getPos().getX();
            int z = location.getPos().getZ();
            int shortX = x / (gridSize * 16);
            int shortZ = z / (gridSize * 16);
            if (shortX >= shortZ) {
                return shortX * shortX + shortZ;
            } else {
                return (shortZ + 2) * shortZ - shortX;
            }
        }
        return -1; //not in a pocket dimension
    }

    public boolean isPlayerAllowedToBeHere(final EntityPlayerMP player, final Location location) {
        if (player.isCreative()) {
            return true;
        } else {
            int pocketID = getPocketIDFromCoords(location);
            if (pocketID < 0) { //not in a pocket dimension
                return true;
            } else {
                EnumPocketType type = DimDoorDimensions.getPocketType(location.getDimensionID());
                Pocket pocket = pocketLists.get(type).get(pocketID);
                if (pocket.isPlayerAllowedInPocket(player) && pocket.isLocationWithinPocketBounds(location, gridSize)) {
                    return true;
                }
                return false;
            }
        }
    }
}
