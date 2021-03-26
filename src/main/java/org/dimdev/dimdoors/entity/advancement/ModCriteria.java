package org.dimdev.dimdoors.entity.advancement;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class ModCriteria {
	public static final RiftTrackedCriterion RIFT_TRACKED = CriterionRegistry.register(new RiftTrackedCriterion());

	public static void init() {
	}
}
