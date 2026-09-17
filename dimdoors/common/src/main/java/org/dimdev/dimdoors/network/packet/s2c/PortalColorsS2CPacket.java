package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.util.StreamCodecUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record PortalColorsS2CPacket(int[] colors) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("portal_colors");
    public static final StreamCodec<RegistryFriendlyByteBuf, PortalColorsS2CPacket> STREAM_CODEC = StreamCodecUtils.intArray(16).map(PortalColorsS2CPacket::new, PortalColorsS2CPacket::colors).cast();
    public static final Type<PortalColorsS2CPacket> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<PortalColorsS2CPacket> type() {
        return TYPE;
    }


}
