package org.dimdev.dimdoors;

import dev.architectury.registry.CreativeTabOutput;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.callback.AddCallback;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;
import org.dimdev.dimdoors.fluid.neoforge.ModFluidTypes;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge extends SidedImpl {
    private final List<Consumer<BuildCreativeModeTabContentsEvent>> BUILD_CONTENTS_LISTENERS = new ArrayList<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegister = new HashMap<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegisterHolder = new HashMap<>();
    private final Map<ResourceKey<?>, AddCallback<?>> callbacks = new HashMap<>();
    private final IEventBus bus;
    private ResourceKey<? extends Registry<?>> activeKey;
    private final Map<ResourceKey<?>, List<Runnable>> registerRunnables = new HashMap<>();

    public DimensionalDoorsNeoForge(IEventBus bus) {
        StreamUtils.setup(this);
        ModAttachmentTypes.register(bus);
        this.bus = bus;
        registerRunnable(NeoForgeRegistries.Keys.FLUID_TYPES, ModFluidTypes::init);
        DimensionalDoors.init(this);

        bus.addListener(this::buildCreateTabContents);

        bus.<RegisterEvent>addListener(event -> {
            var key = event.getRegistryKey();
            DimensionalDoorsNeoForge.this.activeKey = key;

            try {
                var runnables = registerRunnables.remove(key);
                if (runnables != null) {
                    runnables.forEach(Runnable::run);
                }

                var map = toRegister.get(key);
                var registry = event.getRegistry();

                if (map != null && !map.isEmpty()) {
                    populate(registry, map);
                }

                AddCallback<?> callback = callbacks.get(key);

                if(callback != null) ((Registry) registry).addCallback(callback);
            } finally {
                DimensionalDoorsNeoForge.this.activeKey = null;
            }
        });

        ModBiomeModifiers.init(bus);

        NeoForge.EVENT_BUS.<ChunkEvent.Load>addListener(load -> {
            if (!load.isNewChunk() && load.getLevel() instanceof ServerLevel level && load.getChunk() instanceof LevelChunk chunk)
                ChunkServedCallback.EVENT.invoker().onChunkServed(level, chunk);
        });

        bus.<RegisterPayloadHandlersEvent>addListener(event -> {
            event.registrar("1")
                    .playToClient(PlayerInventorySlotUpdateS2CPacket.TYPE, PlayerInventorySlotUpdateS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onPlayerInventorySlotUpdate(packet))
                    .playToClient(SyncPocketAddonsS2CPacket.TYPE, SyncPocketAddonsS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onSyncPocketAddons(packet))
                    .playToClient(MonolithAggroParticlesPacket.TYPE, MonolithAggroParticlesPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onMonolithAggroParticles(packet))
                    .playToClient(MonolithTeleportParticlesPacket.TYPE, MonolithTeleportParticlesPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onMonolithTeleportParticles(packet))
                    .playToClient(RenderBreakBlockS2CPacket.TYPE, RenderBreakBlockS2CPacket.STREAM_CODEC, (packet, context) -> ClientPacketListener.onRenderBreakBlock(packet))
                    .playToServer(NetworkHandlerInitializedC2SPacket.TYPE, NetworkHandlerInitializedC2SPacket.STREAM_CODEC, new PlayPayloadHandlerReturnable<>((packet, player) -> ServerPacketHandler.onNetworkHandlerInitialized(player)))
                    .playToServer(HitBlockWithItemC2SPacket.TYPE, HitBlockWithItemC2SPacket.STREAM_CODEC, new PlayPayloadHandlerReturnable<>((packet, player) -> ServerPacketHandler.onAttackBlock(player, packet)));
        });
    }

    public <T> void populate(Registry<T> registry, Map<ResourceLocation, Object> map) {
        map.forEach((resourceLocation, obj) -> Registry.register(registry, resourceLocation, (T) obj));
    }

    @Override
    public void modify(CreativeModeTab tab, ModifyTabCallback filler) {
        BUILD_CONTENTS_LISTENERS.add(event -> {
            if (event.getTab().equals(tab)) {
                filler.accept(event.getFlags(), wrapTabOutput(event), event.hasPermissions());
            }
        });
    }

    private void buildCreateTabContents(BuildCreativeModeTabContentsEvent event) {
        if (APPENDS.containsKey(event.getTab())) {
            APPENDS.get(event.getTab()).forEach(event::accept);
        }

        for (Consumer<BuildCreativeModeTabContentsEvent> listener : BUILD_CONTENTS_LISTENERS) {
            listener.accept(event);
        }
    }

    private CreativeTabOutput wrapTabOutput(BuildCreativeModeTabContentsEvent event) {
        return new CreativeTabOutput() {
            @Override
            public void acceptAfter(ItemStack after, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                event.insertAfter(after, stack, visibility);
            }

            @Override
            public void acceptBefore(ItemStack before, ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                event.insertBefore(before, stack, visibility);
            }
        };
    }

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(
            BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> packetFunction) implements IPayloadHandler<T> {


        @Override
        public void handle(T payload, IPayloadContext context) {
            var returnPayload = packetFunction.apply(payload, (ServerPlayer) context.player());

            if(returnPayload != null) context.handle(returnPayload);
        }
    }

    @Override
    public <T, V extends T> V register(ResourceKey<Registry<T>> key, ResourceLocation id, V obj) {
        if (key.equals(activeKey)) {
            return Registry.register((Registry<T>) BuiltInRegistries.REGISTRY.get(key.location()), id, obj);
        } else {
            Map<ResourceLocation, Object> map = this.toRegister.computeIfAbsent(key, a -> new HashMap<>());

            map.putIfAbsent(id, obj);

            return obj;
        }
    }

    @Override
    public <T> void registerCallback(Registry<T> registry, TriConsumer<Registry<T>, ResourceLocation, T> consumer) {
        callbacks.put(registry.key(), new Callback<>(consumer));
    }

    @Override
    public CreativeModeTab createTab(Function<CreativeModeTab.Builder, CreativeModeTab.Builder> consumer) {
        return consumer.apply(CreativeModeTab.builder()).build();
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStartedEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    class Callback<T> implements AddCallback<T> {
        private final TriConsumer<Registry<T>, ResourceLocation, T> consumer;

        Callback(TriConsumer<Registry<T>, ResourceLocation, T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onAdd(Registry<T> registry, int id, ResourceKey<T> key, T obj) {
            ResourceKey<? extends Registry<?>> previousKey = activeKey;
            activeKey = registry.key();
            try {
                consumer.accept(registry, key.location(), obj);
            } finally {
                activeKey = previousKey;
            }
        }
    }

    @Override
    public void registerRunnable(ResourceKey<? extends Registry<?>> key, Runnable runnable) {
        registerRunnables.computeIfAbsent(key, ignored -> new ArrayList<>()).add(runnable);
    }

    @Override
    public Fluid createFlowingEternalFluid() {
        return new EternalFluid.Flowing() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.ETERNAL;
            }
        };
    }

    @Override
    public FlowingFluid createEternalFluid() {
        return new EternalFluid.Still() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.ETERNAL;
            }
        };
    }

    @Override
    public Fluid createFlowingLeakFluid() {
        return new LeakFluid.Flowing() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.LEAK;
            }
        };
    }

    @Override
    public FlowingFluid createLeakFluid() {
        return new LeakFluid.Still() {
            @Override
            public FluidType getFluidType() {
                return ModFluidTypes.LEAK;
            }
        };
    }
}
