package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

/** Sent when the player leaves pocket space entirely, so the client can drop pocket state instead of keeping the last one. */
public record ClearPocketS2CPacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("clear_pocket");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearPocketS2CPacket> STREAM_CODEC = StreamCodec.unit(new ClearPocketS2CPacket());
    public static final Type<ClearPocketS2CPacket> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<ClearPocketS2CPacket> type() {
        return TYPE;
    }
}
