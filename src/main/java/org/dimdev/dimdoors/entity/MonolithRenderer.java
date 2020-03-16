package org.dimdev.dimdoors.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

public class MonolithRenderer extends EntityRenderer<MonolithEntity> {
    private final EntityRendererRegistry.Context context;

    protected MonolithRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
        super(dispatcher);
        this.context = context;
    }

    @Override
    public Identifier getTexture(MonolithEntity entity) {
        return new Identifier("dimdoors:monolith");
    }
}
