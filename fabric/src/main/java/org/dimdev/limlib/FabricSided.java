package org.dimdev.limlib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
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
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.limlib.impl.SidedImpl;
import org.dimdev.limlib.util.DataValue;
import org.dimdev.limlib.api.ModCommon;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

public abstract class FabricSided<V extends FabricSided<V, S>, S extends ModCommon<? super V>> extends SidedImpl<V, S> implements ModInitializer {
    private MinecraftServer server;
    private final List<BiConsumer<CreativeModeTab, FabricItemGroupEntries>> creativeTabListeners = new ArrayList<>();

    public FabricSided(S common) {
        super(common);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::setServer);
        common.init(self());

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            if(APPENDS.containsKey(group)) {
                FabricSided.this.APPENDS.get(group).forEach(entries::accept);
            }
            creativeTabListeners.forEach(listener -> listener.accept(group, entries));
        });

    }

    public <T extends CustomPacketPayload> void registerServerPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,  BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> function) {
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, new PlayPayloadHandlerReturnable<>(function));
    }

    public <T extends CustomPacketPayload> void registerClientPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,  Consumer<T> function) {
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> function.accept(payload));
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
    public void onServerStarting(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> consumer.accept(server));
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> consumer.accept(server));
    }

    @Override
    public void onPlayerQuit(Consumer<ServerPlayer> consumer) {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> consumer.accept(handler.player));
    }

    @Override
    public void onServerLevelTick(Consumer<ServerLevel> consumer) {
        ServerTickEvents.START_WORLD_TICK.register(world -> consumer.accept(world));
    }

    @Override
    public void onAttackBlock(AttackBlockCallback callback) {
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> callback.attack(player, hand, pos, direction));
    }

    @Override
    public void onUseItem(UseItemCallback callback) {
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, world, hand) -> {
            InteractionResult result = callback.use(player, hand);
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        });
    }

    @Override
    public void onUseBlock(UseBlockCallback callback) {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> callback.use(player, hand, hitResult));
    }

    @Override
    public void onBeforeBlockBreak(BlockBreakCallback callback) {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> !callback.shouldCancel(level, pos, state, player));
    }

    @Override
    public void onBeforeBlockPlace(BlockPlaceCallback callback) {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!(player.getItemInHand(hand).getItem() instanceof BlockItem)) {
                return InteractionResult.PASS;
            }
            var pos = hitResult.getBlockPos();
            return callback.shouldCancel(level, pos, level.getBlockState(pos), player) ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> type, Supplier<AttributeSupplier.Builder> attributes) {
        FabricDefaultAttributeRegistry.register(type, attributes.get());
    }

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(BiFunction<T, ServerPlayer, ? extends @Nullable CustomPacketPayload> packetFunction) implements ServerPlayNetworking.PlayPayloadHandler<T> {
        @Override
        public void receive(T payload, ServerPlayNetworking.Context context) {
            var returnPayload = packetFunction.apply(payload, context.player());

            if(returnPayload != null) context.responseSender().sendPacket(returnPayload);
        }
    }

    @Override
    public void modify(CreativeModeTab tab, ModifyTabCallback filler) {
        creativeTabListeners.add((group, entries) -> {
            if (!group.equals(tab)) {
                return;
            }

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
        });
    }

    @Override
    public void registerRunnable(ResourceKey<? extends Registry<?>> key, Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> DataValue<T> registerDataValue(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return (DataValue<T>) AttachmentRegistry.create(DimensionalDoors.id(name), new Consumer<AttachmentRegistry.Builder<T>>() {
            @Override
            public void accept(AttachmentRegistry.Builder<T> builder) {
                builder.initializer(defaultValue);
                builder.persistent(codec);
                if(streamCodec != null) builder.syncWith(streamCodec, AttachmentSyncPredicate.all());
            }
        });
    }

    @Override
    public void registerRunDataValue(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        ClientPlayNetworking.send(packet);
    }

    public Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer, boolean loadAfterTags) {
        var id = DimensionalDoors.id(name);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(id, provider -> new FabricResourceLoader(id, manager -> consumer.accept(provider, manager), loadAfterTags ? List.of(ResourceReloadListenerKeys.TAGS) : List.of()));
    }

    private record FabricResourceLoader(ResourceLocation id, Consumer<ResourceManager> consumer, List<ResourceLocation> dependecies) implements IdentifiableResourceReloadListener, ResourceManagerReloadListener {

        @Override
        public ResourceLocation getFabricId() {
            return id;
        }

        @Override
        public Collection<ResourceLocation> getFabricDependencies() {
            return dependecies;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            consumer.accept(resourceManager);
        }
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }

    @Override
    public MinecraftServer getServer() {
        return Objects.requireNonNull(server, "Minecraft server is not available");
    }

    protected void setServer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public long bucketAmount() {
        return 81000;
    }

    @Override
    public void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher));
    }

    @Override
    public <T> void createDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        DynamicRegistries.register(key, codec);
    }

    @Override
    public void addPack(PackType type, String id, String name, boolean defaultedOn) {
        ResourceManagerHelper.registerBuiltinResourcePack(ResourceLocation.fromNamespaceAndPath(getModId(), id), FabricLoader.getInstance().getModContainer(getModId()).get(), Component.literal(name), defaultedOn ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL);
    }
}
