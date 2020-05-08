package org.dimdev.dimdoors.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class ModEntityTypes {
    public static final EntityType<MonolithEntity> MONOLITH = register(
            "dimdoors:monolith",
            MonolithEntity::new,
            3, 3
    );

    public static final EntityType<MaskEntity> MASK = register(
            "dimdoors:mask",
            MaskEntity::new,
            1, 1
    );

    public static void init() {
        EntityRendererRegistry.INSTANCE.register(MONOLITH, MonolithRenderer::new);
        EntityRendererRegistry.INSTANCE.register(MASK, MaskRenderer::new);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, int a, int b) {
        return Registry.register(Registry.ENTITY_TYPE, id, EntityType.Builder.create(factory, EntityCategory.MONSTER).setDimensions(a, b).makeFireImmune().spawnableFarFromPlayer().build(id));
    }
}
