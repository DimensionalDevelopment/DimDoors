package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public record MonolithAggroParticlesPacket(int aggro) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("monolith_aggro_particles");
    public static final Type<MonolithAggroParticlesPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MonolithAggroParticlesPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, MonolithAggroParticlesPacket::aggro, MonolithAggroParticlesPacket::new);

    @Override
    public @NotNull Type<MonolithAggroParticlesPacket> type() {
        return TYPE;
    }
}
