package org.dimdev.dimdoors;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface INetworking {
    <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet);

    <T extends CustomPacketPayload> void sendPacket(T packet);
}
