package org.dimdev.dimdoors.subsystem.registry

import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.rift.block.LevelSpaceRegistry
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.rift.link.LinkPropertiesRegistry
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.player.DialingRegistry
import org.dimdev.dimdoors.subsystem.player.HubRegistry
import org.dimdev.dimdoors.subsystem.pocket.DungeonRegistry
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry
import org.dimdev.dimdoors.subsystem.pocket.PrivateRegistry

object SubsystemTypes : PlatformRegistry<SubSystem.Type<*>>(ModRegistryKeys.SUBSYSTEM_TYPE, getSided()) {
    val GRAPH = create("rift_graph", ::RiftGraph, RiftGraph.CODEC)
    val LOCATION_RIFT = create("location_rift_registry", ::LocationRiftRegistry, LocationRiftRegistry.CODEC)
    val ITEM_RIFT = create("item_rift_registry", ::ItemRiftRegistry, ItemRiftRegistry.CODEC)
    val PRIVATE = create("private_registry", ::PrivateRegistry, PrivateRegistry.CODEC)
    val POCKET = create("pocket_registry", ::PocketRegistry, PocketRegistry.CODEC)
    val DIALING = create("dialing_registry", ::DialingRegistry, DialingRegistry.CODEC)
    val PROPERTIES = create("properties_registry", ::LinkPropertiesRegistry, LinkPropertiesRegistry.CODEC)
    val LEVEL_SPACE = create("level_space_registry", ::LevelSpaceRegistry, LevelSpaceRegistry.CODEC)
    val DUNGEON = create("dungeon_registry", ::DungeonRegistry, DungeonRegistry.CODEC)
    val HUB = create("hub_registry", ::HubRegistry, HubRegistry.CODEC)

    private fun <T : SubSystem<T>> create(
        name: String,
        supplier: () -> T,
        codec: MapCodec<T>
    ): SubSystem.Type<T> = create(name) { SubSystem.Type("$modid/$name", supplier, codec) }

}