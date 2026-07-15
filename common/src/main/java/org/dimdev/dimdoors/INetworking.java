package org.dimdev.dimdoors;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface INetworking {
    <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet);

    <T extends CustomPacketPayload> void sendPacket(T packet);

    <T extends CustomPacketPayload> void registerServerPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> function);

    <T extends CustomPacketPayload> void registerClientPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,  Consumer<T> function);
}
