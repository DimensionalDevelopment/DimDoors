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
        textureWidth = 256;
        textureHeight = 256;

        body = new ModelPart(this);
        body.setPivot(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(0, 0).addCuboid(-24.0F, -108.0F, -6.0F, 48.0F, 108.0F, 12.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void setAngles(MonolithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        body.yaw = netHeadYaw * 0.017453292F;
        body.pitch = headPitch * 0.017453292F;
    }
}