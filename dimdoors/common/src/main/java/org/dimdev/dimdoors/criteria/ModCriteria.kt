package org.dimdev.dimdoors.criteria

import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModCriteria : PlatformRegistry<CriterionTrigger<*>>(Registries.TRIGGER_TYPE, BuiltInRegistries.TRIGGER_TYPES, getSided()) {
    @JvmField
    val RIFT_TRACKED = create<RiftTrackedCriterion>(RiftTrackedCriterion.ID) { RiftTrackedCriterion() }
    @JvmField
    val TAG_BLOCK_BREAK = create(TagBlockBreakCriteria.ID) { TagBlockBreakCriteria() }
    @JvmField val POCKET_SPAWN_POINT_SET= create(PocketSpawnPointSetCondition.ID) { PocketSpawnPointSetCondition() }
}
