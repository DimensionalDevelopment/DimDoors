package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.dimdev.dimdoors.entity.FarShotEnderPearlEntity;
import org.jetbrains.annotations.NotNull;

public class FarShotEnderPearlRenderer extends ThrownItemRenderer<FarShotEnderPearlEntity> {
    private final ItemRenderer itemRenderer;

    protected FarShotEnderPearlRenderer(EntityRendererProvider.Context context) {
        super(context);

        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FarShotEnderPearlEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();

        if (this.shouldShowName(entity)) {
            this.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight, partialTicks);
        }
    }
}
