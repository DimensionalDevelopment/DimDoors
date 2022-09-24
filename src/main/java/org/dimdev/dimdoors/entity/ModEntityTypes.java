package org.dimdev.dimdoors.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.client.MaskRenderer;
import org.dimdev.dimdoors.client.MonolithRenderer;
import org.dimdev.dimdoors.entity.masktypes.*;

public class ModEntityTypes {
    public static final EntityType<MonolithEntity> MONOLITH = register("dimdoors:monolith", MonolithEntity::new,
			SpawnGroup.MONSTER, 2f, 2.7f, false);

    public static final EntityType<AbstractMaskEntity> CYCLOPS_MASK = register("dimdoors:cyclops_mask", CyclopsMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);
	public static final EntityType<AbstractMaskEntity> BI_MASK = register("dimdoors:bi_mask", BiMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);
	public static final EntityType<AbstractMaskEntity> ENLIGHTENED_MASK = register("dimdoors:enlightened_mask", EnlightenedMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);
	public static final EntityType<AbstractMaskEntity> FORESIGHT_MASK = register("dimdoors:foresight_mask", ForesightMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);
	public static final EntityType<AbstractMaskEntity> SKULKING_MASK = register("dimdoors:skulking_mask", SkulkingMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);
	public static final EntityType<AbstractMaskEntity> BLACK_MASK = register("dimdoors:black_mask", BlackMaskEntity::new,
			SpawnGroup.MONSTER, 0.9375f, 0.9375f, true);

    public static void init() {
        FabricDefaultAttributeRegistry.register(MONOLITH, MonolithEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(CYCLOPS_MASK, AbstractMaskEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(BI_MASK, AbstractMaskEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ENLIGHTENED_MASK, AbstractMaskEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(FORESIGHT_MASK, AbstractMaskEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(SKULKING_MASK, AbstractMaskEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(BLACK_MASK, AbstractMaskEntity.createMobAttributes());
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.register(MONOLITH, MonolithRenderer::new);
        EntityRendererRegistry.register(CYCLOPS_MASK, MaskRenderer::new);
		EntityRendererRegistry.register(BI_MASK, MaskRenderer::new);
		EntityRendererRegistry.register(ENLIGHTENED_MASK, MaskRenderer::new);
		EntityRendererRegistry.register(FORESIGHT_MASK, MaskRenderer::new);
		EntityRendererRegistry.register(SKULKING_MASK, MaskRenderer::new);
		EntityRendererRegistry.register(BLACK_MASK, MaskRenderer::new);
    }

    private static <E extends Entity> EntityType<E> register(String id, EntityType.EntityFactory<E> factory, SpawnGroup group, float width, float height, boolean fixed) {
        return Registry.register(Registry.ENTITY_TYPE, id, FabricEntityTypeBuilder.create(group, factory).dimensions(new EntityDimensions(width, height, fixed)).spawnableFarFromPlayer().fireImmune().build());
    }
}
