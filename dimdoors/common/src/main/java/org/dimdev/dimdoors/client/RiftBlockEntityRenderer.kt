package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.entity.BlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.client.RenderUtils.renderTextLines
import org.dimdev.dimdoors.item.ModItems
import java.util.function.Consumer

abstract class RiftBlockEntityRenderer<T>(private val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<T> where T : BlockEntity, T : Rift {
    override fun render(rift: T, f: Float, matrices: PoseStack, multiBufferSource: MultiBufferSource, i: Int, j: Int) {
        val minecraft = Minecraft.getInstance()

        if (minecraft.player != null && minecraft.player!!.getItemInHand(InteractionHand.MAIN_HAND)
                .`is`(ModItems.RIFT_CONFIGURATION_TOOL)
        ) {
            matrices.pushPose()

            matrices.translate(0.5, 1.25, 0.5)
            matrices.mulPose(minecraft.getBlockEntityRenderDispatcher().camera.rotation())
            matrices.scale(0.025f, -0.025f, 0.025f)

            renderTextLines(
                Util.make<ArrayList<Component?>?>(
                    ArrayList<Component?>(),
                    Consumer { list: ArrayList<Component?>? ->
                        rift!!.gatherDebug(
                            Consumer { e: Component? -> list!!.add(e) })
                    }), matrices, multiBufferSource, context.getFont(), LightTexture.FULL_BRIGHT
            )

            matrices.popPose()
        }
    }
}
