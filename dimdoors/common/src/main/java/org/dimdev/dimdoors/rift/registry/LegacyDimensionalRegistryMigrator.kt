package org.dimdev.dimdoors.rift.registry

import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.*
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelResource
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistries
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.NbtUtil
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget
import org.dimdev.dimdoors.rift.targets.TempTarget
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.world.pocket.PocketDirectory
import org.dimdev.dimdoors.world.pocket.PocketInfo
import org.dimdev.dimdoors.world.pocket.PrivateRegistry
import org.dimdev.dimdoors.world.pocket.type.PocketColor
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object LegacyDimensionalRegistryMigrator {
    private const val OLD_REGISTRY_ID = "dimdoors:dimensional_registry"
    private const val SUPPORTED_RIFT_DATA_VERSION = 1
    private const val COMPOUND = Tag.TAG_COMPOUND.toInt()
    private val POCKET_DIRECTORY_MAP_CODEC = Codec.unboundedMap(Level.RESOURCE_KEY_CODEC, PocketDirectory.CODEC)

    private val LEGACY_SOURCES: List<Pair<String, (CompoundTag) -> CompoundTag>> = listOf(
        "cardinal_world_components" to { data: CompoundTag ->
            data.getCompound("cardinal_components").getCompound(OLD_REGISTRY_ID)
        },
        "capabilities" to { data: CompoundTag -> data.getCompound(OLD_REGISTRY_ID) },
        "dimensional_registry" to { data: CompoundTag -> data }
    )

    private val splitTypes: List<SubSystem.Type<*>>
        get() = listOf(
            SubsystemTypes.GRAPH,
            SubsystemTypes.RIFT,
            SubsystemTypes.PRIVATE,
            SubsystemTypes.POCKET,
            SubsystemTypes.PROPERTIES
        )

    fun migrateIfNeeded(server: MinecraftServer) {
        val existing = splitTypes.count { Files.exists(server.dataFile(it.toFilename())) }
        if (existing == splitTypes.size) return

        val (oldName, oldData) = readLegacyData(server) ?: return

        if (existing > 0) {
            DimensionalDoors.LOGGER.warn(
                "Found old {}.dat but only some split registry data exists. Skipping automatic registry migration.",
                oldName
            )
            return
        }

        val migration = parse(oldData)
        migration.install(server)

        DimensionalDoors.LOGGER.info(
            "Migrated old {}.dat into split registry data: {} rifts, {} pocket entrance pointers, {} private pocket owners, {} graph edges.",
            oldName,
            migration.riftCount,
            migration.pocketEntrancePointerCount,
            migration.privatePocketOwnerCount,
            migration.graphEdgeCount
        )

        if (migration.droppedAnything) {
            DimensionalDoors.LOGGER.warn(
                "Registry migration skipped {} rift entries, {} unresolved links, {} pocket pointer entries, {} private pointer entries, and {} old overworld entries.",
                migration.droppedRiftCount,
                migration.droppedLinkCount,
                migration.droppedPocketPointerCount,
                migration.droppedPlayerPointerCount,
                migration.droppedOverworldEntryCount
            )
        }
    }

    private fun MinecraftServer.dataFile(name: String): Path =
        getWorldPath(LevelResource.ROOT).resolve("data").resolve("$name.dat")

    private fun readLegacyData(server: MinecraftServer): Pair<String, CompoundTag>? =
        LEGACY_SOURCES.firstNotNullOfOrNull { (name, payload) ->
            val file = server.dataFile(name)
            if (!Files.exists(file)) return@firstNotNullOfOrNull null

            payload(readSavedDataRoot(file).getCompound("data")).takeUnless { it.isEmpty() }?.let { name to it }
        }

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

        val pocketRegistryTag = oldData.getCompound("pocket_registry")
        fixPocketRegistry(pocketRegistryTag)

        val directories =
            POCKET_DIRECTORY_MAP_CODEC.parse(NbtOps.INSTANCE, pocketRegistryTag).getOrThrow().toMutableMap()
        val privatePockets = readPrivatePockets(oldData.getCompound("private_registry"))
        val riftTag = oldData.getCompound("rift_registry")

        val rifts = readRifts(riftTag)
        val entrances = readPocketEntrancePointers(riftTag)
        val vertices = rifts.locations.keys.map { Vertex(it, RegistryVertices.RIFT) } + entrances.ids.values.distinct()
            .map { Vertex(it, RegistryVertices.ENTRANCE) }
        val edges = readLinks(riftTag, entrances.ids, vertices.mapTo(hashSetOf()) { it.id })
        val playerConnections = readPlayerConnections(riftTag, rifts.locations)

        val playerPointerEntries =
            riftTag.compounds("last_private_pocket_entrances").size + riftTag.compounds("last_private_pocket_exits").size

        return Migration(
            pocketRegistry = PocketRegistry(directories),
            riftRegistry = RiftRegistry(rifts.locations),
            privateRegistry = PrivateRegistry(playerConnections, privatePockets),
            riftGraph = RiftGraph(vertices, edges),
            propertiesRegistry = LinkPropertiesRegistry(rifts.properties),
            riftCount = rifts.locations.size,
            pocketEntrancePointerCount = entrances.ids.size,
            privatePocketOwnerCount = privatePockets.size,
            graphEdgeCount = edges.size,
            droppedRiftCount = rifts.dropped,
            droppedPocketPointerCount = entrances.dropped,
            droppedLinkCount = riftTag.compounds("links").size - edges.size,
            droppedPlayerPointerCount = playerPointerEntries - playerConnections.size,
            droppedOverworldEntryCount = riftTag.compounds("overworld_rifts").size + riftTag.compounds("overworld_locations").size
        )
    }

    private fun fixPocketRegistry(pocketRegistry: CompoundTag) {
        for (world in pocketRegistry.allKeys) {
            val pockets = pocketRegistry.getCompound(world).getCompound("pockets")

            for (id in pockets.allKeys) {
                fixPocket(pockets.getCompound(id))
            }
        }
    }

    private fun fixPocket(pocket: CompoundTag) {
        if (pocket.getString("type") == "dimdoors:lazy_gen_pocket") pocket.putString("type", "dimdoors:pocket")

        pocket.remove("to_be_genned_chunks")
        if (pocket.contains("generator", COMPOUND)) pocket.remove("generator")

        if (pocket.getString("type") == "dimdoors:id_reference") return

        val addons = ListTag()
        pocket.compounds("addons").mapNotNullTo(addons, ::fixAddon)
        pocket.put("addons", addons)
    }

    private fun fixAddon(addon: CompoundTag): CompoundTag? = when (addon.getString("type")) {
        "dimdoors:dyeable" -> addon.apply {
            fixDyeColor("dyeColor")
            fixDyeColor("nextDyeColor")
        }

        "dimdoors:sky" -> fixSky(addon)
        "dimdoors:block_break_container" -> null
        else -> addon
    }

    private fun CompoundTag.fixDyeColor(key: String) {
        if (contains(key, Tag.TAG_ANY_NUMERIC.toInt())) {
            putString(key, PocketColor.entries.getOrElse(getInt(key)) { PocketColor.NONE }.serializedName)
        }
    }

    private fun fixSky(sky: CompoundTag): CompoundTag? {
        val environment = CompoundTag()

        when (sky.getString("effect")) {
            "minecraft:overworld" -> {
                environment.putString("type", "dimdoors:overworld")
                environment.putLong("day_time", sky.getLong("dayTime"))
                environment.putInt("moon_phase", sky.getByte("moonPhase").toInt())
            }

            "minecraft:the_end" -> environment.putString("type", "dimdoors:end")
            else -> return null
        }

        return CompoundTag().apply {
            putString("type", "dimdoors:environment")
            put("environment", environment)
        }
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

    private fun readRifts(riftTag: CompoundTag): ParsedRifts {
        val riftType = RegistryVertices.RIFT.typeId
        val locations = LinkedHashMap<UUID, Location>()
        val properties = LinkedHashMap<UUID, LinkPropertiesRegistry.Entry>()
        var dropped = 0

        for (vertexTag in riftTag.compounds("rifts")) {
            val location = vertexTag.takeIf { it.getString("type") == riftType }
                ?.let { Location.CODEC.parseOrNull(it.getCompound("location")) }

            if (location == null) {
                dropped++
                continue
            }

            val id = vertexTag.getUUID("id")
            locations[id] = location
            properties[id] = LinkPropertiesRegistry.Entry(
                LinkProperties.CODEC.parseOrNull(vertexTag.get("properties")),
                vertexTag.getBoolean("isDetached")
            )
        }

        return ParsedRifts(locations, properties, dropped)
    }

    private fun readPocketEntrancePointers(riftTag: CompoundTag): ParsedPointers {
        val entranceType = RegistryVertices.ENTRANCE.typeId
        val ids = HashMap<UUID, UUID>()
        var dropped = 0

        for (pointerTag in riftTag.compounds("pockets")) {
            if (pointerTag.getString("type") != entranceType) continue

            val world = Level.RESOURCE_KEY_CODEC.parseOrNull(pointerTag.get("pocketDim"))

            if (world == null) {
                dropped++
                continue
            }

            ids[pointerTag.getUUID("id")] = PocketInfo(world, pointerTag.getInt("pocketId")).uuid
        }

        return ParsedPointers(ids, dropped)
    }

    private fun readLinks(
        riftTag: CompoundTag,
        entranceIds: Map<UUID, UUID>,
        vertices: Set<UUID>
    ): List<RiftGraph.Edge> =
        riftTag.compounds("links")
            .map {
                RiftGraph.Edge(it.getUUID("from").let { from -> entranceIds[from] ?: from }, it.getUUID("to"))
            }.filter { it.source in vertices && it.target in vertices }

    private fun readPlayerConnections(riftTag: CompoundTag, rifts: Map<UUID, Location>): MutableMap<UUID, UUID> {
        val connections = LinkedHashMap<UUID, UUID>()

        fun read(key: String, variant: PlayerTrackerPointer.Variant) {
            for (entry in riftTag.compounds(key)) {
                val rift = entry.getUUID("rift")
                if (rift in rifts) connections[variant.getTrackingId(entry.getUUID("player"), SubsystemTypes.PRIVATE)] = rift
            }
        }

        read("last_private_pocket_entrances", PlayerTrackerPointer.Variant.Entrance)
        read("last_private_pocket_exits", PlayerTrackerPointer.Variant.Exit)

        return connections
    }

    val RegistryVertex.typeId: String get() = ModRegistries.REGISTRY_VERTEX_TYPE.getKey(this).toString()

    private fun CompoundTag.compounds(key: String): List<CompoundTag> =
        getList(key, COMPOUND).filterIsInstance<CompoundTag>()

    private fun <T> Codec<T>.parseOrNull(tag: Tag?): T? = tag?.let { parse(NbtOps.INSTANCE, it).result().orElse(null) }

    private fun Migration.install(server: MinecraftServer) {
        fun put(type: SubSystem.Type<*>, data: SubSystem<*>) {
            data.setDirty()
            data.save(server.dataFile(type.toFilename()).toFile(), server.registryAccess())
        }

        put(SubsystemTypes.POCKET, pocketRegistry)
        put(SubsystemTypes.RIFT, riftRegistry)
        put(SubsystemTypes.PRIVATE, privateRegistry)
        put(SubsystemTypes.GRAPH, riftGraph)
        put(SubsystemTypes.PROPERTIES, propertiesRegistry)
    }

    private class ParsedRifts(
        val locations: Map<UUID, Location>,
        val properties: MutableMap<UUID, LinkPropertiesRegistry.Entry>,
        val dropped: Int
    )

    private class ParsedPointers(val ids: Map<UUID, UUID>, val dropped: Int)

    private class Migration(
        val pocketRegistry: PocketRegistry,
        val riftRegistry: RiftRegistry,
        val privateRegistry: PrivateRegistry,
        val riftGraph: RiftGraph,
        val propertiesRegistry: LinkPropertiesRegistry,
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
    }

    fun upgradeDestination(tag: CompoundTag, owner: Location): VirtualTarget<*> {
        fun wrapped() = upgradeDestination(tag.getCompound("wrappedDestination"), owner).takeUnless { it === VirtualTarget.NoneTarget }
        fun reference(location: Location?) = location?.asTarget() ?: VirtualTarget.NoneTarget

        return when (tag.getString("type")) {
            "" -> VirtualTarget.NoneTarget
            "dimdoors:global", "dimdoors:rift_reference" -> reference(Location.CODEC.parseOrNull(tag.getCompound("target")))
            "dimdoors:local" -> reference(BlockPos.CODEC.parseOrNull(tag.get("target"))?.let { Location(owner.worldId, it) })
            "dimdoors:relative" -> reference(Vec3i.CODEC.parseOrNull(tag.get("offset"))?.let { Location(owner.worldId, owner.blockPos.offset(it)) })
            "dimdoors:public_pocket" -> wrapped() ?: PublicPocketTarget
            "dimdoors:template" -> wrapped() ?: decodeDestination(tag.apply {
                putString("type", "dimdoors:generator")
                putString("template", getString("template").replace("dimdoors:lab/", "dimdoors:myth/lab/"))
            })
            "dimdoors:temp" -> TempTarget(
                upgradeDestination(tag.getCompound("temp"), owner),
                upgradeDestination(tag.getCompound("original"), owner)
            )
            else -> decodeDestination(tag)
        }
    }

    private fun decodeDestination(tag: CompoundTag): VirtualTarget<*> = VirtualTarget.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(DimensionalDoors.LOGGER::error).orElse(VirtualTarget.NoneTarget)
}
