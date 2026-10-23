package org.dimdev.dimcore.api.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

fun interface CustomItemRenderer {
    fun render(stack: ItemStack, ctx: ItemDisplayContext, poseStack: PoseStack, buffers: MultiBufferSource, light: Int, overlay: Int)
}