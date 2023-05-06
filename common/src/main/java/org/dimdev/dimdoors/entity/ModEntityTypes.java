package org.dimdev.dimdoors.entity;

//import org.dimdev.dimdoors.client.MaskRenderer;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.MonolithRenderer;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<MonolithEntity>> MONOLITH = register(
            "dimdoors:monolith",
            MonolithEntity::new,
			2f, 2.7f, false
    );

    public static final RegistrySupplier<EntityType<MaskEntity>> MASK = register(
            "dimdoors:mask",
            MaskEntity::new,
            0.9375f, 0.9375f, true
    );

    public static void init() {
        ENTITY_TYPES.register();
        EntityAttributeRegistry.register(MONOLITH, MonolithEntity::createMobAttributes);
        EntityAttributeRegistry.register(MASK, MonolithEntity::createMobAttributes);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.register(MONOLITH, MonolithRenderer::new);
//        EntityRendererRegistry.INSTANCE.register(MASK, MaskRenderer::new);
    }

    private static <E extends Entity> RegistrySupplier<EntityType<E>> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).canSpawnFarFromPlayer().fireImmune().build(null));
    }
}
