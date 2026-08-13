package org.dimdev.dimcore;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.dimcore.api.ISided;
import org.dimdev.dimcore.api.ModCommon;
import org.dimdev.dimcore.api.SidedImpl;
import org.dimdev.dimcore.util.DataValue;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.function.*;

public abstract class NeoForgeSided<V extends NeoForgeSided<V, T>, T extends ModCommon<? super V>> extends SidedImpl<V, T> {
    private final List<Consumer<BuildCreativeModeTabContentsEvent>> BUILD_CONTENTS_LISTENERS = new ArrayList<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegister = new HashMap<>();
    private final Map<ResourceKey<?>, Map<ResourceLocation, HolderRegistration<?>>> toRegisterHolder = new HashMap<>();
    private final Map<ResourceKey<?>, AddCallback<?>> callbacks = new HashMap<>();
    private final IEventBus bus;
    private ResourceKey<? extends Registry<?>> activeKey;
    private final Map<ResourceKey<?>, List<Runnable>> registerRunnables = new HashMap<>();
	private final List<Registry<?>> registriesToRegister = new ArrayList<>();
	private final List<EntityAttributeRegistration> entityAttributeRegistrations = new ArrayList<>();
	private final List<DataPackRegistryRegistration<?>> dataPackRegistries = new ArrayList<>();
	private final Object2IntMap<ItemLike> fuels = new Object2IntLinkedOpenHashMap<>();
	private final Map<Block, Block> strippables = new HashMap<>();
	private List<CreativeTabModifier> creativeTabModifiers;

	public NeoForgeSided(IEventBus bus, T common) {
        super(common);
        this.bus = bus;

		bus.addListener(this::modifyCreativeTabContents);

		bus.addListener(this::buildCreateTabContents);
        bus.addListener(this::onEntityAttributeRegister);

        bus.addListener(this::onDataPackRegister);

        bus.<NewRegistryEvent>addListener(event -> registriesToRegister.forEach(event::register));

        bus.<RegisterEvent>addListener(EventPriority.LOWEST, event -> {
            var key = event.getRegistryKey();
            NeoForgeSided.this.activeKey = key;

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

                var holderMap = toRegisterHolder.remove(key);
                if (holderMap != null && !holderMap.isEmpty()) {
                    populateHolders(registry, holderMap);
                }
            } finally {
                NeoForgeSided.this.activeKey = null;
            }
        });

        bus.<RegisterPayloadHandlersEvent>addListener(this::registerPackets);

