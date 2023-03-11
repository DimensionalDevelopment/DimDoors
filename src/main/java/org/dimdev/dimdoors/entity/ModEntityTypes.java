package org.dimdev.dimdoors.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.dimdev.dimdoors.client.MonolithRenderer;

public class ModEntityTypes {
    public static final EntityType<MonolithEntity> MONOLITH = register(
            "dimdoors:monolith",
            MonolithEntity::new,
			2f, 2.7f, false
    );

    public static final EntityType<MaskEntity> MASK = register(
            "dimdoors:mask",
            MaskEntity::new,
            0.9375f, 0.9375f, true
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(MONOLITH, MonolithEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(MASK, MonolithEntity.createMobAttributes());
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.register(MONOLITH, MonolithRenderer::new);
//        EntityRendererRegistry.INSTANCE.register(MASK, MaskRenderer::new);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, FabricEntityTypeBuilder.create(MobCategory.MONSTER, factory).dimensions(new EntityDimensions(width, height, fixed)).spawnableFarFromPlayer().fireImmune().build());
    }
}
