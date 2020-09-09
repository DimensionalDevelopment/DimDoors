package org.dimdev.dimdoors.world.pocket;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.NbtUtil;
import org.dimdev.dimdoors.util.WorldUtil;
import org.dimdev.dimdoors.util.math.GridUtil;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class PocketRegistry extends PersistentState {
    private Codec<Map<Integer, Pocket>> pocketsCodec = Codec.unboundedMap(Codec.INT, Pocket.CODEC);

    private static final String DATA_NAME = "pocketlib_pockets";

    @Saved /*package-private*/ int gridSize; // Determines how much pockets in their dimension are spaced
    @Saved /*package-private*/ int privatePocketSize;
    @Saved /*package-private*/ int publicPocketSize;
    @Saved /*package-private*/ Map<Integer, Pocket> pockets;
    @Saved /*package-private*/ int nextID;

    private ServerWorld world;

    public PocketRegistry() {
        super(DATA_NAME);
        gridSize = ModConfig.POCKETS.pocketGridSize;

        nextID = 0;
        pockets = new HashMap<>();
    }

    public PocketRegistry(String s) {
        super(s);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        gridSize = tag.getInt("gridSize");
        privatePocketSize = tag.getInt("privatePocketSize");
        publicPocketSize = tag.getInt("publicPocketSize");
        pockets = NbtUtil.deserialize(tag.get("pockets"), pocketsCodec);
        nextID = tag.getInt("nextID");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("gridSize", gridSize);
        tag.putInt("privatePocketSize", privatePocketSize);
        tag.putInt("publicPocketSize", publicPocketSize);
        tag.put("pockets", NbtUtil.serialize(pockets, pocketsCodec));
        tag.putInt("nextID", nextID);
        return tag;
    }

    public static PocketRegistry instance(RegistryKey<World> key) {
        ServerWorld world = WorldUtil.getWorld(key);

        if (!(ModDimensions.isDimDoorsPocketDimension(world))) {
            throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
        }

        PocketRegistry instance = world.getPersistentStateManager().getOrCreate(PocketRegistry::new, DATA_NAME);

        instance.world = world;
        for (Pocket pocket : instance.pockets.values()) {
            pocket.world = key;
        }

        return instance;
    }

    /**
     * Create a new blank pocket.
     *
     * @return The newly created pockets
     */
    public Pocket newPocket() {
        Pocket pocket = null;
        while (pocket == null) pocket = newPocket(nextID++);
        return pocket;
    }

    /**
     * Create a new pockets with a specific ID.
     *
     * @return The newly created Pocket, or null if that ID is already taken.
     */
    public Pocket newPocket(int id) {
        if (pockets.get(id) != null) return null;
        GridUtil.GridPos pos = idToGridPos(id);
        Pocket pocket = new Pocket(id, world.getRegistryKey(), pos.x, pos.z);
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

    public GridUtil.GridPos idToGridPos(int id) {
        return GridUtil.numToPos(id);
    }

    public int gridPosToID(GridUtil.GridPos pos) {
        return GridUtil.posToNum(pos);
    }

    /**
     * Calculates the default BlockPos where a pocket should be based on the ID. Use this only for placing
     * pockets, and use Pocket.getGridPos() for getting the position
     *
     * @param id The ID of the pocket
     * @return The BlockPos of the pocket
     */
    public BlockPos idToPos(int id) {
        GridUtil.GridPos pos = idToGridPos(id);
        return new BlockPos(pos.x * gridSize * 16, 0, pos.z * gridSize * 16);
    }

    /**
     * Calculates the ID of a pocket at a certain BlockPos.
     *
     * @param pos The position
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    public int posToID(BlockPos pos) {
        return gridPosToID(new GridUtil.GridPos(pos.getX() / (gridSize * 16), pos.getZ() / (gridSize * 16)));
    }

    public Pocket getPocketAt(BlockPos pos) { // TODO: use BlockPos
        return getPocket(posToID(pos));
    }

    public boolean isWithinPocketBounds(BlockPos pos) {
        Pocket pocket = getPocketAt(pos);
        return pocket != null && pocket.isInBounds(pos);
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getPrivatePocketSize() {
        return privatePocketSize;
    }

    public int getPublicPocketSize() {
        return publicPocketSize;
    }

    public Map<Integer, Pocket> getPockets() {
        return pockets;
    }

    public int getNextID() {
        return nextID;
    }
}
