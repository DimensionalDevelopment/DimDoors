package org.dimdev.dimdoors.client;

import java.util.Collections;

import org.dimdev.dimdoors.entity.MonolithEntity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MonolithModel extends EntityModel<MonolithEntity> {
    private final ModelPart body;

    public MonolithModel() {
        ModelPart.Cuboid bodyCuboid = new ModelPart.Cuboid(0, 0, -23.5F, -23.5F, 0, 49.0F, 4.90F, 1.0F, 0, 0, 0, false, 102, 51);
		this.body = new ModelPart(Collections.singletonList(bodyCuboid), Collections.emptyMap());
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
