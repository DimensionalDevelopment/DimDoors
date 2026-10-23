package org.dimdev.dimcore.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge
import org.dimdev.dimcore.api.client.ClientPlatform

class NeoForgeClientPlatform : ClientPlatform() {
    private val loginRunnables = mutableListOf<() -> Unit>()
    private val systemInfoProivders = mutableListOf<(MutableList<String>) -> Unit>()
    private val gameInfoProivders = mutableListOf<(MutableList<String>) -> Unit>()
    private val overlays = mutableListOf<(GuiGraphics, Player, DeltaTracker) -> Unit>()

    init {


        NeoForge.EVENT_BUS.addListener<CustomizeGuiOverlayEvent.DebugText> { event ->
            event.left.run { gameInfoProivders.forEach { it(this) } }
            event.right.run { systemInfoProivders.forEach { it(this) } }
        }

        NeoForge.EVENT_BUS.addListener<ClientPlayerNetworkEvent.LoggingIn> { loginRunnables.forEach { it.invoke() } }

        NeoForge.EVENT_BUS.addListener<RenderGuiLayerEvent.Post> { event ->
            val minecraft = Minecraft.getInstance()
            val player = minecraft.player ?: return@addListener
            if (event.name != VanillaGuiLayers.CAMERA_OVERLAYS || !minecraft.options.cameraType.isFirstPerson || player.isScoping) return@addListener
            overlays.forEach { it(event.guiGraphics, player, event.partialTick) }
        }
    }

    override fun register(type: RenderType, vararg blocks: Block) = blocks.forEach { block -> ItemBlockRenderTypes.setRenderLayer(block, type) }

    override fun onClientPlayerJoin(listener: () -> Unit) {
        loginRunnables.add(listener)
    }

    override fun onDebugText(
        type: ClientPlatform.DebugTextType,
        listener: (MutableList<String>) -> Unit
    ) {
        when(type) {
            ClientPlatform.DebugTextType.GAME -> gameInfoProivders
            ClientPlatform.DebugTextType.SYSTEM -> systemInfoProivders
        }.add(listener)
    }

    override fun onFirstPersonOverlay(listener: (GuiGraphics, Player, DeltaTracker) -> Unit) {
        overlays.add(listener)
    }

    override fun fluidSprite(fluid: Fluid): TextureAtlasSprite = fluid.extensions.stillTexture.let { Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(it) }

    override fun fluidTint(fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?): Int {
        val extensions = fluid.extensions

        return if (level != null && pos != null) extensions.getTintColor(fluid.defaultFluidState(), level, pos) else extensions.tintColor
    }
}

private val Fluid.extensions: IClientFluidTypeExtensions get() = IClientFluidTypeExtensions.of(this)