package org.dimdev.dimcore.api.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid

abstract class ClientPlatform {
    val itemRenderers = mutableMapOf<Item, CustomItemRenderer>()

    abstract fun register(type: RenderType, vararg blocks: Block)

    abstract fun onClientPlayerJoin(listener: () -> Unit)
    abstract fun onDebugText(type: DebugTextType, listener: (MutableList<String>) -> Unit)
    abstract fun onFirstPersonOverlay(listener: (GuiGraphics, Player, DeltaTracker) -> Unit)
    open fun fluidSprite(fluid: Fluid): TextureAtlasSprite = Minecraft.getInstance().blockRenderer.blockModelShaper.getParticleIcon(fluid.defaultFluidState().createLegacyBlock())
    open fun fluidTint(fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?): Int = Minecraft.getInstance().blockColors.getColor(fluid.defaultFluidState().createLegacyBlock(), level, pos, 0)

    fun registerCustomItemRenderer(renderer: CustomItemRenderer, vararg items: ItemLike) {
        items.forEach {
            itemRenderers[it.asItem()] = renderer
        }
    }

    enum class DebugTextType {
        GAME, SYSTEM
    }
}
