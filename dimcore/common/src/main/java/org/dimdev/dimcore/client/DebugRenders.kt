package org.dimdev.dimcore.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource

object DebugRenders {
    private val renderers = mutableListOf<(PoseStack, MultiBufferSource) -> Unit>()

    fun register(renderer: (PoseStack, MultiBufferSource) -> Unit) {
        renderers += renderer
    }

    @JvmStatic
    fun render(poseStack: PoseStack, buffers: MultiBufferSource) = renderers.forEach { it(poseStack, buffers) }
}
