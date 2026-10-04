package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.MapCodec
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.world.pocket.PrivateRegistry

object SubsystemTypes : PlatformRegistry<SubSystem.Type<*>>(ModRegistryKeys.SUBSYSTEM_TYPE, getSided()) {
    val GRAPH = create("rift_graph", ::RiftGraph, RiftGraph.CODEC)
    val RIFT = create("rift_registry", ::RiftRegistry, RiftRegistry.CODEC)
    val PRIVATE = create("private_registry", ::PrivateRegistry, PrivateRegistry.CODEC)
    val POCKET = create("pocket_registry", ::PocketRegistry, PocketRegistry.CODEC)
    val DIALING = create("dialing_registry", ::DialingRegistry, DialingRegistry.CODEC)
//    val DUNGEON = create("dungeon_registry", ::DungeonRegistry, DungeonRegistry.CODEC)

    private fun <T : SubSystem<T>> create(
        name: String,
        supplier: () -> T,
        codec: MapCodec<T>
    ): SubSystem.Type<T> = create(name) { SubSystem.Type("$modid/$name", supplier, codec) }
}