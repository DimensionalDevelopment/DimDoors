package org.dimdev.dimdoors.entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.client.MonolithRenderer;

public class ModEntityTypes {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MODID);

    public static final RegistryObject<EntityType<MonolithEntity>> MONOLITH = ENTITY_TYPES.register("monolith", () -> register(
			"dimdoors:monolith", MonolithEntity::new,
			2f, 2.7f, false
	));

    public static final RegistryObject<EntityType<MaskEntity>> MASK = ENTITY_TYPES.register("mask", () -> register(
            "dimdoors:mask",
            MaskEntity::new,
            0.9375f, 0.9375f, true
    ));

    public static void init(IEventBus event) {
        ENTITY_TYPES.register(event);
		event.addListener(ModEntityTypes::registerAttributes);
    }

	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MONOLITH.get(), MonolithEntity.createMobAttributes().build());
		event.put(MASK.get(), MonolithEntity.createMobAttributes().build());
	}

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.register(MONOLITH, MonolithRenderer::new);
//        EntityRendererRegistry.INSTANCE.register(MASK, MaskRenderer::new);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, float width, float height, boolean fixed) {
        return EntityType.Builder.of(factory, MobCategory.MONSTER).sized(width, height).canSpawnFarFromPlayer().fireImmune().build(null);
    }
}
