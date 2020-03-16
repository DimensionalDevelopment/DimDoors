package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;

public interface EntityTarget extends Target {
    boolean receiveEntity(Entity entity, float yawOffset);
}
