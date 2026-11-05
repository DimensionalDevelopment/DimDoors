package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.client.RenderUtils.renderTextLines

abstract class RiftBlockEntityRenderer<T>(private val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<T> where T : BlockEntity, T : Rift<T> {


    override fun render(rift: T, tickDelta: Float, matrices: PoseStack, multiBufferSource: MultiBufferSource, breakingProgress: Int, alpha: Int) {
        val minecraft = Minecraft.getInstance()

        if (DimensionalDoorsClient.eyePieceActive) {
            val texts = mutableListOf<Component>().also{ rift.gatherDebug(it::add) }.takeIf { it.isNotEmpty() } ?: return

            matrices.pusPop {

                matrices.translate(0.5, 1.25, 0.5)
                matrices.mulPose(minecraft.blockEntityRenderDispatcher.camera.rotation())
                matrices.scale(0.025f, -0.025f, 0.025f)

                renderTextLines(
                    texts,
                    matrices,
                    multiBufferSource,
                    context.font,
                    rift.textColor,
                    LightTexture.FULL_BRIGHT
                )
            }
        }
    }
}
