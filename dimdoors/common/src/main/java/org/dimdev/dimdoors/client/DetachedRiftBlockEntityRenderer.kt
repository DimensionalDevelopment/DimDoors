package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.client.tesseract.Tesseract.draw
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.rift.RiftUtils

class DetachedRiftBlockEntityRenderer(context: BlockEntityRendererProvider.Context) : RiftBlockEntityRenderer<DetachedRiftBlockEntity>(context) {
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
                multiBufferSource.getBuffer(RenderType.entityTranslucent(TESSERACT_PATH)),
                rift,
                matrices,
                riftCoreVisibility
            )
        }

        if (this.shouldRenderDecayRadiusDebug()) {
            val renderType = RenderType.debugStructureQuads()
            this.renderDecayRadius(renderType, multiBufferSource.getBuffer(renderType), rift, matrices)
        }

        this.renderCrack(multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TESSERACT_PATH)), matrices, rift)
    }

    private fun shouldRenderDecayRadiusDebug(): Boolean {
        val minecraft = Minecraft.getInstance()
        return minecraft.player != null && minecraft.player!!.getItemInHand(InteractionHand.MAIN_HAND).`is`(ModItems.RIFT_CONFIGURATION_TOOL)
    }

    private fun renderDecayRadius(
        renderType: RenderType,
        vc: VertexConsumer,
        rift: DetachedRiftBlockEntity,
        matrices: PoseStack
    ) {
        val radius = rift.decayRadius
        if (radius <= 0) {
            return
        }

        var color = rift.color
        if (color == RGBA.NONE) {
            color = DEFAULT_COLOR
        }

        val alpha: Float = DECAY_RADIUS_ALPHA * color.alpha

        matrices.pushPose()
        matrices.translate(0.5f, 0.5f, 0.5f)

        RenderUtils.renderSolidColorSphere(
            renderType,
            vc,
            matrices,
            (radius + 1).toFloat(),
            color.red,
            color.green,
            color.blue,
            alpha,
            DECAY_RADIUS_LATITUDE_SEGMENTS,
            DECAY_RADIUS_LONGITUDE_SEGMENTS
        )

        matrices.popPose()
    }

    private fun renderCrack(vc: VertexConsumer, matrices: PoseStack, rift: DetachedRiftBlockEntity) {
        matrices.pushPose()
        matrices.translate(0.5f, 0.5f, 0.5f)
        matrices.mulPose(Axis.YP.rotationDegrees(rift.riftYaw))
        RiftCrackRenderer.drawCrack(
            matrices.last().pose(),
            vc,
            0f,
            RiftCurves.CURVES[rift.curveID],
            config.graphicsConfig.riftSize * rift.data.size / 150,
            0
        )

        matrices.popPose()
    }

    private fun renderTesseract(
        vc: VertexConsumer,
        rift: DetachedRiftBlockEntity,
        matrices: PoseStack,
        alphaMultiplier: Float
    ) {
        val radian = (DimensionalDoorsClient.INSTANCE.renderTick * 10 % 360) * Mth.DEG_TO_RAD
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
        private const val DECAY_RADIUS_ALPHA = 0.18f
        private const val DECAY_RADIUS_LATITUDE_SEGMENTS = 12
        private const val DECAY_RADIUS_LONGITUDE_SEGMENTS = 24
    }
}
