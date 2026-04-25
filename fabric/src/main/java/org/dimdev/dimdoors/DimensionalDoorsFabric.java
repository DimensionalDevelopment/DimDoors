package org.dimdev.dimdoors;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class DimensionalDoorsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StreamUtils.setup(this);
        ModAttachmentTypes.register();
        DimensionalDoors.init();
        PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);

        ServerChunkEvents.CHUNK_LOAD.register((serverLevel, levelChunk) -> ChunkServedCallback.EVENT.invoker().onChunkServed(serverLevel, levelChunk));

        initNetworking();
    }

    private void initNetworking() {
        PayloadTypeRegistry.playS2C().register(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(NetworkHandlerInitializedC2SPacket.TYPE, NetworkHandlerInitializedC2SPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC);

        initServerSideHandler();
    }

    private void initServerSideHandler() {
        registerServerListener(NetworkHandlerInitializedC2SPacket.TYPE, (networkHandlerInitializedC2SPacket, context) -> ServerPacketHandler.onNetworkHandlerInitialized(context.player()));
        registerServerListener(HitBlockWithItemC2SPacket.TYPE, (packet, context) -> ServerPacketHandler.onAttackBlock(context.player(), packet));
    }

    private <T extends CustomPacketPayload> void registerServerListener(CustomPacketPayload.Type<T> type, BiFunction<T, ServerPlayNetworking.Context, ? extends @Nullable CustomPacketPayload> packetFunction) {
        ServerPlayNetworking.registerGlobalReceiver(type, new PlayPayloadHandlerReturnable<>(packetFunction));
    }

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(BiFunction<T, ServerPlayNetworking.Context, ? extends @Nullable CustomPacketPayload> packetFunction) implements ServerPlayNetworking.PlayPayloadHandler<T> {
        @Override
        public void receive(T payload, ServerPlayNetworking.Context context) {
            var returnPayload = packetFunction.apply(payload, context);

            if(returnPayload != null) context.responseSender().sendPacket(returnPayload);
        }
    }
}
