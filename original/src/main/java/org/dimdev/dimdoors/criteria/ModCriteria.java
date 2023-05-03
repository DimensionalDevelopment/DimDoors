package org.dimdev.dimdoors.criteria;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class ModCriteria {
	public static final RiftTrackedCriterion RIFT_TRACKED = CriterionRegistry.register(new RiftTrackedCriterion());
	public static final TagBlockBreakCriteria TAG_BLOCK_BREAK = CriterionRegistry.register(new TagBlockBreakCriteria());
	public static final PocketSpawnPointSetCondition POCKET_SPAWN_POINT_SET = CriterionRegistry.register(new PocketSpawnPointSetCondition());

	public static void init() {
	}
}
