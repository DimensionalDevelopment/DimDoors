package org.dimdev.dimdoors.pockets.modifier

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.ModRegistryKeys

object Modifiers : PlatformRegistry.MapCodecPlatformRegistry<Modifier>(ModRegistryKeys.MODIFIER_TYPE, DimensionalDoors.getSided()) {
    val SHELL = create(ShellModifier.KEY) { ShellModifier.CODEC }
    val DIMENSIONAL_DOOR = create(DimensionalDoorModifier.KEY) { DimensionalDoorModifier.CODEC }
    val PUBLIC = create(PocketEntranceModifier.KEY) { PocketEntranceModifier.CODEC }
    val RIFT_DATA = create(RiftDataModifier.KEY) { RiftDataModifier.CODEC }
    val RELATIVE_REFERENCE = create(RelativeReferenceModifier.KEY) { RelativeReferenceModifier.CODEC }
    val OFFSET = create(OffsetModifier.KEY) { OffsetModifier.CODEC }
    val TEMPLATE = create(TemplateModifier.KEY) { TemplateModifier.CODEC }
}