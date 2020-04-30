package org.dimdev.dimdoors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.dimdev.dimdoors.entity.MonolithEntity;

@Environment(EnvType.CLIENT)
public class MonolithModel extends EntityModel<MonolithEntity> {
    private final ModelPart body;

    public MonolithModel() {
        super();
        textureWidth = 256;
        textureHeight = 256;

        body = new ModelPart(this, 0, 0);
        body.addCuboid(-24F, -108F / 1.3F, -6F, 48, 108, 12);
    }

    @Override
    public void setAngles(MonolithEntity entity, float f, float g, float h, float i, float j) {

    }

    @Override
    public void animateModel(MonolithEntity entity, float f, float g, float h) {
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        this.body.render(matrixStack, vertexConsumer, i, j, f, g, h, k);
    }
}
