package org.dimdev.dimdoors.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.client.MonolithModel;

public class MonolithRenderer extends EntityRenderer<MonolithEntity> {
    private MonolithModel model = new MonolithModel();

    protected MonolithRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
        super(dispatcher);
    }

    @Override
    public void render(MonolithEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);

        matrixStack.push();
        //model.render(entity);
        matrixStack.pop();

    }

    @Override
    public Identifier getTexture(MonolithEntity entity) {
        return new Identifier("dimdoors:monolith");
    }
}
