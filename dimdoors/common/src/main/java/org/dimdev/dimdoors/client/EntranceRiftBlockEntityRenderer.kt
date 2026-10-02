package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import org.dimdev.dimdoors.api.client.DefaultTransformation
import org.dimdev.dimdoors.api.client.DefaultTransformation.Companion.fromDirection
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer.renderDimensionalPortal
import org.dimdev.dimdoors.api.client.Transformer
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

class EntranceRiftBlockEntityRenderer<T : EntranceRiftBlockEntity<T>>(context: BlockEntityRendererProvider.Context) : RiftBlockEntityRenderer<T>(context) {
    override fun render(
        rift: T,
        tickDelta: Float,
        matrices: PoseStack,
        multiBufferSource: MultiBufferSource,
        breakingProgress: Int,
        alpha: Int
    ) {
        super.render(rift, tickDelta, matrices, multiBufferSource, breakingProgress, alpha)

        val state = rift.renderBlockState ?: return

        renderDimensionalPortal(state, matrices, multiBufferSource, breakingProgress, alpha)

        //        renderBlockState(state, blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
//        if (state.getBlock() instanceof DoorBlock) {
//            matrixStack.pushPose();
//
//            matrixStack.translate(0, 1, 0);
//            renderBlockState(state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
//            matrixStack.popPose();
//
//        }
    }

    private fun renderBlockState(
        renderState: BlockState,
        random: RandomSource,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val model = Minecraft.getInstance().modelManager.blockModelShaper.getBlockModel(renderState)
        val renderType = ItemBlockRenderTypes.getRenderType(renderState, false)
        val vertexConsumer = vertexConsumerProvider.getBuffer(renderType)

        for (direction in Direction.entries) {
            val quads = model.getQuads(renderState, direction, random)
            renderQuads(matrixStack, vertexConsumer, quads, light, overlay)
        }

        val quads = model.getQuads(renderState, null, random)
        renderQuads(matrixStack, vertexConsumer, quads, light, overlay)
    }

    private fun renderQuads(
        stack: PoseStack,
        consumer: VertexConsumer,
        quads: MutableList<BakedQuad>,
        light: Int,
        overlay: Int
    ) {
        val pose = stack.last()
        for (quad in quads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay)
        }
    }

    companion object {
        fun getTransformer(blockEntity: EntranceRiftBlockEntity<*>): Transformer? {
            if (blockEntity.blockState.block is DimensionalTrapDoorBlock<*>) {
                return if (blockEntity.blockState
                        .getValue(TrapDoorBlock.HALF) == Half.TOP
                ) DefaultTransformation.TOP_TRAPDOOR else DefaultTransformation.BOTTOMM_TRAPDOOR
            }

            return fromDirection(blockEntity.orientation)
        }
    }
}
