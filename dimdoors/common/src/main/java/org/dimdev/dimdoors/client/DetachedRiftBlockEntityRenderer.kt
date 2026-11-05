package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.client.tesseract.Tesseract.draw
import org.dimdev.dimdoors.rift.RiftUtils

class DetachedRiftBlockEntityRenderer(context: BlockEntityRendererProvider.Context) : RiftBlockEntityRenderer<DetachedRiftBlockEntity>(context) {
    val tesseractRenderType: RenderType by lazy { RenderType.entityTranslucent(TESSERACT_PATH) }

    override fun render(
        rift: DetachedRiftBlockEntity,
        tickDelta: Float,
        matrices: PoseStack,
        multiBufferSource: MultiBufferSource,
        breakingProgress: Int,
        alpha: Int
    ) {
        super.render(rift, tickDelta, matrices, multiBufferSource, breakingProgress, alpha)

        val riftCoreVisibility = if (config.graphicsConfig.showRiftCore) 1f else RiftUtils.showRiftTimer.visibility
        if (riftCoreVisibility > 0) {
            this.renderTesseract(
                multiBufferSource.getBuffer(tesseractRenderType),
                rift,
                matrices,
                riftCoreVisibility
            )
        }

        this.renderCrack(multiBufferSource, matrices, rift)
    }

    private fun renderCrack(vc: MultiBufferSource, matrices: PoseStack, rift: DetachedRiftBlockEntity) = matrices.pusPop {
        matrices.translate(0.5f, 0.5f, 0.5f)
        matrices.mulPose(Axis.YP.rotationDegrees(rift.riftYaw))

        val size = rift.data.size

        RiftCrackRenderer.drawCrack(
            matrices,
            RiftCrackRenderer.ChromaticAberration.buffer(vc),
            0f,
            RiftCurves.CURVES[rift.curveID],
            size / 150f,
        )
    }

    private fun renderTesseract(
        vc: VertexConsumer,
        rift: DetachedRiftBlockEntity,
        matrices: PoseStack,
        alphaMultiplier: Float
    ) {
        val radian = (DimensionalDoorsClient.renderTick * 10 % 360) * Mth.DEG_TO_RAD
        var color = rift.color
        if (color == RGBA.NONE) {
            color = DEFAULT_COLOR
        }
        color = RGBA(color.red, color.green, color.blue, color.alpha * alphaMultiplier)

        matrices.pushPose()

        matrices.translate(0.5, 0.5, 0.5)
        matrices.scale(0.25f, 0.25f, 0.25f)

        draw(matrices.last().pose(), vc, color, radian)

        matrices.popPose()
    }

    companion object {
        val TESSERACT_PATH: ResourceLocation = DimensionalDoors.id("textures/other/tesseract.png")
        private val DEFAULT_COLOR = RGBA(1f, 0.5f, 1f, 1f)
    }
}

fun PoseStack.pusPop(function: () -> Unit) {
    this.pushPose()
    function()
    this.popPose()
}
