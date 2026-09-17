package org.dimdev.dimdoors.criteria;

import org.dimdev.dimdoors.DimensionalDoors;

public class ModCriteria {
    public static final RiftTrackedCriterion RIFT_TRACKED = DimensionalDoors.getSided().registerTriggerType(RiftTrackedCriterion.ID, new RiftTrackedCriterion());
    public static final TagBlockBreakCriteria TAG_BLOCK_BREAK = DimensionalDoors.getSided().registerTriggerType(TagBlockBreakCriteria.ID, new TagBlockBreakCriteria());
    public static final PocketSpawnPointSetCondition POCKET_SPAWN_POINT_SET = DimensionalDoors.getSided().registerTriggerType(PocketSpawnPointSetCondition.ID, new PocketSpawnPointSetCondition());

    public static void init() {
    }
}
