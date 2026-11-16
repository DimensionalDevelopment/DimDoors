package org.dimdev.dimdoors.subsystem.pocket

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceKey
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.pockets.PocketInfo
import org.dimdev.dimdoors.pockets.TemplateUtils
import org.dimdev.dimdoors.pockets.VirtualLocation
import org.dimdev.dimdoors.pockets.generatePocket
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.targets.DelvingTarget
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.util.ext.unboundedMap
import java.util.*

class DungeonRegistry(dungeons: Map<UUID, Dungeon> = emptyMap()) : SubSystem<DungeonRegistry>() {
    private val dungeons: MutableMap<UUID, Dungeon> = dungeons.toMutableMap()

    override fun type(): Type<DungeonRegistry> = SubsystemTypes.DUNGEON

    fun create(theme: ResourceKey<VirtualPocket>, root: VirtualLocation, gate: Vertex): UUID = UUID.randomUUID().also { id ->
        dungeons[id] = Dungeon(theme, root, gate)
        RiftGraph.getInstance().addVertex(id, RegistryVertices.DUNGEON)
        setDirty()
    }

    fun dungeonOf(pocket: Pocket<*, *>): UUID? {
        val graph = RiftGraph.getInstance()
        return graph.sources(PocketInfo(pocket.world, pocket.id).uuid).firstOrNull { graph.vertex(it) === RegistryVertices.DUNGEON }
    }

    fun generateRoom(id: UUID, depth: Int, owner: Vertex): Target? {
        val dungeon = dungeons[id] ?: return null
        val location = VirtualLocation.fromLocation(owner).copy(depth = depth)

        val pocket = dungeon.theme.generatePocket(location, RiftReference(owner))

        if (pocket == null) {
            LOGGER.error("Failed to generate room for dungeon {} at {} from {}.", id, location, owner)
            return null
        }

        addRoom(id, pocket)
        RiftGraph.getInstance().removeEdge(id, owner.id)

        val entrance = PocketRegistry.instance.getPocketEntrance(pocket)
        TemplateUtils.linkRifts(owner, entrance)
        return entrance?.blockEntity?.castOrNull<Target>()
    }

    fun addRoom(id: UUID, pocket: Pocket<*, *>) {
        val graph = RiftGraph.getInstance()
        graph.addEdge(id, PocketInfo(pocket.world, pocket.id).uuid)

        pocket.blockEntities.values.filterIsInstance<BlockEntityRift<*>>()
            .filter { it.data().destination === DelvingTarget }
            .mapNotNull(BlockEntityRift<*>::id)
            .forEach { graph.addEdge(id, it) }
    }

    fun linkToRoot(id: UUID, owner: Vertex): Target? {
        val gate = dungeons[id]?.gate ?: return null
        TemplateUtils.linkRifts(owner, gate)
        return gate.providedLocation?.blockEntity?.castOrNull<Target>()
    }

    data class Dungeon(val theme: ResourceKey<VirtualPocket>, val root: VirtualLocation, val gate: Vertex) {
        companion object {
            val CODEC: Codec<Dungeon> = RecordCodecBuilder.create { instance -> instance.group(
                    Schemas.registryKey(ModRegistryKeys.POCKET_GROUPS).fieldOf("theme").forGetter(Dungeon::theme),
                    VirtualLocation.CODEC.fieldOf("root").forGetter(Dungeon::root),
                    Vertex.CODEC.fieldOf("gate").forGetter(Dungeon::gate)
                ).apply(instance, ::Dungeon)
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        val CODEC: MapCodec<DungeonRegistry> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.STRING_CODEC.unboundedMap(Dungeon.CODEC).fieldOf("dungeons").forGetter { it.dungeons }
            ).apply(instance, ::DungeonRegistry)
        }

        @JvmStatic
        val instance: DungeonRegistry get() = getInstance(SubsystemTypes.DUNGEON)!!
    }
}
