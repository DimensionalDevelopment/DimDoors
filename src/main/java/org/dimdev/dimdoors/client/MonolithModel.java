package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.entity.MonolithEntity;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MonolithModel extends EntityModel<MonolithEntity> {
    private final ModelPart body;

    public MonolithModel(EntityRendererFactory.Context context) {
        super(MyRenderLayer::getMonolith);
        this.body = context.getPart(ModEntityModelLayers.MONOLITH);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-23.5F, -23.5F, 0, 49.0F, 49.0F, 1.0F, false), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 102, 51);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(matrixStack, consumer, packedLight, packedOverlay);
    }

    @Override
    public void setAngles(MonolithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yaw = netHeadYaw * 0.017453292F;
        this.body.pitch = headPitch * 0.017453292F;
    }
}
