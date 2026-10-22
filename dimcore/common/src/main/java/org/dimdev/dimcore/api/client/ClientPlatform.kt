package org.dimdev.dimcore.api.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid

interface ClientPlatform {
    fun register(type: RenderType, vararg blocks: Block)
    fun onClientPlayerJoin(listener: () -> Unit)
    fun onDebugText(type: DebugTextType, listener: (MutableList<String>) -> Unit)
    fun onFirstPersonOverlay(listener: (GuiGraphics, Player, DeltaTracker) -> Unit)
    fun fluidSprite(fluid: Fluid): TextureAtlasSprite = Minecraft.getInstance().blockRenderer.blockModelShaper.getParticleIcon(fluid.defaultFluidState().createLegacyBlock())
    fun fluidTint(fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?): Int = Minecraft.getInstance().blockColors.getColor(fluid.defaultFluidState().createLegacyBlock(), level, pos, 0)

    enum class DebugTextType {
        GAME, SYSTEM
    }
}
