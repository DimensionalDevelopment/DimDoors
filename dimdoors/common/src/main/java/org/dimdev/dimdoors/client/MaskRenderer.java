package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.mask.MaskEntity;
import org.jetbrains.annotations.NotNull;

public class MaskRenderer extends MobRenderer<MaskEntity, MaskModel> {
    private static final ResourceLocation TEXTURE = DimensionalDoors.id("textures/mob/mask/mask.png");

    public MaskRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MaskModel(ctx), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MaskEntity entity) {
        return TEXTURE;
    }
}
