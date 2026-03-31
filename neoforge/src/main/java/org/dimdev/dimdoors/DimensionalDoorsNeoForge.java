package org.dimdev.dimdoors;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.pockets.dimension.UpdateDimensionsPacket;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge {
    public DimensionalDoorsNeoForge(IEventBus bus) {
        StreamUtils.setup(this);
        ModAttachmentTypes.register(bus);
        DimensionalDoors.init();

        ModBiomeModifiers.init(bus);

        NeoForge.EVENT_BUS.<ChunkEvent.Load>addListener(load -> {
            if(!load.isNewChunk() && load.getLevel() instanceof ServerLevel level && load.getChunk() instanceof LevelChunk chunk)
                ChunkServedCallback.EVENT.invoker().onChunkServed(level, chunk);
        });

        bus.<RegisterPayloadHandlersEvent>addListener(event -> {
            event.registrar("1")
                    .playToClient(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onPlayerInventorySlotUpdate(packet))
                    .playToClient(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onSyncPocketAddons(packet))
                    .playToClient(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onMonolithAggroParticles(packet))
                    .playToClient(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onMonolithTeleportParticles(packet))
                    .playToClient(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onRenderBreakBlock(packet))
                    .playToClient(UpdateDimensionsPacket.TYPE, UpdateDimensionsPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onUpdateDimensions(packet))
                    .playToServer(NetworkHandlerInitializedC2SPacket.TYPE, NetworkHandlerInitializedC2SPacket.STREAM_CODEC, new PlayPayloadHandlerReturnable<>((packet, player) -> ServerPacketHandler.onNetworkHandlerInitialized(player)))
                    .playToServer(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC, new PlayPayloadHandlerReturnable<>((packet, player) -> ServerPacketHandler.onAttackBlock(player, packet)));
        });
    }

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(
            BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> packetFunction) implements IPayloadHandler<T> {


        @Override
        public void handle(T payload, IPayloadContext context) {
            var returnPayload = packetFunction.apply(payload, (ServerPlayer) context.player());

            if(returnPayload != null) context.handle(returnPayload);
        }
    }
}