        NeoForge.EVENT_BUS.addListener(this::addReloaders);
		NeoForge.EVENT_BUS.addListener(this::getFuelBurnTime);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::modifyBlockWithTool);
        bus.addListener(this::addPackFinders);

        common.init(self());
    }

	private void modifyBlockWithTool(BlockEvent.BlockToolModificationEvent event) {
		if (event.getItemAbility() != ItemAbilities.AXE_STRIP || event.getFinalState() != event.getState()) {
			return;
		}

		Block target = strippables.get(event.getState().getBlock());
		if (target != null) {
			event.setFinalState(target.withPropertiesOf(event.getState()));
		}
	}

	private void getFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		Integer burnTime = fuels.get(event.getItemStack().getItem());
		if (burnTime != null) {
			event.setBurnTime(burnTime);
		}
	}

    private void onDataPackRegister(DataPackRegistryEvent.NewRegistry event) {
        dataPackRegistries.forEach(registration -> registration.register(event));
    }

    private record DataPackRegistryRegistration<T>(ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
        private void register(DataPackRegistryEvent.NewRegistry event) {
			if(networkCodec != null) {
				event.dataPackRegistry(key, codec, networkCodec);
			} else {
				event.dataPackRegistry(key, codec);
			}
        }
    }

    public <T> void populate(Registry<T> registry, Map<ResourceLocation, Object> map) {
        map.forEach((resourceLocation, obj) -> Registry.register(registry, resourceLocation, (T) obj));
    }

    public <T> void populateHolders(Registry<T> registry, Map<ResourceLocation, HolderRegistration<?>> map) {
        map.forEach((resourceLocation, registration) -> ((HolderRegistration<T>) registration).register(registry, resourceLocation));
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
	public <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, ResourceLocation id, V obj) {
		if (key.equals(activeKey)) {
			return Registry.registerForHolder((Registry<T>) BuiltInRegistries.REGISTRY.get(key.location()), id, obj);
		} else {
			Map<ResourceLocation, HolderRegistration<?>> map = this.toRegisterHolder.computeIfAbsent(key, a -> new HashMap<>());
			HolderRegistration<T> registration = (HolderRegistration<T>) map.computeIfAbsent(id, ignored -> new HolderRegistration<>(obj, BindableDeferredHolder.createBindable(key, id)));

			return registration.holder();
		}
	}

	private record HolderRegistration<T>(T obj, BindableDeferredHolder<T, ? extends T> holder) {
		private void register(Registry<T> registry, ResourceLocation id) {
			Registry.registerForHolder(registry, id, obj);
			holder.bind();
		}
	}

	private static class BindableDeferredHolder<R, T extends R> extends DeferredHolder<R, T> {
		private BindableDeferredHolder(ResourceKey<R> key) {
			super(key);
		}

		private static <R, T extends R> BindableDeferredHolder<R, T> createBindable(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation id) {
			return new BindableDeferredHolder<>(ResourceKey.create(registryKey, id));
		}

		private void bind() {
			bind(false);
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
    public <T> DataValue<T> registerDataValue(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        var dataValue = AttachmentType.builder(defaultValue).serialize(codec);
        if(streamCodec != null) {
            dataValue.sync(streamCodec);
        }

        return (DataValue<T>) (Object) register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, name, dataValue.build());
    }

    @Override
    public void registerRunDataValue(Runnable runnable) {
        registerRunnable(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, runnable);
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultId, boolean sync) {


        var registry =new RegistryBuilder<>(key).sync(sync).defaultKey(defaultId).create();

        registriesToRegister.add(registry);

        return registry;
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    private record ClientPacket<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Consumer<T> function) {
        public void register(PayloadRegistrar registrar) {
            registrar.playToClient(type, streamCodec, (packet, ctx) -> function.accept(packet));
        }
    }
    private record ServerPacket<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> function) {
        public void register(PayloadRegistrar registrar) {
            registrar.playToServer(type, streamCodec, new PlayPayloadHandlerReturnable<>(function));
        }
    }

    private final List<ClientPacket<?>> clientPackets = new ArrayList<>();
    private final List<ServerPacket<?>> serverPackets = new ArrayList<>();


    @Override
    public <T extends CustomPacketPayload> void registerClientPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Consumer<T> function) {
        clientPackets.add(new ClientPacket<>(type, streamCodec, function));
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> function) {
        serverPackets.add(new ServerPacket<>(type, streamCodec, function));
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        clientPackets.forEach(packet -> packet.register(registrar));
        serverPackets.forEach(packet -> packet.register(registrar));
    }

    private final List<Triple<ResourceLocation, BiConsumer<HolderLookup.Provider, ResourceManager>, Boolean>> loaders = new ArrayList<>();

    public Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }


    private final Map<PackType, List<PackInfo>> packs = new HashMap<>();

    private record PackInfo(String id, String name, boolean defaultedOn) {
        public Pack create(String modId, PackType type) {
            var resourcePath = ModList.get().getModFileById(modId).getFile().findResource("resourcepacks", id);
            return Pack.readMetaAndCreate(new PackLocationInfo(id, Component.literal(name), PackSource.BUILT_IN, Optional.empty()),
                    new PathPackResources.PathResourcesSupplier(resourcePath), type, new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
        }
    }

    public void addPack(PackType type, String id, String name, boolean defaultedOn) {
        packs.computeIfAbsent(type, a -> new ArrayList<>()).add(new PackInfo(id, name, defaultedOn));
    }

    public void addPackFinders(AddPackFindersEvent event) {
        var type = event.getPackType();

        var modId = common.getModId();

        event.addRepositorySource(source -> {
            packs.getOrDefault(type, Collections.emptyList()).stream().map(a -> a.create(modId, type)).forEach(source);
        });
    }

    public void addReloaders(AddReloadListenerEvent event) {
        loaders.forEach(pair -> event.addListener(new NeoforgeResourceLoader.Server(pair.getLeft(), pair.getMiddle())));
    }

	public void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer, boolean loadAfterTags) {
		loaders.add(Triple.of(ResourceLocation.fromNamespaceAndPath(common.getModId(), name), consumer, loadAfterTags));
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
    public <T> void createDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
        dataPackRegistries.add(new DataPackRegistryRegistration<>(key, codec, networkCodec));
    }

	@Override
	public void registerStrippable(Block source, Block target) {
		strippables.put(source, target);
	}

	@Override
	public void registerFuel(ItemLike item, int amount) {
		fuels.put(item, amount);
	}

	@Override
	public void registryFlammable(Block block, int encouragement, int flammability) {
		((FireBlock) Blocks.FIRE).setFlammable(block, encouragement, flammability);
	}

	@Override
	public void modifyCreativeTab(ResourceKey<CreativeModeTab> tab, Consumer<CreativeTabEntries> consumer) {
		creativeTabModifiers().add(new CreativeTabModifier(tab, consumer));
	}

	private List<CreativeTabModifier> creativeTabModifiers() {
		if (creativeTabModifiers == null) {
			creativeTabModifiers = new ArrayList<>();
		}

		return creativeTabModifiers;
	}

	private void modifyCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		for (CreativeTabModifier modifier : creativeTabModifiers()) {
			if (event.getTabKey().equals(modifier.tab())) {
				modifier.consumer().accept(new NeoForgeCreativeTabEntries(event));
			}
		}
	}

	private record CreativeTabModifier(ResourceKey<CreativeModeTab> tab, Consumer<CreativeTabEntries> consumer) {
	}

	private record NeoForgeCreativeTabEntries(BuildCreativeModeTabContentsEvent event) implements CreativeTabEntries {
		@Override
		public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
			event.accept(stack, visibility);
		}

		@Override
		public void addAfter(ItemStack after, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
			if (after.isEmpty()) {
				acceptAll(stacks, visibility);
				return;
			}

			ItemStack previous = after;

			for (ItemStack stack : stacks) {
				event.insertAfter(previous, stack, visibility);
				previous = stack;
			}
		}

		@Override
		public void addBefore(ItemStack before, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
			if (before.isEmpty()) {
				acceptAll(stacks, visibility);
				return;
			}

			for (ItemStack stack : stacks) {
				event.insertBefore(before, stack, visibility);
			}
		}
	}
}
