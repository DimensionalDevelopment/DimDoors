package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import org.dimdev.dimdoors.entity.MonolithEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MonolithRenderer extends MobEntityRenderer<MonolithEntity, MonolithModel> {
    public static final List<Identifier> MONOLITH_TEXTURES = Stream.of(
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
	).collect(Collectors.toList());

    private static MonolithModel INSTANCE;

    public MonolithRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, INSTANCE = new MonolithModel(ctx), 0);
    }

    public static MonolithModel getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(MonolithEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
    	super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

	@Override
	protected void scale(MonolithEntity entity, MatrixStack matrices, float amount) {
		matrices.scale(entity.getScale(), entity.getScale(), entity.getScale());
	}

	@Override
    protected boolean hasLabel(MonolithEntity mobEntity) {
        return false;
    }

    @Override
    public Identifier getTexture(MonolithEntity entity) {
        return MonolithRenderer.MONOLITH_TEXTURES.get(entity.getTextureState());
    }
}
