package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.NonNull;

public class MonolithModel extends EntityModel<MonolithRenderState> {

    public MonolithModel(EntityRendererProvider.Context context) {
        super(context.bakeLayer(ModEntityModelLayers.MONOLITH), MyRenderLayer::getMonolith);
    }

    public static LayerDefinition getTexturedModelData() {
    MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 0).addBox(-23.5F, -54, -6, 47, 108, 12, false), PartPose.ZERO);
        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void setupAnim(@NonNull MonolithRenderState state) {
        super.setupAnim(state);

        final float minScaling = 0;
        final float maxScaling = 0.001f;

        // Use linear interpolation to scale how much jitter we want for our given aggro level
        float aggroScaling = minScaling + (maxScaling - minScaling) * state.aggro;

        // Calculate jitter - include entity ID to give Monoliths individual jitters
        float time = ((Minecraft.getInstance().getFrameTimeNs() + 0xF1234568 * state.id) % 200000) / 50.0F;
        // We use random constants here on purpose just to get different wave forms
        this.root.x += (float) (aggroScaling * Math.sin(1.1f * time) * Math.sin(0.8f * time));
        this.root.y += (float) (aggroScaling * Math.sin(1.2f * time) * Math.sin(0.9f * time));
        this.root.z += (float) (aggroScaling * Math.sin(1.3f * time) * Math.sin(0.7f * time));

        this.root.yRot = state.yRot * 0.017453292F;
        this.root.xRot = state.xRot * 0.017453292F;
    }
}
