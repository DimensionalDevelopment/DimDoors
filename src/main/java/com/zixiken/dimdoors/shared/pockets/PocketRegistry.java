package com.zixiken.dimdoors.shared.pockets;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.zixiken.dimdoors.shared.DDConfig;
import ddutils.math.GridUtils;
import com.zixiken.dimdoors.DimDoors;
import ddutils.nbt.NBTUtils;
import ddutils.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class PocketRegistry extends WorldSavedData { // TODO: unregister pocket entrances, private pocket entrances/exits

    private static final String DATA_NAME = DimDoors.MODID + "_pockets";
    @Getter private static final int DATA_VERSION = 0; // IMPORTANT: Update this and upgradeRegistry when making changes.

    @Getter private int gridSize; // Determines how much pockets in their dimension are spaced
    @Getter private int maxPocketSize;
    @Getter private int privatePocketSize;
    @Getter private int publicPocketSize;
    private BiMap<String, Integer> privatePocketMap; // Player UUID -> Pocket ID, in pocket dim only
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
        privatePocketMap = HashBiMap.create();
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
        privatePocketMap = NBTUtils.readBiMapStringInteger(nbt.getCompoundTag("privatePocketMap"));
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

    @SuppressWarnings("unused")
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
     * @return The newly created pocket
     */
    public Pocket newPocket() {
        Pocket pocket = null;
        while(pocket == null) pocket = newPocket(nextID++); // TODO: config option to reuse IDs (start at 0 rather than nextFreePocket)
        return pocket;
    }

    /**
     * Create a new pocket with a specific ID.
     *
     * @return The newly created Pocket, or null if that ID is already taken.
     */
    public Pocket newPocket(int id) {
        if (pockets.get(id) != null) return null;
        GridUtils.GridPos pos = idToGridPos(id);
        Pocket pocket = new Pocket(id, dimID, pos.getX(), pos.getZ());
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

    // TODO: these should be per-map rather than per-world
    public int getPrivatePocketID(String playerUUID) {
        Integer id = privatePocketMap.get(playerUUID);
        if (id == null) return -1;
        return id;
    }

    public String getPrivatePocketOwner(int id) {
        return privatePocketMap.inverse().get(id);
    }

    public void setPrivatePocketID(String playerUUID, int id) {
        privatePocketMap.put(playerUUID, id);
        markDirty();
    }

    public GridUtils.GridPos idToGridPos(int id) {
        return GridUtils.numToPos(id);
    }

    public int gridPosToID(GridUtils.GridPos pos) {
        return GridUtils.posToNum(pos);
    }

    /**
     * Calculates the default BlockPos where a pocket should be based on the ID. Use this only for placing
     * pockets, and use Pocket.getGridPos() for getting the position
     *
     * @param id The ID of the pocket
     * @return The BlockPos of the pocket
     */
    public BlockPos idToPos(int id) {
        GridUtils.GridPos pos = idToGridPos(id);
        return new BlockPos(pos.getX() * gridSize * 16, 0, pos.getZ() * gridSize * 16);
    }

    /**
     * Calculates the ID of a pocket at a certain BlockPos.
     *
     * @param pos The position
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    public int posToID(BlockPos pos) {
        return gridPosToID(new GridUtils.GridPos(pos.getX() / (gridSize * 16), pos.getZ() / (gridSize * 16)));
    }

    public Pocket getPocketAt(BlockPos pos) { // TODO: use BlockPos
        return getPocket(posToID(pos));
    }

    public boolean isPlayerAllowedToBeHere(EntityPlayerMP player, BlockPos pos) {
        return true; // TODO: fix this
        //Pocket pocket = getPocketAt(pos);
        //return pocket != null && pocket.isInBounds(pos);
    }
}
