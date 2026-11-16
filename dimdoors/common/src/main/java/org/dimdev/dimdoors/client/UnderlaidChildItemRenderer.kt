package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.ChildItem

object UnderlaidChildItemRenderer {
    val underlay: ItemStack = ModBlocks.DIMENSIONAL_PORTAL.asItem().defaultInstance


    //    @Override
    fun render(
        stack: ItemStack,
        mode: ItemDisplayContext?,
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val childItem = stack.item.castOrNull<ChildItem>()
            ?: throw UnsupportedOperationException("Can only use UnderlaidChildItemRenderer for ChildItems")

        matrices.pusPop {
        matrices.translate(0.5, 0.5, 0.5)

        val itemRenderer = Minecraft.getInstance().itemRenderer


        matrices.pusPop {
            childItem.transformUnderlay(matrices)
            matrices.scale(0.9f, 0.9f, 0.9f)
            itemRenderer.renderStatic(underlay, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, null, 0)
        }

        val originalItemStack =
            ItemStack(childItem.originalItem, stack.count).apply { applyComponents(stack.getComponents()) }

        matrices.pusPop {
            childItem.transformOverlay(matrices)
            itemRenderer.renderStatic(
                originalItemStack,
                ItemDisplayContext.NONE,
                light,
                overlay,
                matrices,
                vertexConsumers,
                null,
                0
            )
        }

        }
    }
}
