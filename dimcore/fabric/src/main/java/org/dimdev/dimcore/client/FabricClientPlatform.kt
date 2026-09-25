package org.dimdev.dimcore.client

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.api.client.ClientPlatform

class FabricClientPlatform() : ClientPlatform {
    override fun register(type: RenderType, vararg blocks: Block) = BlockRenderLayerMap.INSTANCE.putBlocks(type, *blocks)
    override fun onClientPlayerJoin(listener: () -> Unit) = ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ -> listener.invoke() })
}
