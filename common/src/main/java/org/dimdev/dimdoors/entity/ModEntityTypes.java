package org.dimdev.dimdoors.entity;

//import org.dimdev.dimdoors.client.MaskRenderer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.mask.MaskEntity;

public class ModEntityTypes {

    public static final EntityType<MonolithEntity> MONOLITH = register(
            "monolith",
            MonolithEntity::new,
        2f, 2.7f, false
    );

    public static final EntityType<MaskEntity> MASK = register(
            "mask",
            MaskEntity::new,
            0.9375f, 0.9375f, true
    );

    public static void init() {
        DimensionalDoors.getSided().registerEntityAttributes(MONOLITH, MonolithEntity::createMobAttributes);
        DimensionalDoors.getSided().registerEntityAttributes(MASK, MaskEntity::createAttributes);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return DimensionalDoors.getSided().registerEntityType(id, EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).canSpawnFarFromPlayer().fireImmune().build(id));
    }
}
