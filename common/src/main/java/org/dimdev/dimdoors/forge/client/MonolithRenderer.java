package org.dimdev.dimdoors.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.MonolithEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class MonolithRenderer extends MobRenderer<MonolithEntity, MonolithModel> {
    public static final List<ResourceLocation> TRANSPARENT = Stream.of(
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_0.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_1.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_2.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_3.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_4.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_5.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_6.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_7.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_8.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_9.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_10.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_11.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_12.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_13.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_14.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_15.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_16.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_17.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_18.png")
	).collect(Collectors.toList());

    public static final List<ResourceLocation> SOLID = Stream.of(
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_0.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_1.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_2.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_3.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_4.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_5.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_6.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_7.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_8.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_9.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_10.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_11.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_12.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_13.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_14.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_15.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_16.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_17.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_18.png")
    ).collect(Collectors.toList());



    private static MonolithModel INSTANCE;

    public MonolithRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, INSTANCE = new MonolithModel(ctx), 0);
    }

    public static MonolithModel getInstance() {
        return INSTANCE;
    }

	@Override
	protected void scale(MonolithEntity entity, PoseStack matrices, float amount) {
		matrices.scale(entity.getScale(), entity.getScale(), entity.getScale());
	}

    @Override
    public void render(MonolithEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.getSolid()) {
            poseStack.pushPose();

            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

            poseStack.popPose();

        }
    }

    @Override
    protected boolean shouldShowName(MonolithEntity mobEntity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(MonolithEntity entity) {
        return SOLID.get(entity.getTextureState());
    }
}
