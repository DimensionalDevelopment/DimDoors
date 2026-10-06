package org.dimdev.dimdoors.rift.registry

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object RegistryVertices : PlatformRegistry<RegistryVertex>(ModRegistryKeys.REGISTRY_VERTEX_TYPE, DimensionalDoors.getSided()) {
    val PRIVATE_ENTRANCE = create("private_entrance") { PlayerTrackerPointer.create(SubsystemTypes.PRIVATE, PlayerTrackerPointer.Variant.Entrance) }
    val PRIVATE_EXIT = create("private_exit") { PlayerTrackerPointer.create(SubsystemTypes.PRIVATE, PlayerTrackerPointer.Variant.Exit) }
    val DIALING_ENTRANCE = create("dialing_entrance") { PlayerTrackerPointer.create(SubsystemTypes.DIALING, PlayerTrackerPointer.Variant.Entrance) }
    val DIALING_EXIT = create("dialing_exit") { PlayerTrackerPointer.create(SubsystemTypes.DIALING, PlayerTrackerPointer.Variant.Exit) }
    val RIFT = create("rift") { Rift }
    val ENTRANCE = create("entrance") { PocketEntrancePointer }
    val RIFT_PLACEHOLDER = create("rift_placeholder") { RiftPlaceholder }
}