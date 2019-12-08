package org.dimdev.dimdoors.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class MaskEntity extends MobEntity { // TODO
    protected MaskEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
}
