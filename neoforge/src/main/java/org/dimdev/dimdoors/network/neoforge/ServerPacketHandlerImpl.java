package org.dimdev.dimdoors.network.neoforge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerPacketHandlerImpl {
    public static <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}