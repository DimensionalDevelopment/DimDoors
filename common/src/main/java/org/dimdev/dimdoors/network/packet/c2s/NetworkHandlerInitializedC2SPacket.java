package org.dimdev.dimdoors.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public record NetworkHandlerInitializedC2SPacket() implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkHandlerInitializedC2SPacket> STREAM_CODEC = StreamCodec.unit(new NetworkHandlerInitializedC2SPacket());
    public static final Type<NetworkHandlerInitializedC2SPacket> TYPE = new Type<NetworkHandlerInitializedC2SPacket>(DimensionalDoors.id("network_handler_initialized"));

    @Override
    public @NotNull Type<NetworkHandlerInitializedC2SPacket> type() {
    return TYPE;
    }
}
