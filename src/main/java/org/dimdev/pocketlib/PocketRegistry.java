package org.dimdev.pocketlib;

import net.minecraft.world.World;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.ddutils.math.GridUtils;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.WorldUtils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.dimdev.dimdoors.shared.ModConfig;

@NBTSerializable public class PocketRegistry extends WorldSavedData {

    private static final String DATA_NAME = "pocketlib_pockets";

    @Saved @Getter /*package-private*/ int gridSize; // Determines how much pockets in their dimension are spaced
    @Saved @Getter /*package-private*/ int privatePocketSize;
    @Saved @Getter /*package-private*/ int publicPocketSize;
    @Saved @Getter /*package-private*/ Map<Integer, Pocket> pockets;
    @Saved @Getter /*package-private*/ int nextID;

    @Getter private int dim;

    public PocketRegistry() {
        super(DATA_NAME);
    }

    public PocketRegistry(String s) {
        super(s);
    }

    public static PocketRegistry instance(int dim) {
        World world = WorldUtils.getWorld(dim);

        if (!(world.provider instanceof WorldProviderPocket)) {
            throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions (asked for dim " + dim + ")!");
        }

        MapStorage storage = world.getPerWorldStorage();
        PocketRegistry instance = (PocketRegistry) storage.getOrLoadData(PocketRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new PocketRegistry();
            instance.initNewRegistry();
            storage.setData(DATA_NAME, instance);
        }

        instance.dim = dim;
        for (Pocket pocket : instance.pockets.values()) {
            pocket.dim = dim;
        }

        return instance;
    }

    public void initNewRegistry() {
        gridSize = ModConfig.pockets.pocketGridSize;

        nextID = 0;
        pockets = new HashMap<>();
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    /**
     * Create a new blank pocket.
     *
     * @return The newly created pockets
     */
    public Pocket newPocket() {
        Pocket pocket = null;
        while(pocket == null) pocket = newPocket(nextID++);
        return pocket;
    }

    /**
     * Create a new pockets with a specific ID.
     *
     * @return The newly created Pocket, or null if that ID is already taken.
     */
    public Pocket newPocket(int id) {
        if (pockets.get(id) != null) return null;
        GridUtils.GridPos pos = idToGridPos(id);
        Pocket pocket = new Pocket(id, dim, pos.getX(), pos.getZ());
        pockets.put(id, pocket);
        if (id >= nextID) nextID = id + 1;
        markDirty();
        return pocket;
    }

    public void removePocket(int id) {
        pockets.remove(id);
        markDirty();
    }

    /**
     * Gets the pocket with a certain ID, or null if there is no pocket with that ID.
     *
     * @return The pocket with that ID, or null if there was no pocket with that ID.
     */
    public Pocket getPocket(int id) {
        return pockets.get(id);
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

    public boolean isWithinPocketBounds(BlockPos pos) {
        Pocket pocket = getPocketAt(pos);
        return pocket != null && pocket.isInBounds(pos);
    }
}
