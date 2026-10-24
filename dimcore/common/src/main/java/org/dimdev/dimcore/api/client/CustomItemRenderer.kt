package org.dimdev.dimcore.api.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

fun interface CustomItemRenderer {
    fun render(stack: ItemStack, ctx: ItemDisplayContext, poseStack: PoseStack, buffers: MultiBufferSource, light: Int, overlay: Int)
}

class CustomBlockEntityItemRenderer(block: Block,function: (BlockPos, BlockState) -> BlockEntity) : CustomItemRenderer {
    val blockEntity = function.invoke(BlockPos.ZERO, block.defaultBlockState())

    override fun render(
        stack: ItemStack,
        ctx: ItemDisplayContext,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        Minecraft.getInstance().blockEntityRenderDispatcher.renderItem(blockEntity, poseStack, buffers, light, overlay)
    }
}