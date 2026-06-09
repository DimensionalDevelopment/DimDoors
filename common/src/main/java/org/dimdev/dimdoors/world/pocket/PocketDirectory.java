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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PocketDirectory {
    public static final Codec<PocketDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("grid_size").forGetter(a -> a.gridSize),
            Codec.INT.fieldOf("private_pocket_size").forGetter(a -> a.privatePocketSize),
            Codec.INT.fieldOf("public_pocket_size").forGetter(a -> a.publicPocketSize),
            CodecUtils.unboundedMap(CodecUtils.STRING_INT, AbstractPocket.CODEC).fieldOf("pockets").forGetter(a -> a.pockets),
            CodecUtils.unboundedMap(CodecUtils.STRING_INT, Codec.INT, Int2IntAVLTreeMap::new).fieldOf("next_id_map").forGetter(a -> a.nextIDMap)
    ).apply(instance, PocketDirectory::new));

    int gridSize;
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
        this.pockets = new HashMap<>(Objects.requireNonNull(pockets, "pockets"));
        this.nextIDMap = Objects.requireNonNull(nextIDMap, "nextIDMap");

        validateLoadedState();
    }

    /**
     * Create a new blank pocket.
     *
     * @return The newly created pocket
     */
    public <T extends Pocket<T, ?>> T newPocket(ResourceKey<Level> worldKey, Pocket.PocketBuilder<T, ?> builder) {
        Vec3i size = builder.getExpectedSize();
        int longest = Math.max(Math.max(size.getX(), size.getZ()), 1);
        longest = Math.floorDiv(longest - 1, gridSize * 16) + 1;

        int base3Size = 1;
        while (longest > base3Size) {
            base3Size *= 3;
        }

        int squaredSize = base3Size * base3Size;

        int cursor = nextIDMap.headMap(base3Size + 1).values().intStream().max().orElse(0);
        cursor = cursor - Math.floorMod(cursor, squaredSize);

        T pocket = null;
        while (pocket == null) {
            int pocketId = cursor + squaredSize - 1;
            int minId = pocketId - squaredSize + 1;
            int maxId = pocketId;

            if (!isIdRangeFree(minId, maxId)) {
                cursor += squaredSize;
                continue;
            }

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
        }

        int minId = cursor - squaredSize + 1;
        int maxId = cursor;

        assertIdRangeFree(minId, maxId);

        nextIDMap.put(base3Size, cursor + squaredSize);

        PocketChunkClaims.claimChunks(pocket);

        addPocket(pocket);

        IdReferencePocket.IdReferencePocketBuilder idReferenceBuilder = IdReferencePocket.builder();
        for (int i = 1; i < squaredSize; i++) {
            addPocket(idReferenceBuilder
                    .id(cursor - i)
                    .world(worldKey)
                    .referencedId(cursor)
                    .build());
        }

        preloadPocketChunks(pocket);
        PocketChunkLoadingManager.applyIfForceLoaded(pocket);

        return pocket;
    }

    private boolean isIdRangeFree(int minId, int maxId) {
        for (int id = minId; id <= maxId; id++) {
            if (this.pockets.containsKey(id)) {
                return false;
            }
        }

        return true;
    }

    private void assertIdRangeFree(int minId, int maxId) {
        for (int id = minId; id <= maxId; id++) {
            AbstractPocket<?, ?> existing = this.pockets.get(id);
            if (existing != null) {
                throw new IllegalStateException("Pocket id range collision at id " + id
                        + " in range " + minId + ".." + maxId
                        + ". Existing=" + existing);
            }
        }
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
        Objects.requireNonNull(pocket, "pocket");

        AbstractPocket<?, ?> previous = this.pockets.putIfAbsent(pocket.getId(), pocket);
        if (previous != null) {
            throw new IllegalStateException("Attempted to overwrite pocket id " + pocket.getId()
                    + ". Previous=" + previous
                    + ", New=" + pocket);
        }

        DimensionalRegistry.setIsDirty();
    }

    public void removePocket(int id) {
        DimensionalDoors.LOGGER.warn("Pocket deletion is disabled pending full registry cleanup support. Ignoring removePocket({}).", id);
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

    public <P extends Pocket<?, ?>> P getPocket(int id, Class<P> clazz) {
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
     * pockets, and use Pocket.getGridPos() for getting the position.
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
        return new BlockPos(
                (pos.x * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.getX() / 16) / 2 * 16,
                0,
                (pos.z * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.getZ() / 16) / 2 * 16
        );
    }

    /**
     * Calculates the ID of a pocket at a certain BlockPos.
     *
     * @param pos The position
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    public int posToID(BlockPos pos) {
        return this.gridPosToID(new GridUtil.GridPos(
                Math.floorDiv(pos.getX(), this.gridSize * 16),
                Math.floorDiv(pos.getZ(), this.gridSize * 16)
        ));
    }

    public Pocket<?, ?> getPocketAt(BlockPos pos) {
        return this.getPocket(this.posToID(pos));
    }

    public boolean isWithinPocketBounds(BlockPos pos) {
        Pocket<?, ?> pocket = this.getPocketAt(pos);
        return pocket != null && pocket.isInBounds(pos);
    }

    private void validateLoadedState() {
        if (this.pockets.isEmpty() && !this.nextIDMap.isEmpty()) {
            DimensionalDoors.LOGGER.error(
                    "Loaded PocketDirectory with empty pockets map but non-empty nextIDMap. gridSize={}, privatePocketSize={}, publicPocketSize={}, nextIDMap={}",
                    this.gridSize,
                    this.privatePocketSize,
                    this.publicPocketSize,
                    this.nextIDMap
            );
        }

        for (Map.Entry<Integer, AbstractPocket<?, ?>> entry : this.pockets.entrySet()) {
            int key = entry.getKey();
            AbstractPocket<?, ?> pocket = entry.getValue();

            if (pocket == null) {
                throw new IllegalStateException("Loaded null pocket at id " + key);
            }

            if (pocket.getId() != key) {
                throw new IllegalStateException("Pocket map key/id mismatch. Key=" + key
                        + ", pocketId=" + pocket.getId()
                        + ", pocket=" + pocket);
            }

            if (pocket instanceof IdReferencePocket referencePocket && !this.pockets.containsKey(referencePocket.getReferencedId())) {
                throw new IllegalStateException("Reference pocket " + referencePocket.getId()
                        + " points to missing pocket " + referencePocket.getReferencedId());
            }
        }
    }

    public boolean hasPockets() {
        return !this.pockets.isEmpty();
    }

    public int getPocketCount() {
        return this.pockets.size();
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
        return Collections.unmodifiableMap(this.pockets);
    }
}