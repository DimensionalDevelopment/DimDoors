package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.MonolithEntity;

@Environment(EnvType.CLIENT)
public class MonolithRenderer extends MobRenderer<MonolithEntity, MonolithModel> {
    public static final List<ResourceLocation> MONOLITH_TEXTURES = Stream.of(
            DimensionalDoors.id("textures/mob/monolith/monolith0.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith1.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith2.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith3.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith4.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith5.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith6.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith7.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith8.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith9.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith10.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith11.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith12.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith13.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith14.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith15.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith16.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith17.png"),
            DimensionalDoors.id("textures/mob/monolith/monolith18.png")
	).collect(Collectors.toList());

    private static MonolithModel INSTANCE;

    public MonolithRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, INSTANCE = new MonolithModel(ctx), 0);
    }

    public static MonolithModel getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(MonolithEntity mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
    	super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

	@Override
	protected void scale(MonolithEntity entity, PoseStack matrices, float amount) {
		matrices.scale(entity.getScale(), entity.getScale(), entity.getScale());
	}

	@Override
    protected boolean shouldShowName(MonolithEntity mobEntity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(MonolithEntity entity) {
        return MonolithRenderer.MONOLITH_TEXTURES.get(entity.getTextureState());
    }
}
