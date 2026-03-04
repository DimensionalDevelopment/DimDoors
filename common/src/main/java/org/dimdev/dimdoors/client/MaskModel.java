package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.dimdev.dimdoors.entity.mask.MaskEntity;

public class MaskModel extends HierarchicalModel<MaskEntity> {
    private final ModelPart root;

    public MaskModel(EntityRendererProvider.Context root) {
        this.root = root.bakeLayer(ModEntityModelLayers.MASK);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mask = partdefinition.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -1.6F));

        mask.addOrReplaceChild("toprightlimb", CubeListBuilder.create(), PartPose.offset(4.0F, -7.0F, 0.0F))
                .addOrReplaceChild("toprightlimb_r1", CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -0.9F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F));

        mask.addOrReplaceChild("middlerightlimb", CubeListBuilder.create(), PartPose.offset(4.0F, -4.0F, 0.0F))
                .addOrReplaceChild("middlerightlimb_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F));

        mask.addOrReplaceChild("bottomrightlimb", CubeListBuilder.create(), PartPose.offset(4.0F, -1.0F, 0.0F))
                .addOrReplaceChild("bottomrightlimb_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -1.1F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F));

        mask.addOrReplaceChild("topleftlimb", CubeListBuilder.create(), PartPose.offset(-4.0F, -7.0F, 0.0F))
                .addOrReplaceChild("Topleftlimb_r1", CubeListBuilder.create().texOffs(10, 9).addBox(-1.0F, -0.9F, -4.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3998F, 0.0F));

        mask.addOrReplaceChild("middleleftlimb", CubeListBuilder.create(), PartPose.offset(-4.0F, -4.0F, 0.0F))
                .addOrReplaceChild("middleleftlimb_r1", CubeListBuilder.create().texOffs(10, 15).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3998F, 0.0F));

        mask.addOrReplaceChild("bottomleftlimb", CubeListBuilder.create(), PartPose.offset(-4.0F, -1.0F, 0.0F))
                .addOrReplaceChild("bottomleftlimb_r1", CubeListBuilder.create().texOffs(10, 21).addBox(-1.0F, -1.1F, -4.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3998F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(MaskEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleState, MaskAnimations.IDLE, ageInTicks);
        this.animate(entity.spottedState, MaskAnimations.SPOTTED, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}