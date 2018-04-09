package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.entity.Entity;

public interface IEntityTarget extends ITarget {
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch);
}
