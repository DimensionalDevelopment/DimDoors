package org.dimdev.dimdoors.criteria;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModCriteria {
	public static final DeferredRegister<CriterionTrigger<?>> CRITERION = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.TRIGGER_TYPE);
	public static final Holder<RiftTrackedCriterion> RIFT_TRACKED = CRITERION.register("rift_traced", RiftTrackedCriterion::new);
	public static final Holder<TagBlockBreakCriteria> TAG_BLOCK_BREAK = CRITERION.register("tag_block_break", TagBlockBreakCriteria::new);
	public static final Holder<PocketSpawnPointSetCondition> POCKET_SPAWN_POINT_SET = CRITERION.register("pocket_spawn_point_set", PocketSpawnPointSetCondition::new);

	public static void init() {
		CRITERION.register();
	}
}
