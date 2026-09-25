package org.dimdev.dimdoors.rift.registry

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object RegistryVertices : PlatformRegistry.MapCodecPlatformRegistry<RegistryVertex>(ModRegistryKeys.REGISTRY_VERTEX_TYPE, DimensionalDoors.getSided()) {
    val PLAYER = create("player") { PlayerRiftPointer.MAP_CODEC }
    val RIFT = create("rift") { Rift.MAP_CODEC }
    val ENTRANCE = create("entrance") { PocketEntrancePointer.MAP_CODEC }
    val RIFT_PLACEHOLDER = create("rift_placeholder") { RiftPlaceholder.MAP_CODEC }
}