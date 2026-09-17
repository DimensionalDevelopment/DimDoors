package org.dimdev.dimdoors.entity;

//import org.dimdev.dimdoors.client.MaskRenderer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

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
    public static EntityType<FarShotEnderPearlEntity> FARSHOT_ENDER_PEARL = DimensionalDoors.getSided().registerEntityType(
            "farshot_ender_pearl",
            EntityType.Builder.<FarShotEnderPearlEntity>of(FarShotEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("farshot_ender_pearl"));

    public static void init() {
        DimensionalDoors.getSided().registerEntityAttributes(MONOLITH, MonolithEntity::createMobAttributes);
        DimensionalDoors.getSided().registerEntityAttributes(MASK, MonolithEntity::createMobAttributes);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return DimensionalDoors.getSided().registerEntityType(id, EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).canSpawnFarFromPlayer().fireImmune().build(id));
    }
}
