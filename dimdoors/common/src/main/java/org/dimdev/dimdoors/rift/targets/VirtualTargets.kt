package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimcore.api.PlatformRegistry.MapCodecPlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.rift.targets.VirtualTarget.NoneTarget

object VirtualTargets : MapCodecPlatformRegistry<VirtualTarget<*>>(ModRegistryKeys.VIRTUAL_TARGET, DimensionalDoors.getSided()) {
    val AVAILABLE_LINK = create("available_link") { AvailableLinkTarget.CODEC }
    val DUNGEON = create("dungeon") { DungeonTarget.CODEC }
    val TEMPLATE= create("template") { TemplateTarget.CODEC }
    val ESCAPE = create("escape") { EscapeTarget.CODEC }
    val RIFT_REFERENCE = create("rift_reference") { RiftReference.CODEC }
    val TEMP = create("temp") { TempTarget.CODEC }
    val LIMBO = create("limbo") { LimboTarget.CODEC }
    val PUBLIC_POCKET = create("public_pocket") { PublicPocketTarget.codec }
    val POCKET_ENTRANCE = create("pocket_entrance") { PocketEntranceMarker.CODEC }
    val POCKET_EXIT = create("pocket_exit") { PocketExitMarker.codec }
    val PRIVATE = create("private") { PrivatePocketTarget.codec }
    val PRIVATE_POCKET_EXIT = create("private_pocket_exit") { PrivatePocketExitTarget.codec }
    val DIALING = create("dialing") { DialingTargetImpl.CODEC }
    val DIALING_EXIT = create("dialing_pocket_exit") { DialingExitTarget.codec }
    val UNSTABLE = create("unstable") { UnstableTarget.codec }
    val ID_MARKER = create("id_marker") { IdMarker.CODEC }
    val NONE = create("none") { NoneTarget.codec }
}