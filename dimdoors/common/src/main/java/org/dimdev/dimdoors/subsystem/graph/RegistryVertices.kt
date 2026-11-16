package org.dimdev.dimdoors.subsystem.graph

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.rift.RiftPlaceholder
import org.dimdev.dimdoors.rift.block.LocationRift
import org.dimdev.dimdoors.rift.item.ItemRift
import org.dimdev.dimdoors.subsystem.pocket.DungeonVertex
import org.dimdev.dimdoors.subsystem.pocket.PocketEntrancePointer

object RegistryVertices : PlatformRegistry<RegistryVertex>(ModRegistryKeys.REGISTRY_VERTEX_TYPE, DimensionalDoors.getSided()) {
    val ENTRANCE = create("entrance") { PocketEntrancePointer }
    val DUNGEON = create("dungeon") { DungeonVertex }

    val RIFT_PLACEHOLDER = create("rift_placeholder") { RiftPlaceholder }
    val LOCATION_RIFT = create("location_rift") { LocationRift }
    val ITEM_RIFT = create("item_rift") { ItemRift }
}