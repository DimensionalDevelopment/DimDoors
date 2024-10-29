package org.dimdev.dimdoors.criteria;

import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteria {
	public static final RiftTrackedCriterion RIFT_TRACKED = CriteriaTriggers.register(RiftTrackedCriterion.ID, new RiftTrackedCriterion());
	public static final TagBlockBreakCriteria TAG_BLOCK_BREAK = CriteriaTriggers.register(TagBlockBreakCriteria.ID, new TagBlockBreakCriteria());
	public static final PocketSpawnPointSetCondition POCKET_SPAWN_POINT_SET = CriteriaTriggers.register(PocketSpawnPointSetCondition.ID, new PocketSpawnPointSetCondition());

	public static void init() {

	}
}
