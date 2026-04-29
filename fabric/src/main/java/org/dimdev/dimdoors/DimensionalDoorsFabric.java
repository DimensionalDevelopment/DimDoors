package org.dimdev.dimdoors;

import dev.architectury.registry.registries.DeferredSupplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DimensionalDoorsFabric extends SidedImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        StreamUtils.setup(this);
        ModAttachmentTypes.register();
        DimensionalDoors.init(this);
        PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            if(APPENDS.containsKey(group)) {
                DimensionalDoorsFabric.this.APPENDS.get(group).forEach(entries::accept);
            }
        });

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

    @Override
    @SuppressWarnings("unchecked")
    public <T, V extends T> V register(ResourceKey<Registry<T>> key, ResourceLocation id, V obj) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) {
            throw new IllegalArgumentException("Unknown registry: " + key.location());
        }
        Registry.register(registry, id, obj);
        return obj;
    }

    @Override
    public <T> void registerCallback(Registry<T> registry, TriConsumer<Registry<T>, ResourceLocation, T> consumer) {
        RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> consumer.accept(registry, id, object));
    }

    @Override
    public CreativeModeTab createTab(Function<CreativeModeTab.Builder, CreativeModeTab.Builder> consumer) {
        return consumer.apply(FabricItemGroup.builder()).build();
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> consumer.accept(server));
    }

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(BiFunction<T, ServerPlayNetworking.Context, ? extends @Nullable CustomPacketPayload> packetFunction) implements ServerPlayNetworking.PlayPayloadHandler<T> {
        @Override
        public void receive(T payload, ServerPlayNetworking.Context context) {
            var returnPayload = packetFunction.apply(payload, context);

            if(returnPayload != null) context.responseSender().sendPacket(returnPayload);
        }
    }

    @Override
    public void modify(CreativeModeTab tab, ModifyTabCallback filler) {
        ItemGroupEvents.modifyEntriesEvent(BuiltInRegistries.CREATIVE_MODE_TAB.wrapAsHolder(tab).unwrapKey().get()).register(new ItemGroupEvents.ModifyEntries() {
            @Override
            public void modifyEntries(FabricItemGroupEntries entries) {
                filler.accept(entries.getEnabledFeatures(), new CreativeTabOutput() {
                    @Override
                    public void acceptAfter(ItemStack after, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        if (after.isEmpty()) {
                            entries.accept(stack, visibility);
                        } else {
                            entries.addAfter(after, List.of(stack), visibility);
                        }
                    }

                    @Override
                    public void acceptBefore(ItemStack before, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                        if (before.isEmpty()) {
                            entries.accept(stack, visibility);
                        } else {
                            entries.addBefore(before, List.of(stack), visibility);
                        }
                    }
                }, entries.shouldShowOpRestrictedItems());
            }
        });
    }

    @Override
    public void registerRunnable(ResourceKey<? extends Registry<?>> key, Runnable runnable) {
        runnable.run();
    }
}
