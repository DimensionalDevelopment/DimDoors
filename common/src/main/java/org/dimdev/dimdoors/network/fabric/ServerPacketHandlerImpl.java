package org.dimdev.dimdoors.network.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class ServerPacketHandlerImpl {
    public static <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        ServerPlayNetworking.send(player, packet);
    }
}
