package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.dimdev.dimdoors.entity.MonolithEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import org.jetbrains.annotations.Nullable;

public class MonolithRenderer extends LivingEntityRenderer<MonolithEntity, MonolithModel> {
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

    public MonolithRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
        super(dispatcher, new MonolithModel(), 0);
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
