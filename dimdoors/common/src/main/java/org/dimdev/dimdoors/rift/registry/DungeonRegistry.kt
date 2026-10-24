package org.dimdev.dimdoors.rift.registry

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
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.pockets.TemplateUtils
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.world.pocket.PocketInfo
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

class DungeonRegistry(dungeons: Map<UUID, Dungeon> = emptyMap()) : SubSystem<DungeonRegistry>() {
    private val dungeons: MutableMap<UUID, Dungeon> = dungeons.toMutableMap()

    override fun type(): Type<DungeonRegistry> = SubsystemTypes.DUNGEON

    fun create(theme: ResourceKey<VirtualPocket>, root: VirtualLocation): UUID = UUID.randomUUID().also { id ->
        dungeons[id] = Dungeon(theme, root)
        RiftGraph.getInstance().addVertex(id, RegistryVertices.DUNGEON)
        setDirty()
    }

    fun dungeonOf(pocket: Pocket<*, *>): UUID? {
        val graph = RiftGraph.getInstance()
        return graph.sources(PocketInfo(pocket.world, pocket.id).uuid).firstOrNull { graph.vertex(it) === RegistryVertices.DUNGEON }
    }

    fun generateRoom(id: UUID, depth: Int, owner: Vertex): Target? {
        val dungeon = dungeons[id] ?: return null
        val location = VirtualLocation(dungeon.root.world, dungeon.root.x, dungeon.root.z, depth)

        val pocket = PocketGenerator.generateDungeonPocketV2(location, RiftReference(owner), null, dungeon.theme)

        if (pocket == null) {
            LOGGER.error("Failed to generate room for dungeon {} at {} from {}.", id, location, owner)
            return null
        }

        RiftGraph.getInstance().addEdge(id, PocketInfo(pocket.world, pocket.id).uuid)

        val entrance = PocketRegistry.instance.getPocketEntrance(pocket)
        TemplateUtils.linkRifts(owner, entrance)
        return entrance?.blockEntity?.castOrNull<Target>()
    }

    data class Dungeon(val theme: ResourceKey<VirtualPocket>, val root: VirtualLocation) {
        companion object {
            val CODEC: Codec<Dungeon> = RecordCodecBuilder.create { instance -> instance.group(
                    Schemas.registryKey(ModRegistryKeys.POCKET_GROUPS).fieldOf("theme").forGetter(Dungeon::theme),
                    VirtualLocation.CODEC.fieldOf("root").forGetter(Dungeon::root)
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
