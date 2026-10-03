package org.dimdev.dimdoors.world.pocket

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.math.GridUtil
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.util.CodecUtils
import org.dimdev.dimdoors.util.CodecUtils.unboundedMap
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket
import org.dimdev.dimdoors.world.pocket.type.IdReferencePocket
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*
import kotlin.math.max

class PocketDirectory {
    var gridSize: Int
    var privatePocketSize: Int = 0
    var publicPocketSize: Int = 0
    private var pockets: Int2ObjectAVLTreeMap<AbstractPocket<*, *>>
    private val nextIDMap: Int2IntAVLTreeMap

    constructor() {
        this.gridSize = DimensionalDoors.config.pocketsConfig.pocketGridSize
        this.nextIDMap = Int2IntAVLTreeMap()
        this.pockets = Int2ObjectAVLTreeMap<AbstractPocket<*, *>>()
    }

    constructor(
        gridSize: Int,
        privatePocketSize: Int,
        publicPocketSize: Int,
        pockets: MutableMap<Int, AbstractPocket<*, *>>,
        nextIDMap: Int2IntAVLTreeMap
    ) {
        this.gridSize = gridSize
        this.privatePocketSize = privatePocketSize
        this.publicPocketSize = publicPocketSize
        this.pockets = Int2ObjectAVLTreeMap<AbstractPocket<*, *>>(pockets)
        this.nextIDMap = nextIDMap

        validateLoadedState()
    }

    /**
     * Create a new blank pocket.
     * 
     * @return The newly created pocket
     */
    fun newPocket(worldKey: ResourceKey<Level>, builder: Pocket.PocketBuilder<*, *>): Pocket<*, *> {
        val size = builder.expectedSize
        var longest = max(max(size.x, size.z), 1)
        longest = Math.floorDiv(longest - 1, gridSize * 16) + 1

        var base3Size = 1
        while (longest > base3Size) {
            base3Size *= 3
        }

        val squaredSize = base3Size * base3Size

        var cursor = nextIDMap.headMap(base3Size + 1).values.intStream().max().orElse(0)
        cursor -= Math.floorMod(cursor, squaredSize)

        var pocket: Pocket<*, *>? = null
        while (pocket == null) {
            val pocketId = cursor + squaredSize - 1
            val minId = pocketId - squaredSize + 1
            val maxId = pocketId

            if (!isIdRangeFree(minId, maxId)) {
                cursor += squaredSize
                continue
            }

            val candidate = builder.copy()
                .id(pocketId)
                .world(worldKey)
                .range(squaredSize)
                .offsetOrigin(idToCenteredPos(pocketId, base3Size, builder.expectedSize))
                .build()

            if (!PocketChunkClaims.hasClaimedChunk(candidate)) {
                cursor = pocketId
                pocket = candidate
            } else {
                cursor += squaredSize
            }
        }

        val minId = cursor - squaredSize + 1
        val maxId = cursor

        assertIdRangeFree(minId, maxId)

        nextIDMap.put(base3Size, cursor + squaredSize)

        PocketChunkClaims.claimChunks(pocket)

        addPocket(pocket)

        val idReferenceBuilder = IdReferencePocket.builder()
        for (i in 1..<squaredSize) {
            addPocket(
                idReferenceBuilder
                    .id(cursor - i)
                    .world(worldKey)
                    .referencedId(cursor)
                    .build()
            )
        }

        preloadPocketChunks(pocket)

        return pocket
    }

    private fun isIdRangeFree(minId: Int, maxId: Int): Boolean {
        for (id in minId..maxId) {
            if (this.pockets.containsKey(id)) {
                return false
            }
        }

        return true
    }

    private fun assertIdRangeFree(minId: Int, maxId: Int) {
        for (id in minId..maxId) {
            val existing = this.pockets.get(id)
            check(existing == null) {
                ("Pocket id range collision at id " + id
                        + " in range " + minId + ".." + maxId
                        + ". Existing=" + existing)
            }
        }
    }

    private fun preloadPocketChunks(pocket: Pocket<*, *>) {
        val level = DimensionalDoors.getWorld(pocket.world) ?: return

        val box = pocket.box

        val minCX = box.minX() shr 4
        val maxCX = box.maxX() shr 4
        val minCZ = box.minZ() shr 4
        val maxCZ = box.maxZ() shr 4

        for (cx in minCX..maxCX) {
            for (cz in minCZ..maxCZ) {
                level.getChunk(cx, cz)
            }
        }
    }

    private fun addPocket(pocket: AbstractPocket<*, *>?) {
        Objects.requireNonNull(pocket, "pocket")

        val previous = this.pockets.putIfAbsent(pocket!!.id, pocket)
        check(previous == null) {
            ("Attempted to overwrite pocket id " + pocket.id
                    + ". Previous=" + previous
                    + ", New=" + pocket)
        }

        instance.setDirty()
    }

