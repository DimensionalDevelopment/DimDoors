package org.dimdev.dimdoors.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.network.packet.s2c.*;

public class ClientPacketLogic {
    public void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet, NetworkManager.PacketContext context) {}

    public void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet, NetworkManager.PacketContext context) {}

    public void onMonolithAggroParticles(MonolithAggroParticlesPacket packet, NetworkManager.PacketContext context) {}

    public void spawnParticles(int aggro) {}

    public void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet, NetworkManager.PacketContext context) {}

    public void onRenderBreakBlock(RenderBreakBlockS2CPacket packet, NetworkManager.PacketContext context) {}

    public void sendPacket(CustomPacketPayload packet) {}
}

