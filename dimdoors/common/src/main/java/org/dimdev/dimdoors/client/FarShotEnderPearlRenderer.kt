package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import org.dimdev.dimdoors.entity.FarShotEnderPearlEntity

open class FarShotEnderPearlRenderer protected constructor(context: EntityRendererProvider.Context) :
    ThrownItemRenderer<FarShotEnderPearlEntity>(context) {
    private val itemRenderer = context.itemRenderer

    override fun render(
        entity: FarShotEnderPearlEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation())
        this.itemRenderer.renderStatic(
            entity.item,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            entity.level(),
            entity.id
        )
        poseStack.popPose()

        if (this.shouldShowName(entity)) {
            this.renderNameTag(entity, entity.displayName!!, poseStack, buffer, packedLight, partialTicks)
        }
    }
}
