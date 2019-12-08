package org.dimdev.dimdoors.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class ModEntityTypes {
    public static final EntityType<MonolithEntity> MONOLITH = register(
            "dimdoors:monolith",
            MonolithEntity::new,
            EntityCategory.MONSTER,
            true,
            true,
            true,
            true,
            new EntityDimensions(3, 3, false)
    );

    public static final EntityType<MaskEntity> MASK = register(
            "dimdoors:mask",
            MaskEntity::new,
            EntityCategory.MONSTER,
            true,
            true,
            true,
            true,
            new EntityDimensions(1, 1, false)
    );

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, EntityCategory category, boolean canSpawnFar, boolean saveable, boolean summonable, boolean immuneToFire, EntityDimensions dimensions) {
        return Registry.register(Registry.ENTITY_TYPE, id, new EntityType<>(factory, category, canSpawnFar, saveable, summonable, immuneToFire, dimensions));
    }
}
