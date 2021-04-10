package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.MobEntityRenderer;
import org.dimdev.dimdoors.entity.MonolithEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MonolithRenderer extends MobEntityRenderer<MonolithEntity, MonolithModel> {
    public static final List<RenderLayer> MONOLITH_TEXTURES = Stream.of(
            new Identifier("dimdoors:textures/mob/monolith/monolith0.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith1.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith2.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith3.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith4.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith5.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith6.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith7.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith8.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith9.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith10.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith11.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith12.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith13.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith14.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith15.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith16.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith17.png"),
            new Identifier("dimdoors:textures/mob/monolith/monolith18.png")
	).map(MyRenderLayer::getMonolith).collect(Collectors.toList());

    public MonolithRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MonolithModel(), 0);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(MonolithEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return MonolithRenderer.MONOLITH_TEXTURES.get(entity.getTextureState());
    }

    @Override
    public Identifier getTexture(MonolithEntity entity) {
        return null;
    }
}
