package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.util.NBTUtils;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class PocketRegistry extends WorldSavedData {

    private static final String DATA_NAME = DimDoors.MODID + "_pockets";
    @Getter private static final int DATA_VERSION = 0; // IMPORTANT: Update this and upgradeRegistry when making changes.

    @Getter private int gridSize; // Determines how much pockets in their dimension are spaced
    @Getter private int maxPocketSize;
    @Getter private int privatePocketSize;
    @Getter private int publicPocketSize;
    private Map<String, Integer> privatePocketMap; // Player UUID -> Pocket ID, in pocket dim only
    @Getter private Map<Integer, Pocket> pockets; // TODO: remove getter?
    @Getter private int nextID;

    @Getter private int dimID;

    public PocketRegistry() {
        super(DATA_NAME);
    }

    public PocketRegistry(String s) {
        super(s);
    }

    public static PocketRegistry getForDim(int dimID) {
        if (!DimDoorDimensions.isPocketDimension(dimID)) throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");

        MapStorage storage = WorldUtils.getWorld(dimID).getPerWorldStorage();
        PocketRegistry instance = (PocketRegistry) storage.getOrLoadData(PocketRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new PocketRegistry();
            instance.initNewRegistry();
            storage.setData(DATA_NAME, instance);
        }

        instance.dimID = dimID;
        return instance;
    }

    public void initNewRegistry() {
        gridSize = DDConfig.getPocketGridSize();
        maxPocketSize = DDConfig.getMaxPocketSize();
        privatePocketSize = DDConfig.getPrivatePocketSize();
        publicPocketSize = DDConfig.getPublicPocketSize();

        nextID = 0;
        pockets = new HashMap<>();
        privatePocketMap = new HashMap<>();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        Integer version = nbt.getInteger("version");
        if (version == null || version != DATA_VERSION) {
            if (upgradeRegistry(nbt, version == null ? -1 : version)) {
                markDirty();
            } else {
                DimDoors.log.fatal("Failed to upgrade the pocket registry, you'll have to recreate your world!");
                throw new RuntimeException("Couldn't upgrade registry"); // TODO: better exceptions
            }
        }

        gridSize = nbt.getInteger("gridSize");
        maxPocketSize = nbt.getInteger("maxPocketSize");
        privatePocketSize = nbt.getInteger("privatePocketSize");
        publicPocketSize = nbt.getInteger("publicPocketSize");
        privatePocketMap = NBTUtils.readMapStringInteger(nbt.getCompoundTag("privatePocketMap"));
        nextID = nbt.getInteger("nextID");

        pockets = new HashMap<>();
        NBTTagList pocketsNBT = (NBTTagList) nbt.getTag("pockets");
        for (NBTBase pocketNBT : pocketsNBT) { // TODO: convert to map to be able to skip IDs efficiently
            NBTTagCompound pocketNBTC = (NBTTagCompound) pocketNBT;
            pockets.put(pocketNBTC.getInteger("id"), Pocket.readFromNBT(pocketNBTC));
        }

        for (Pocket pocket : pockets.values()) {
            pocket.dimID = dimID;
        }
    }

    private static boolean upgradeRegistry(NBTTagCompound nbt, int oldVersion) {
        if (oldVersion > DATA_VERSION) throw new RuntimeException("Upgrade the mod!"); // TODO: better exceptions
        switch (oldVersion) {
            case -1: // No version tag
                return false;
            case 0:
                // Upgrade to 1 or return false
            case 1:
                // Upgrade to 2 or return false
            case 2:
                // Upgrade to 3 or return false
            // ...
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("version", DATA_VERSION);

        nbt.setInteger("gridSize", gridSize);
        nbt.setInteger("maxPocketSize", maxPocketSize);
        nbt.setInteger("privatePocketSize", privatePocketSize);
        nbt.setInteger("publicPocketSize", publicPocketSize);
        nbt.setTag("privatePocketMap", NBTUtils.writeMapStringInteger(privatePocketMap));
        nbt.setInteger("nextID", nextID);

        NBTTagList pocketsNBT = new NBTTagList();
        for (Map.Entry<Integer, Pocket> pocketEntry : pockets.entrySet()) {
            if (pocketEntry.getValue() == null) continue;
            NBTTagCompound pocketNBT = (NBTTagCompound) Pocket.writeToNBT(pocketEntry.getValue());
            pocketNBT.setInteger("id", pocketEntry.getKey()); // TODO: store separately?
            pocketsNBT.appendTag(pocketNBT);
        }
        nbt.setTag("pockets", pocketsNBT);

        return nbt;
    }

    /**
     * Create a new blank pocket.
     *
     * @return The newly created Pocket
     */
    public Pocket newPocket(int depth) {
        Pocket pocket = null;
        while(pocket == null) pocket = newPocket(nextID++, depth); // TODO: config option to reuse IDs (start at 0 rather than nextFreePocket)
        return pocket;
    }

    /**
     * Create a new pocket with a specific ID. IMPORTANT: This generates data for all pockets with an ID below, so don't set
     * too high!
     *
     * @return The newly created Pocket, or null if that ID is taken already.
     */
    public Pocket newPocket(int id, int depth) {
        if (pockets.get(id) != null) return null;
        GridUtils.GridPos pos = getGridPosFromID(id);
        Pocket pocket = new Pocket(id, dimID, pos.getX(), pos.getZ(), depth);
        pockets.put(id, pocket);
        if (id >= nextID) nextID = id + 1;
        markDirty();
        return pocket;
    }

    public void removePocket(int id) {
        pockets.remove(id);
        markDirty();
    }

    // TODO: Lookup functions such as getPocketByType/Depth/etc.

    /**
     * Gets the pocket with a certain ID, or null if there is no pocket with that ID.
     *
     * @return The pocket with that ID, or null if there was no pocket with that ID.
     */
    public Pocket getPocket(int id) {
        return pockets.get(id);
    }

    public int getPrivatePocketID(String playerUUID) {
        Integer id = privatePocketMap.get(playerUUID);
        if (id == null) return -1;
        return id;
    }

    public void setPrivatePocketID(String playerUUID, int id) {
        privatePocketMap.put(playerUUID, id);
        markDirty();
    }

    public GridUtils.GridPos getGridPosFromID(int id) {
        return GridUtils.numToPos(id);
    }

    public int getIDFromGridPos(GridUtils.GridPos pos) {
        return GridUtils.posToNum(pos);
    }

    /**
     * Calculates the default Location where a pocket should be based on the ID. Use this only for placing
     * pockets, and use Pocket.getGridPos() for getting the position
     *
     * @param id The ID of the pocket
     * @return The Location of the pocket
     */
    public Location getLocationFromID(int id) {
        GridUtils.GridPos pos = getGridPosFromID(id);
        return new Location(dimID, pos.getX() * gridSize * 16, 0, pos.getZ() * gridSize * 16);
    }

    /**
     * Calculates the ID of a pocket based on the Location.
     *
     * @param x The x coordinate of the player.
     * @param y The y coordinate of the player.
     * @param z The z coordinate of the player.
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    public int getIDFromLocation(int x, int y, int z) {
        int id = getIDFromGridPos(new GridUtils.GridPos(x / (gridSize * 16), z / (gridSize * 16)));
        return pockets.containsKey(id) ? id : -1;
    }

    public Pocket getPocketFromLocation(int x, int y, int z) {
        return getPocket(getIDFromLocation(x, y, z));
    }

    public void allowPlayerAtLocation(EntityPlayer player, int x, int y, int z) {
        Pocket pocket = getPocketFromLocation(x, y, z);
        if (pocket != null) {
            pocket.allowPlayer(player);
            markDirty();
        }
    }

    public boolean isPlayerAllowedToBeHere(EntityPlayerMP player, int x, int y, int z) { // TODO see getLocationFromID
        int pocketID = getIDFromLocation(x, y, z);
        if (pocketID == -1) { // outside of a pocket
            return false;
        } else {
            Pocket pocket = pockets.get(pocketID);
            return pocket.isPlayerAllowedInPocket(player) && pocket.isLocationWithinPocketBounds(x, y, z, gridSize);
        }
    }
}
