package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.GridUtil;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.IdReferencePocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.Map;

public class PocketDirectory {
    public static final Codec<PocketDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("grid_size").forGetter(a -> a.gridSize),
            Codec.INT.fieldOf("private_pocket_size").forGetter(a -> a.privatePocketSize),
            Codec.INT.fieldOf("public_pocket_size").forGetter(a -> a.publicPocketSize),
            CodecUtils.unboundedMap(Codec.INT, AbstractPocket.CODEC).fieldOf("pockets").forGetter(a -> a.pockets),
            CodecUtils.unboundedMap(Codec.INT, Codec.INT, Int2IntAVLTreeMap::new).fieldOf("next_id_map").forGetter(a -> a.nextIDMap)
    ).apply(instance, PocketDirectory::new));



    int gridSize; // Determines how much pockets in their dimension are spaced
    int privatePocketSize;
    int publicPocketSize;
    Map<Integer, AbstractPocket<?, ?>> pockets;
    private final Int2IntAVLTreeMap nextIDMap;

    public PocketDirectory() {
        this.gridSize = DimensionalDoors.getConfig().getPocketsConfig().pocketGridSize;
        this.nextIDMap = new Int2IntAVLTreeMap();
        this.pockets = new HashMap<>();
    }

    public PocketDirectory(int gridSize, int privatePocketSize, int publicPocketSize, Map<Integer, AbstractPocket<?, ?>> pockets, Int2IntAVLTreeMap nextIDMap) {
        this.gridSize = gridSize;
        this.privatePocketSize = privatePocketSize;
        this.publicPocketSize = publicPocketSize;
        this.pockets = pockets;

        this.nextIDMap = nextIDMap;
    }

    /**
     * Create a new blank pocket.
     *
     * @return The newly created pockets
     */
    public <T extends Pocket<T, ?>> T newPocket(ResourceKey<Level> worldKey, Pocket.PocketBuilder<T, ?> builder) {
        Vec3i size = builder.getExpectedSize();
        int longest = Math.max(Math.max(size.getX(), size.getZ()), 1);
        longest = (Math.floorDiv(longest - 1, gridSize * 16)) + 1;

        int base3Size = 1;
        while (longest > base3Size) {
            base3Size *= 3;
        }

        int squaredSize = base3Size * base3Size;

        int cursor = nextIDMap.headMap(base3Size + 1).values().intStream().max().orElse(0);
        cursor = cursor - Math.floorMod(cursor, squaredSize);

        T pocket = null;
        while (pocket == null) {
            Pocket<?, ?> pocketAt = getPocket(cursor);
            if (pocketAt == null) {
                int pocketId = cursor + squaredSize - 1; // use the last id of the assigned grid space since it is in the bottom left corner

                T candidate = builder.copy()
                        .id(pocketId)
                        .world(worldKey)
                        .range(squaredSize)
                        .offsetOrigin(idToCenteredPos(pocketId, base3Size, builder.getExpectedSize()))
                        .build();

                if (!PocketChunkClaims.hasClaimedChunk(candidate)) {
                    cursor = pocketId;
                    pocket = candidate;
                } else {
                    cursor += squaredSize;
                }
                continue;
            }

            size = pocketAt.getSize();
            longest = Math.max(size.getX(), size.getZ());
            longest = (longest / (gridSize * 16)) + 1;

            int pocketBase3Size = 1;
            while (longest > pocketBase3Size) {
                pocketBase3Size *= 3;
            }

            cursor += Math.max(squaredSize, pocketBase3Size * pocketBase3Size);
        }

        nextIDMap.put(base3Size, cursor + squaredSize);
        PocketChunkClaims.claimChunks(pocket);
        addPocket(pocket);

        preloadPocketChunks(pocket);
        PocketChunkLoadingManager.applyIfForceLoaded(pocket);

        IdReferencePocket.IdReferencePocketBuilder idReferenceBuilder = IdReferencePocket.builder();
        for (int i = 1; i < squaredSize; i++) {
            addPocket(idReferenceBuilder
                    .id(cursor - i)
                    .world(worldKey)
                    .referencedId(cursor)
                    .build());
        }
        return pocket;
    }

    private void preloadPocketChunks(Pocket<?, ?> pocket) {
        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return;

        BoundingBox box = pocket.getBox();

        int minCX = box.minX() >> 4;
        int maxCX = box.maxX() >> 4;
        int minCZ = box.minZ() >> 4;
        int maxCZ = box.maxZ() >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    private void addPocket(AbstractPocket<?, ?> pocket) {
        pockets.put(pocket.getId(), pocket);
        DimensionalRegistry.setDirty();
    }

    public void removePocket(int id) {
        DimensionalDoors.LOGGER.warn("Pocket deletion is disabled pending full registry cleanup support. Ignoring removePocket({}, {})."/*, this.worldKey*/, id);
    }

    private boolean referencesPocket(AbstractPocket<?, ?> pocket, int id) {
        return pocket instanceof IdReferencePocket referencePocket && referencePocket.getReferencedId() == id;
    }

    /**
     * Gets the pocket that occupies the GridPos which a certain ID represents, or null if there is no pocket at that GridPos.
     *
     * @return The pocket which occupies the GridPos represented by that ID, or null if there was no pocket occupying that GridPos.
     */
    public Pocket<?, ?> getPocket(int id) {
        AbstractPocket<?, ?> pocket = this.pockets.get(id);
        return pocket == null ? null : pocket.getReferencedPocket(this);
    }

    public <P extends Pocket< ?, ?>> P getPocket(int id, Class<P> clazz) {
        Pocket<?, ?> pocket = getPocket(id);
        if (clazz.isInstance(pocket)) return clazz.cast(pocket);
        return null;
    }

    public GridUtil.GridPos idToGridPos(int id) {
        return GridUtil.idToGridPos(id);
    }

    public int gridPosToID(GridUtil.GridPos pos) {
        return GridUtil.gridPosToID(pos);
    }

    /**
     * Calculates the default BlockPos where a pocket should be based on the ID. Use this only for placing
     * pockets, and use Pocket.getGridPos() for getting the position
     *
     * @param id The ID of the pocket
     * @return The BlockPos of the pocket
     */
    public BlockPos idToPos(int id) {
        GridUtil.GridPos pos = this.idToGridPos(id);
        return new BlockPos(pos.x * this.gridSize * 16, 0, pos.z * this.gridSize * 16);
    }

    public BlockPos idToCenteredPos(int id, int base3Size, Vec3i expectedSize) {
        GridUtil.GridPos pos = this.idToGridPos(id);
        // you actually need the "/ 2 * 16" here. "*8" would not work the same since it doesn't guarantee chunk alignment
        return new BlockPos((pos.x * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.getX() / 16) / 2 * 16, 0, (pos.z * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.getZ() / 16) / 2 * 16);
    }

    /**
     * Calculates the ID of a pocket at a certain BlockPos.
     *
     * @param pos The position
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    public int posToID(BlockPos pos) {
        return this.gridPosToID(new GridUtil.GridPos(Math.floorDiv(pos.getX(), this.gridSize * 16), Math.floorDiv(pos.getZ(), this.gridSize * 16)));
    }

    public Pocket<?, ?> getPocketAt(BlockPos pos) { // TODO: use BlockPos
        return this.getPocket(this.posToID(pos));
    }

    public boolean isWithinPocketBounds(BlockPos pos) {
        Pocket<?, ?> pocket = this.getPocketAt(pos);
        return pocket != null && pocket.isInBounds(pos);
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public int getPrivatePocketSize() {
        return this.privatePocketSize;
    }

    public int getPublicPocketSize() {
        return this.publicPocketSize;
    }

    public Map<Integer, AbstractPocket<?, ?>> getPockets() {
        return this.pockets;
    }
}
