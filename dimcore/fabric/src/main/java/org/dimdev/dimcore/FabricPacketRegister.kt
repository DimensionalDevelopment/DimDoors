package org.dimdev.dimcore

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.PacketRegister

object FabricPacketRegister : PacketRegister {
    override fun <T : CustomPacketPayload> registerServerPacket(
        type: CustomPacketPayload.Type<T>,
        streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>,
        function: (T, ServerPlayer) -> CustomPacketPayload?
    ) {
        PayloadTypeRegistry.playC2S().register<T?>(type, streamCodec)
        ServerPlayNetworking.registerGlobalReceiver<T?>(type, FabricSided.PlayPayloadHandlerReturnable<T>(function))
    }

    override fun <T : CustomPacketPayload> registerClientPacket(
        type: CustomPacketPayload.Type<T>,
        streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>,
        consumer: (T) -> Unit
    ) {
        PayloadTypeRegistry.playS2C().register<T>(type, streamCodec)
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) ClientPlayNetworking.registerGlobalReceiver<T>(type) { payload: T, _ -> consumer.invoke(payload) }
    }
}