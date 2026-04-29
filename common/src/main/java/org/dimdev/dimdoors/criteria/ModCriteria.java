package org.dimdev.dimdoors.criteria;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModCriteria {
    public static final RiftTrackedCriterion RIFT_TRACKED = DimensionalDoors.getSided().registerTriggerType(RiftTrackedCriterion.ID, new RiftTrackedCriterion());
    public static final TagBlockBreakCriteria TAG_BLOCK_BREAK = DimensionalDoors.getSided().registerTriggerType(TagBlockBreakCriteria.ID, new TagBlockBreakCriteria());
    public static final PocketSpawnPointSetCondition POCKET_SPAWN_POINT_SET = DimensionalDoors.getSided().registerTriggerType(PocketSpawnPointSetCondition.ID, new PocketSpawnPointSetCondition());

    public static void init() {
    }
}
