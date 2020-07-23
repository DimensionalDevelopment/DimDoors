package org.dimdev.dimdoors.entity;

import org.dimdev.dimdoors.client.MaskRenderer;
import org.dimdev.dimdoors.client.MonolithRenderer;
import org.dimdev.dimdoors.client.RiftRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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
    public static final EntityType<RiftEntity> RIFT = register(
            "dimdoors:rift",
            RiftEntity::new,
            1, 1
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(MONOLITH, MonolithEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(MASK, MonolithEntity.createMobAttributes());
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.INSTANCE.register(MONOLITH, MonolithRenderer::new);
        EntityRendererRegistry.INSTANCE.register(MASK, MaskRenderer::new);
        EntityRendererRegistry.INSTANCE.register(RIFT, RiftRenderer::new);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, int width, int height) {
        return Registry.register(Registry.ENTITY_TYPE, id, FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, factory).dimensions(EntityDimensions.fixed(width, height)).spawnableFarFromPlayer().fireImmune().build());
    }
}
