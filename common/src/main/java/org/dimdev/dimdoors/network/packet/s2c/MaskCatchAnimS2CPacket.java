package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record MaskCatchAnimS2CPacket(MaskType maskType) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("mask_catch_anim");
    public static final CustomPacketPayload.Type<MaskCatchAnimS2CPacket> TYPE = new CustomPacketPayload.Type<MaskCatchAnimS2CPacket>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, MaskCatchAnimS2CPacket> STREAM_CODEC = MaskType.STREAM_CODEC.map(MaskCatchAnimS2CPacket::new, MaskCatchAnimS2CPacket::maskType);

    @Override
    public @NotNull CustomPacketPayload.Type<MaskCatchAnimS2CPacket> type() {
        return TYPE;
    }
}
