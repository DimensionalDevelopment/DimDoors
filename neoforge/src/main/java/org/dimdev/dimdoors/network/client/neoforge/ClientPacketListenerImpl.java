package org.dimdev.dimdoors.network.client.neoforge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientPacketListenerImpl {
    public static <T extends CustomPacketPayload> void sendPacket(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}