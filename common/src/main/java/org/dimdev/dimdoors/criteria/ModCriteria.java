package org.dimdev.dimdoors.criteria;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModCriteria {
	private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.TRIGGER_TYPE);

	public static final RegistrySupplier<RiftTrackedCriterion> RIFT_TRACKED = REGISTER.register(RiftTrackedCriterion.ID, RiftTrackedCriterion::new);
	public static final RegistrySupplier<TagBlockBreakCriteria> TAG_BLOCK_BREAK = REGISTER.register(TagBlockBreakCriteria.ID, TagBlockBreakCriteria::new);
	public static final RegistrySupplier<PocketSpawnPointSetCondition> POCKET_SPAWN_POINT_SET = REGISTER.register(PocketSpawnPointSetCondition.ID, PocketSpawnPointSetCondition::new);

	public static void init() {
		REGISTER.register();
	}
}
