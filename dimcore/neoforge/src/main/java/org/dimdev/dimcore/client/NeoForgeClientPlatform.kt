package org.dimdev.dimcore.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.common.NeoForge
import org.dimdev.dimcore.api.client.ClientPlatform
import java.util.function.Consumer
import kotlin.collections.forEach

class NeoForgeClientPlatform : ClientPlatform {
    private val loginRunnables = mutableListOf<() -> Unit>()

    init {
        NeoForge.EVENT_BUS.addListener<ClientPlayerNetworkEvent.LoggingIn> { loginRunnables.forEach { it.invoke() } }
    }

    override fun register(type: RenderType, vararg blocks: Block) = blocks.forEach { block -> ItemBlockRenderTypes.setRenderLayer(block, type) }

    override fun onClientPlayerJoin(listener: () -> Unit) {
        loginRunnables.add(listener)
    }

    override fun fluidSprite(fluid: Fluid): TextureAtlasSprite = fluid.extensions.stillTexture.let { Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(it) }

    override fun fluidTint(fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?): Int {
        val extensions = fluid.extensions

        return if (level != null && pos != null) extensions.getTintColor(fluid.defaultFluidState(), level, pos) else extensions.tintColor
    }
}

private val Fluid.extensions: IClientFluidTypeExtensions get() = IClientFluidTypeExtensions.of(this)