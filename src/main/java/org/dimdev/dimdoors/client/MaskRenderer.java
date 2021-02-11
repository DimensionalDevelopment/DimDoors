package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.entity.MaskEntity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class MaskRenderer extends EntityRenderer<MaskEntity> {
	public MaskRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(MaskEntity entity) {
        return new Identifier("dimdoors:mask");
    }
}
