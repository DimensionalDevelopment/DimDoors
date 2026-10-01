package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.DoorBlock
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer.renderDimensionalPortal
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity

class DialingDoorBlockEntityRenderer(private val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<DialingDoorBlockEntity> {
    override fun render(
        blockEntity: DialingDoorBlockEntity,
        tickDelta: Float,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        renderDimensionalPortal(blockEntity.blockState, matrixStack, vertexConsumerProvider, light, overlay)

        if (blockEntity.blockState.getValue(DoorBlock.OPEN)) return
        renderDialingText(blockEntity, matrixStack, vertexConsumerProvider, light)
    }

    private fun renderDialingText(
        blockEntity: DialingDoorBlockEntity,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int
    ) {
        val font = context.font
        val front = blockEntity.orientation.opposite
        matrixStack.pushPose()

        matrixStack.translate(
            0.5 + front.stepX * TEXT_FORWARD_OFFSET,
            1.0,
            0.5 + front.stepZ * TEXT_FORWARD_OFFSET
        )

        matrixStack.mulPose(Axis.YP.rotationDegrees(-front.toYRot()))


        val address = blockEntity.address

        matrixStack.pushPose()
        matrixStack.translate(-1.5 * VOXEL_SIZE, 10.1 * VOXEL_SIZE, 0.0)
        renderText(address.dial1.toInt().toString(), matrixStack, vertexConsumerProvider, light, font)
        matrixStack.popPose()

        matrixStack.pushPose()
        matrixStack.translate(-1.5 * VOXEL_SIZE, 3.7 * VOXEL_SIZE, 0.0)
        renderText(address.dial2.toInt().toString(), matrixStack, vertexConsumerProvider, light, font)
        matrixStack.popPose()

        matrixStack.pushPose()
        matrixStack.translate(-1.5 * VOXEL_SIZE, -3.3 * VOXEL_SIZE, 0.0)
        renderText(address.dial3.toInt().toString(), matrixStack, vertexConsumerProvider, light, font)
        matrixStack.popPose()

        matrixStack.popPose()
    }

    private fun renderText(
        text: String,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        font: Font
    ) {
        matrixStack.scale(WIDTH_TEXT_SCALE, -HEIGHT_TEXT_SCALE, 1f)

        font.drawInBatch(
            text,
            0f,
            0f,
            0x000000,
            false,
            matrixStack.last().pose(),
            vertexConsumerProvider,
            Font.DisplayMode.NORMAL,
            0x00000000,
            light
        )
    }

    companion object {
        const val VOXEL_SIZE: Float = 1.0f / 16.0f
        private const val HEIGHT_TEXT_SCALE: Float = VOXEL_SIZE * 4.6f / 7f
        const val WIDTH_TEXT_SCALE: Float = VOXEL_SIZE * 0.6f
        private val TEXT_FORWARD_OFFSET: Double = 0.501 + VOXEL_SIZE
    }
}
