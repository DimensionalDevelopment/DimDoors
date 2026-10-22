package org.dimdev.dimcore.client

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.api.client.ClientPlatform
import org.dimdev.dimcore.api.event.CameraOverlayEvents
import org.dimdev.dimcore.api.event.DebugTextEvents

class FabricClientPlatform() : ClientPlatform {
    override fun register(type: RenderType, vararg blocks: Block) = BlockRenderLayerMap.INSTANCE.putBlocks(type, *blocks)
    override fun onClientPlayerJoin(listener: () -> Unit) = ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ -> listener.invoke() })
    override fun onDebugText(
        type: ClientPlatform.DebugTextType,
        listener: (MutableList<String>) -> Unit
    ) = when(type) {
        ClientPlatform.DebugTextType.GAME -> DebugTextEvents.GAME
        ClientPlatform.DebugTextType.SYSTEM -> DebugTextEvents.SYSTEM
    }.register(listener)

    override fun onFirstPersonOverlay(listener: (GuiGraphics, Player, DeltaTracker) -> Unit) = CameraOverlayEvents.OVERLAYS.register(listener)
}
