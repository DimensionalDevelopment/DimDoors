package org.dimdev.dimdoors.entity;

//import org.dimdev.dimdoors.client.MaskRenderer;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<MonolithEntity>> MONOLITH = register(
            "monolith",
            MonolithEntity::new,
			2f, 2.7f, false
    );

    public static final RegistrySupplier<EntityType<MaskEntity>> MASK = register(
            "mask",
            MaskEntity::new,
            0.9375f, 0.9375f, true
    );

    public static void init() {
        ENTITY_TYPES.register();
        EntityAttributeRegistry.register(MONOLITH, MonolithEntity::createMobAttributes);
        EntityAttributeRegistry.register(MASK, MaskEntity::createMobAttributes);
    }

    private static <E extends Entity> RegistrySupplier<EntityType<E>> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).canSpawnFarFromPlayer().fireImmune().build(id));
    }
}
