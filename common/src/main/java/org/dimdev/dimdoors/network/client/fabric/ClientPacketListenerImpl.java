package org.dimdev.dimdoors.network.client.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientPacketListenerImpl {
    public static <T extends CustomPacketPayload> void sendPacket(T packet) {
        ClientPlayNetworking.send(packet);
    }
}
