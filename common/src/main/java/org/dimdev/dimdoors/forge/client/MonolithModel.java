package org.dimdev.dimdoors.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.dimdev.dimdoors.entity.MonolithEntity;

@Environment(EnvType.CLIENT)
public class MonolithModel extends EntityModel<MonolithEntity> {
    private final ModelPart body;
    private int aggro;
    private int id;

    public MonolithModel(EntityRendererProvider.Context context) {
        super(MyRenderLayer::getMonolith);
        this.body = context.bakeLayer(ModEntityModelLayers.MONOLITH);
    }

    public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 0).addBox(-23.5F, -54, -6, 47, 108, 12, false), PartPose.ZERO);
        return LayerDefinition.create(modelData, 128, 128);
    }

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        final float minScaling = 0;
        final float maxScaling = 0.001f;

        // Use linear interpolation to scale how much jitter we want for our given aggro level
        float aggroScaling = minScaling + (maxScaling - minScaling) * aggro;

        // Calculate jitter - include entity ID to give Monoliths individual jitters
        float time = ((Minecraft.getInstance().getFrameTime() + 0xF1234568 * id) % 200000) / 50.0F;
        // We use random constants here on purpose just to get different wave forms
        var jitterX = (float) (aggroScaling * Math.sin(1.1f * time) * Math.sin(0.8f * time));
        var jitterY = (float) (aggroScaling * Math.sin(1.2f * time) * Math.sin(0.9f * time));
        var jitterZ = (float) (aggroScaling * Math.sin(1.3f * time) * Math.sin(0.7f * time));

        matrixStack.pushPose();
        matrixStack.translate(jitterX, jitterY, jitterZ);
        this.body.render(matrixStack, consumer, packedLight, packedOverlay);
        matrixStack.popPose();
    }

    @Override
	public void setupAnim(MonolithEntity monolith, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * 0.017453292F;
        this.body.xRot = headPitch * 0.017453292F;

        this.aggro = monolith.getAggro();
        this.id = monolith.getId();
    }
}
