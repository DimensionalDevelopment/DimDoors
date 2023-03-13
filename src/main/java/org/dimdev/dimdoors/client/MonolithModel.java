package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.dimdev.dimdoors.entity.MonolithEntity;

@Environment(Dist.CLIENT)
public class MonolithModel extends EntityModel<MonolithEntity> {
    private final ModelPart body;

    public MonolithModel(EntityRendererProvider.Context context) {
        super(MyRenderLayer::getMonolith);
        this.body = context.bakeLayer(ModEntityModelLayers.MONOLITH);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-23.5F, -23.5F, 0, 49.0F, 49.0F, 1.0F, false), PartPose.ZERO);
        return LayerDefinition.create(modelData, 102, 51);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		matrixStack.pushPose();
		matrixStack.scale(3.0625f, 3.0625f, 3.0625f);

		PoseStack.Pose entry = matrixStack.last();

		consumer.vertex(entry.pose(), -1, -0.5f, 0)
				.color(red, green, blue, alpha)
				.uv(0,0)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(entry.normal(), 0, 0, 1)
				.endVertex();
		consumer.vertex(entry.pose(), -1, 0.5f, 0)
				.color(red, green, blue, alpha)
				.uv(0, 1)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(entry.normal(), 0, 0, 1)
				.endVertex();
		consumer.vertex(entry.pose(), 1, 0.5f, 0)
				.color(red, green, blue, alpha)
				.uv(1,1)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(entry.normal(), 0, 0, 1)
				.endVertex();
		consumer.vertex(entry.pose(), 1, -0.5f, 0)
				.color(red, green, blue, alpha)
				.uv(1, 0)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(entry.normal(), 0, 0, 1)
				.endVertex();

		matrixStack.popPose();

//        this.body.render(matrixStack, consumer, packedLight, packedOverlay);
    }

    @Override
    public void setupAnim(MonolithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * 0.017453292F;
        this.body.xRot = headPitch * 0.017453292F;
    }
}
