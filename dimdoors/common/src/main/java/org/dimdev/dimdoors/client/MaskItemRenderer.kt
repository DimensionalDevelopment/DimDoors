package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.entity.mask.MaskType
import org.dimdev.dimdoors.item.ModDataComponentTypes

object MaskItemRenderer {
    fun render(
        stack: ItemStack,
            ctx: ItemDisplayContext,
            poseStack: PoseStack,
            buffers: MultiBufferSource,
            light: Int,
            overlay: Int
    ) {
        val texture = stack.getOrDefault(ModDataComponentTypes.MASK_TYPE, MaskType.CYCLOP).texture
        val model = MaskRenderer.instance

        poseStack.pusPop {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f))
            poseStack.translate(-0.5f, -1.5f, 0.5f)
            model.renderToBuffer(poseStack, buffers.getBuffer(model.renderType(texture)), light, overlay)
        }
    }
}
