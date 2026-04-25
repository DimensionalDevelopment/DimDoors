package org.dimdev.dimdoors.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public enum MonolithTeleportParticlesPacket implements CustomPacketPayload {
    INSTANCE;

    public static final ResourceLocation ID = DimensionalDoors.id("monolith_tp_particles");
    public static final CustomPacketPayload.Type<MonolithTeleportParticlesPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MonolithTeleportParticlesPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<MonolithTeleportParticlesPacket> type() {
    return TYPE;
    }
}
