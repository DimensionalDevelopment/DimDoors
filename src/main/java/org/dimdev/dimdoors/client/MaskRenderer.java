package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.entity.MaskEntity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class MaskRenderer extends EntityRenderer<MaskEntity> {
    private final EntityRendererRegistry.Context context;

    public MaskRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
        super(dispatcher);
        this.context = context;
    }

    @Override
    public Identifier getTexture(MaskEntity entity) {
        return new Identifier("dimdoors:mask");
    }
}
