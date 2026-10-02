package org.dimdev.dimdoors.rift.registry

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.nbt.*
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelResource
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.api.util.Edge
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.NbtUtil
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem.PlayerRiftConnection
import org.dimdev.dimdoors.world.pocket.PocketDirectory
import org.dimdev.dimdoors.world.pocket.PocketInfo
import org.dimdev.dimdoors.world.pocket.PrivateRegistry
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object LegacyDimensionalRegistryMigrator {
    private const val OLD_DATA_NAME = "dimensional_registry"
    private const val SUPPORTED_RIFT_DATA_VERSION = 1
    private const val COMPOUND = Tag.TAG_COMPOUND.toInt()
    private val POCKET_DIRECTORY_MAP_CODEC = Codec.unboundedMap(Level.RESOURCE_KEY_CODEC, PocketDirectory.CODEC)

    private val splitTypes: List<SubSystem.Type<*>>
        get() = listOf(SubsystemTypes.GRAPH, SubsystemTypes.RIFT, SubsystemTypes.PRIVATE, SubsystemTypes.POCKET)

    fun migrateIfNeeded(server: MinecraftServer) {
        val oldFile = server.dataFile(OLD_DATA_NAME)
        if (!Files.exists(oldFile)) return

        val existing = splitTypes.count { Files.exists(server.dataFile(it.toFilename())) }
        if (existing == splitTypes.size) return
        if (existing > 0) {
            DimensionalDoors.LOGGER.warn("Found old {}.dat but only some split registry data exists. Skipping automatic registry migration.", OLD_DATA_NAME)
            return
        }

        try {
            val migration = parse(readSavedDataRoot(oldFile).getCompound("data"))
            migration.install(server)

            DimensionalDoors.LOGGER.info(
                "Migrated old {}.dat into split registry data: {} rifts, {} pocket entrance pointers, {} private pocket owners, {} graph edges.",
                OLD_DATA_NAME, migration.riftCount, migration.pocketEntrancePointerCount, migration.privatePocketOwnerCount, migration.graphEdgeCount
            )

            if (migration.droppedAnything) {
                DimensionalDoors.LOGGER.warn(
                    "Registry migration skipped {} rift entries, {} unresolved links, {} pocket pointer entries, {} private pointer entries, and {} old overworld entries.",
                    migration.droppedRiftCount, migration.droppedLinkCount, migration.droppedPocketPointerCount, migration.droppedPlayerPointerCount, migration.droppedOverworldEntryCount
                )
            }
        } catch (e: Exception) {
            DimensionalDoors.LOGGER.error("Failed to migrate old {}.dat. Leaving existing data unchanged.", OLD_DATA_NAME, e)
        }
    }

    private fun MinecraftServer.dataFile(name: String): Path = getWorldPath(LevelResource.ROOT).resolve("data").resolve("$name.dat")

    private fun readSavedDataRoot(file: Path): CompoundTag = try {
        NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap())
    } catch (compressed: IOException) {
        val raw = try {
            NbtIo.read(file)
        } catch (rawFailure: IOException) {
            compressed.addSuppressed(rawFailure)
            null
        }
        raw ?: throw compressed
    }

    private fun parse(oldData: CompoundTag): Migration {
        for (key in listOf("pocket_registry", "private_registry", "rift_registry")) {
            check(oldData.contains(key, COMPOUND)) { "Legacy dimensional registry is missing required compound '$key'" }
        }

        val riftDataVersion = oldData.getInt("RiftDataVersion")
        check(riftDataVersion <= SUPPORTED_RIFT_DATA_VERSION) { "Cannot migrate future RiftDataVersion $riftDataVersion" }

        val directories = POCKET_DIRECTORY_MAP_CODEC.parse(NbtOps.INSTANCE, oldData.getCompound("pocket_registry")).getOrThrow().toMutableMap()
        val privatePockets = readPrivatePockets(oldData.getCompound("private_registry"))
        val riftTag = oldData.getCompound("rift_registry")

        val vertices = HashMap<UUID, RegistryVertex>()
        val rifts = readRifts(riftTag, vertices)
        val entrances = readPocketEntrancePointers(riftTag, vertices)
        val edges = readLinks(riftTag, vertices)
        val importedLinkCount = edges.size
        val playerConnections = readPlayerConnections(riftTag, vertices, edges)

        val playerPointerEntries = riftTag.compounds("last_private_pocket_entrances").size + riftTag.compounds("last_private_pocket_exits").size
        val importedPlayerPointers = playerConnections.values.sumOf { listOfNotNull(it.entrance, it.exit).size }

        return Migration(
            pocketRegistry = PocketRegistry(directories, entrances.pointers),
            riftRegistry = RiftRegistry().apply { locationMap.putAll(rifts.byLocation) },
            privateRegistry = PrivateRegistry(playerConnections, privatePockets),
            riftGraph = RiftGraph(edges),
            riftCount = rifts.byLocation.size,
            pocketEntrancePointerCount = entrances.pointers.size,
            privatePocketOwnerCount = privatePockets.size,
            graphEdgeCount = edges.size,
            droppedRiftCount = rifts.dropped,
            droppedPocketPointerCount = entrances.dropped,
            droppedLinkCount = riftTag.compounds("links").size - importedLinkCount,
            droppedPlayerPointerCount = playerPointerEntries - importedPlayerPointers,
            droppedOverworldEntryCount = riftTag.compounds("overworld_rifts").size + riftTag.compounds("overworld_locations").size
        )
    }

    private fun readPrivatePockets(privateRegistryTag: CompoundTag): HashBiMap<UUID, PocketInfo> {
        val pocketMapTag = privateRegistryTag.getCompound("private_pocket_map")
        val privatePockets = HashBiMap.create<UUID, PocketInfo>()

        for (key in pocketMapTag.allKeys) {
            try {
                val playerId = UUID.fromString(key)
                val pocketInfo = NbtUtil.deserialize(pocketMapTag.getCompound(key), PocketInfo.CODEC)
                if (playerId !in privatePockets && pocketInfo !in privatePockets.values) {
                    privatePockets[playerId] = pocketInfo
                }
            } catch (e: RuntimeException) {
                DimensionalDoors.LOGGER.warn("Skipping invalid legacy private pocket mapping for {}.", key, e)
            }
        }

        return privatePockets
    }

    private fun readRifts(riftTag: CompoundTag, vertices: MutableMap<UUID, RegistryVertex>): ParsedRifts {
        val riftType = typeId(RegistryVertices.RIFT)
        val placeholderType = typeId(RegistryVertices.RIFT_PLACEHOLDER)
        val byLocation = LinkedHashMap<Location, Rift>()
        var dropped = 0

        for (vertexTag in riftTag.compounds("rifts")) {
            val rift = when (vertexTag.getString("type")) {
                riftType -> Rift.MAP_CODEC.parseOrNull(vertexTag)
                placeholderType -> RiftPlaceholder.MAP_CODEC.parseOrNull(vertexTag)
                else -> continue
            }

            if (rift == null) {
                dropped++
                continue
            }

            vertices[rift.id] = rift
            byLocation[rift.location] = rift
        }

        return ParsedRifts(byLocation, dropped)
    }

    private fun readPocketEntrancePointers(riftTag: CompoundTag, vertices: MutableMap<UUID, RegistryVertex>): ParsedPointers {
        val entranceType = typeId(RegistryVertices.ENTRANCE)
        val pointers = mutableMapOf<PocketInfo, PocketEntrancePointer>()
        var entries = 0

        for (pointerTag in riftTag.compounds("pockets")) {
            if (pointerTag.getString("type") != entranceType) continue
            entries++

            val pointer = PocketEntrancePointer.MAP_CODEC.parseOrNull(pointerTag) ?: continue
            val world = pointer.world ?: continue

            pointers.put(PocketInfo(world, pointer.pocketId), pointer)?.let { vertices.remove(it.id) }
            vertices[pointer.id] = pointer
        }

        return ParsedPointers(pointers, entries - pointers.size)
    }

    private fun readLinks(riftTag: CompoundTag, vertices: Map<UUID, RegistryVertex>): MutableList<Edge> =
        riftTag.compounds("links")
            .map { Edge(it.getUUID("from"), it.getUUID("to")) }
            .filterTo(mutableListOf()) { it.source in vertices && it.target in vertices }

    private fun readPlayerConnections(riftTag: CompoundTag, vertices: MutableMap<UUID, RegistryVertex>, edges: MutableList<Edge>): MutableMap<UUID, PlayerRiftConnection> {
        val entranceTargets = readPlayerPointerTargets(riftTag, "last_private_pocket_entrances", vertices)
        val exitTargets = readPlayerPointerTargets(riftTag, "last_private_pocket_exits", vertices)
        val connections = LinkedHashMap<UUID, PlayerRiftConnection>()

        fun createPointer(target: UUID): UUID {
            val pointer = PlayerRiftPointer()
            vertices[pointer.id] = pointer
            edges += Edge(pointer.id, target)
            return pointer.id
        }

        entranceTargets.forEach { (player, target) -> connections.getOrPut(player) { PlayerRiftConnection() }.entrance = createPointer(target) }
        exitTargets.forEach { (player, target) -> connections.getOrPut(player) { PlayerRiftConnection() }.exit = createPointer(target) }

        return connections
    }

    private fun readPlayerPointerTargets(riftTag: CompoundTag, key: String, vertices: Map<UUID, RegistryVertex>): Map<UUID, UUID> =
        riftTag.compounds(key)
            .filter { entry -> vertices[entry.getUUID("rift")].let { it is Rift && it !is RiftPlaceholder } }
            .associate { it.getUUID("player") to it.getUUID("rift") }

    private fun typeId(type: MapCodec<out RegistryVertex>): String = ModRegistries.REGISTRY_VERTEX_TYPE.getKey(type).toString()

    private fun CompoundTag.compounds(key: String): List<CompoundTag> = getList(key, COMPOUND).filterIsInstance<CompoundTag>()

    private fun <T> MapCodec<T>.parseOrNull(tag: CompoundTag): T? = codec().parse(NbtOps.INSTANCE, tag).result().orElse(null)

    private class ParsedRifts(val byLocation: Map<Location, Rift>, val dropped: Int)

    private class ParsedPointers(val pointers: MutableMap<PocketInfo, PocketEntrancePointer>, val dropped: Int)

    private class Migration(
        val pocketRegistry: PocketRegistry,
        val riftRegistry: RiftRegistry,
        val privateRegistry: PrivateRegistry,
        val riftGraph: RiftGraph,
        val riftCount: Int,
        val pocketEntrancePointerCount: Int,
        val privatePocketOwnerCount: Int,
        val graphEdgeCount: Int,
        val droppedRiftCount: Int,
        val droppedPocketPointerCount: Int,
        val droppedLinkCount: Int,
        val droppedPlayerPointerCount: Int,
        val droppedOverworldEntryCount: Int
    ) {
        val droppedAnything: Boolean
            get() = droppedRiftCount > 0 || droppedLinkCount > 0 || droppedPocketPointerCount > 0 || droppedPlayerPointerCount > 0 || droppedOverworldEntryCount > 0

        fun install(server: MinecraftServer) {
            val storage = server.overworld().dataStorage

            fun put(type: SubSystem.Type<*>, data: SubSystem<*>) {
                data.setDirty()
                storage.set(type.toFilename(), data)
            }

            put(SubsystemTypes.POCKET, pocketRegistry)
            put(SubsystemTypes.RIFT, riftRegistry)
            put(SubsystemTypes.PRIVATE, privateRegistry)
            put(SubsystemTypes.GRAPH, riftGraph)
            storage.save()
        }
    }
}