    fun removePocket(id: Int) {
        DimensionalDoors.LOGGER.warn("Pocket deletion is disabled pending full registry cleanup support. Ignoring removePocket({}).", id)
    }

    private fun referencesPocket(pocket: AbstractPocket<*, *>, id: Int): Boolean {
        return pocket is IdReferencePocket && pocket.referencedId == id
    }

    /**
     * Gets the pocket that occupies the GridPos which a certain ID represents, or null if there is no pocket at that GridPos.
     * 
     * @return The pocket which occupies the GridPos represented by that ID, or null if there was no pocket occupying that GridPos.
     */
    fun getPocket(id: Int): Pocket<*, *>? = this.pockets.get(id)?.getReferencedPocket(this)

    fun <P : Pocket<*, *>> getPocket(id: Int, clazz: Class<P>): P? = getPocket(id)?.cast(clazz)

    fun idToGridPos(id: Int): GridUtil.GridPos {
        return GridUtil.idToGridPos(id)
    }

    fun gridPosToID(pos: GridUtil.GridPos): Int {
        return GridUtil.gridPosToID(pos)
    }

    /**
     * Calculates the default BlockPos where a pocket should be based on the ID. Use this only for placing
     * pockets, and use Pocket.getGridPos() for getting the position.
     * 
     * @param id The ID of the pocket
     * @return The BlockPos of the pocket
     */
    fun idToPos(id: Int): BlockPos {
        val pos = this.idToGridPos(id)
        return BlockPos(pos.x * this.gridSize * 16, 0, pos.z * this.gridSize * 16)
    }

    fun idToCenteredPos(id: Int, base3Size: Int, expectedSize: Vec3i): BlockPos {
        val pos = this.idToGridPos(id)
        return BlockPos(
            (pos.x * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.x / 16) / 2 * 16,
            0,
            (pos.z * this.gridSize * 16) + (base3Size * this.gridSize - expectedSize.z / 16) / 2 * 16
        )
    }

    /**
     * Calculates the ID of a pocket at a certain BlockPos.
     * 
     * @param pos The position
     * @return The ID of the pocket, or -1 if there is no pocket at that location
     */
    fun posToID(pos: BlockPos): Int = this.gridPosToID(
        GridUtil.GridPos(
            Math.floorDiv(pos.x, this.gridSize * 16),
            Math.floorDiv(pos.z, this.gridSize * 16)
        )
    )

    fun getPocketAt(pos: BlockPos): Pocket<*, *>? = this.getPocket(this.posToID(pos))

    fun isWithinPocketBounds(pos: BlockPos): Boolean = this.getPocketAt(pos)?.isInBounds(pos) == true

    private fun validateLoadedState() {
        if (this.pockets.isEmpty() && !this.nextIDMap.isEmpty()) {
            DimensionalDoors.LOGGER.error(
                "Loaded PocketDirectory with empty pockets map but non-empty nextIDMap. gridSize={}, privatePocketSize={}, publicPocketSize={}, nextIDMap={}",
                this.gridSize,
                this.privatePocketSize,
                this.publicPocketSize,
                this.nextIDMap
            )
        }

        for (entry in this.pockets) {
            val key = entry.key
            val pocket: AbstractPocket<*, *> = entry.value

            check(pocket.id == key) { ("Pocket map key/id mismatch. Key=$key, pocketId=${pocket.id}, pocket=$pocket") }

            if (pocket is IdReferencePocket) check(this.pockets.containsKey(pocket.referencedId)) { "Reference pocket ${pocket.id} points to missing pocket ${pocket.referencedId}" }
        }
    }

    fun hasPockets(): Boolean {
        return !this.pockets.isEmpty()
    }

    val pocketCount: Int
        get() = this.pockets.size

    fun getPockets(): Map<Int, AbstractPocket<*, *>> {
        return Collections.unmodifiableMap(this.pockets)
    }

    companion object {
        val CODEC: Codec<PocketDirectory> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("grid_size").forGetter(PocketDirectory::gridSize),
                    Codec.INT.fieldOf("private_pocket_size").forGetter (PocketDirectory::privatePocketSize),
                    Codec.INT.fieldOf("public_pocket_size").forGetter(PocketDirectory::publicPocketSize),
                    CodecUtils.STRING_INT.unboundedMap(AbstractPocket.CODEC).fieldOf("pockets").forGetter(PocketDirectory::pockets),
                    unboundedMap(CodecUtils.STRING_INT, Codec.INT, ::Int2IntAVLTreeMap).fieldOf("next_id_map").forGetter(PocketDirectory::nextIDMap)
                ).apply(instance, ::PocketDirectory)
            }
    }
}
