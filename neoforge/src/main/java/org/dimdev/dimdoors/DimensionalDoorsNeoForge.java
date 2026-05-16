package org.dimdev.dimdoors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.InstanceMap;
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

import java.nio.file.Path;
import java.util.*;
import java.util.function.*;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge extends SidedImpl {
    public static final EnumProxy<RecipeBookType> TESSELLATING = new EnumProxy<>(RecipeBookType.class);

    private final List<Consumer<BuildCreativeModeTabContentsEvent>> BUILD_CONTENTS_LISTENERS = new ArrayList<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegister = new HashMap<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegisterHolder = new HashMap<>();
    private final Map<ResourceKey<?>, AddCallback<?>> callbacks = new HashMap<>();
    private final IEventBus bus;
    private ResourceKey<? extends Registry<?>> activeKey;
    private final Map<ResourceKey<?>, List<Runnable>> registerRunnables = new HashMap<>();
    private List<Registry<?>> registriesToRegister = new ArrayList<>();
    private final List<EntityAttributeRegistration> entityAttributeRegistrations = new ArrayList<>();
    private final List<DataPackRegistryRegistration<?>> dataPackRegistries = new ArrayList<>();

    public DimensionalDoorsNeoForge(IEventBus bus) {
        ModAttachmentTypes.register(bus);
        this.bus = bus;

        registerRunnable(NeoForgeRegistries.Keys.FLUID_TYPES, ModFluidTypes::init);
        DimensionalDoors.init(this);

        bus.addListener(this::buildCreateTabContents);
        bus.addListener(this::onEntityAttributeRegister);

        bus.addListener(this::onDataPackRegister);

        bus.<NewRegistryEvent>addListener(event -> registriesToRegister.forEach(event::register));

        bus.<RegisterEvent>addListener(event -> {
            var key = event.getRegistryKey();
            DimensionalDoorsNeoForge.this.activeKey = key;

            try {
                var runnables = registerRunnables.remove(key);
                if (runnables != null) {
                    runnables.forEach(Runnable::run);
                }

                var registry = event.getRegistry();

                AddCallback<?> callback = callbacks.get(key);

                if(callback != null) ((Registry) registry).addCallback(callback);

                var map = toRegister.get(key);

                if (map != null && !map.isEmpty()) {
                    populate(registry, map);
                }
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

        NeoForge.EVENT_BUS.addListener(DimensionalDoorsNeoForge::addReloaders);
        bus.addListener(DimensionalDoorsNeoForge::addPackFinders);
    }

    private void onDataPackRegister(DataPackRegistryEvent.NewRegistry event) {
        dataPackRegistries.forEach(registration -> registration.register(event));
    }

    private record DataPackRegistryRegistration<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {
        private void register(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(key, codec);
        }
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
    public void onServerStarting(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStartingEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStartedEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    @Override
    public void onPlayerQuit(Consumer<ServerPlayer> consumer) {
        NeoForge.EVENT_BUS.<PlayerEvent.PlayerLoggedOutEvent>addListener(event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                consumer.accept(player);
            }
        });
    }

    @Override
    public void onServerLevelTick(Consumer<ServerLevel> consumer) {
        NeoForge.EVENT_BUS.<LevelTickEvent.Pre>addListener(event -> {
            if (event.getLevel() instanceof ServerLevel level) {
                consumer.accept(level);
            }
        });
    }

    @Override
    public void onAttackBlock(AttackBlockCallback callback) {
        NeoForge.EVENT_BUS.<PlayerInteractEvent.LeftClickBlock>addListener(event -> {
            if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START || event.getFace() == null) {
                return;
            }

            InteractionResult result = callback.attack(event.getEntity(), event.getHand(), event.getPos(), event.getFace());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        });
    }

    @Override
    public void onUseItem(ISided.UseItemCallback callback) {
        NeoForge.EVENT_BUS.<PlayerInteractEvent.RightClickItem>addListener(event -> {
            InteractionResult result = callback.use(event.getEntity(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    @Override
    public void onUseBlock(ISided.UseBlockCallback callback) {
        NeoForge.EVENT_BUS.<PlayerInteractEvent.RightClickBlock>addListener(event -> {
            InteractionResult result = callback.use(event.getEntity(), event.getHand(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    @Override
    public void onBeforeBlockBreak(BlockBreakCallback callback) {
        NeoForge.EVENT_BUS.<BlockEvent.BreakEvent>addListener(event -> {
            if (event.getLevel() instanceof Level level && callback.shouldCancel(level, event.getPos(), event.getState(), event.getPlayer())) {
                event.setCanceled(true);
            }
        });
    }

    @Override
    public void onBeforeBlockPlace(BlockPlaceCallback callback) {
        NeoForge.EVENT_BUS.<BlockEvent.EntityPlaceEvent>addListener(event -> {
            if (event.getLevel() instanceof Level level && callback.shouldCancel(level, event.getPos(), event.getPlacedBlock(), event.getEntity())) {
                event.setCanceled(true);
            }
        });
    }

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> type, Supplier<AttributeSupplier.Builder> attributes) {
        entityAttributeRegistrations.add(new EntityAttributeRegistration(type, attributes));
    }

    private void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
        entityAttributeRegistrations.forEach(registration -> event.put(registration.type(), registration.attributes().get().build()));
    }

    private record EntityAttributeRegistration(EntityType<? extends LivingEntity> type, Supplier<AttributeSupplier.Builder> attributes) { }

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
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {


        var registry = new RegistryBuilder<>(key).create();
        registriesToRegister.add(registry);

        return registry;
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

    @Override
    public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    private static List<Triple<ResourceLocation, BiConsumer<HolderLookup.Provider, ResourceManager>, Boolean>> loaders = new ArrayList<>();

    public Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }

    public void initBuiltinPacks() {
        NeoForge.EVENT_BUS.addListener(DimensionalDoorsNeoForge::addReloaders);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionalDoorsImpl::addPackFinders);
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            var classicPack = createPack("classic", "Classic");
            var defaultPack = createPack("default", "Default");
            event.addRepositorySource((packConsumer) -> {
                packConsumer.accept(classicPack);
                packConsumer.accept(defaultPack);
            });
        }
    }

    public static void addReloaders(AddReloadListenerEvent event) {
        loaders.forEach(pair -> event.addListener(new NeoforgeResourceLoader(pair.getMiddle())));
    }

    public static Pack createPack(String id, String name) {

        var resourcePath = ModList.get().getModFileById(DimensionalDoors.MOD_ID).getFile().findResource("resourcepacks", id);
        return Pack.readMetaAndCreate(new PackLocationInfo(id, Component.literal(name), PackSource.BUILT_IN, Optional.empty()),
                new PathPackResources.PathResourcesSupplier(resourcePath), PackType.SERVER_DATA, new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
    }

    public void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer, boolean loadAfterTags) {
        loaders.add(Triple.of(DimensionalDoors.id(name), consumer, loadAfterTags));
    }

    private static class NeoforgeResourceLoader extends ContextAwareReloadListener implements ResourceManagerReloadListener {
        private final BiConsumer<HolderLookup.Provider, ResourceManager> consumer;

        public NeoforgeResourceLoader(BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            consumer.accept(this.getRegistryLookup(), resourceManager);
        }
    }

    @Override
    public RecipeBookType getTesselatingRecipeBookType() {
        return TESSELLATING.getValue();
    }

    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public long bucketAmount() {
        return 1000;
    }

    @Override
    public void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        NeoForge.EVENT_BUS.<RegisterCommandsEvent>addListener(event -> consumer.accept(event.getDispatcher()));
    }

    @Override
    public <T> void createDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        dataPackRegistries.add(new DataPackRegistryRegistration<>(key, codec));
    }
}
