package org.dimdev.dimdoors;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
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
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.fabric.mixin.RecipeBookSettingsAccessor;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.fray.DataValue;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

import static org.dimdev.dimdoors.DimensionalDoors.MOD_ID;
import static org.dimdev.dimdoors.DimensionalDoors.id;

public class DimensionalDoorsFabric extends SidedImpl implements ModInitializer {
    private MinecraftServer server;
    private final List<BiConsumer<CreativeModeTab, FabricItemGroupEntries>> creativeTabListeners = new ArrayList<>();

    private final Supplier<RecipeBookType> TESSELLATING = Suppliers.memoize(() -> {
        var type = ClassTinkerers.getEnum(RecipeBookType.class, "TESSELLATING");
        ImmutableCollectionUtils.getAsMutableMap(RecipeBookSettingsAccessor::getTagFields, RecipeBookSettingsAccessor::setTagFields)
                .putIfAbsent(type, Pair.of("isTessellatingGui", "isTessellatingFilteringCraftable"));
            return type;

    });

    @Override
    public void onInitialize() {
        ModAttachmentTypes.register();
        ServerLifecycleEvents.SERVER_STARTING.register(this::setServer);
        DimensionalDoors.init(this);
        PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            if(APPENDS.containsKey(group)) {
                DimensionalDoorsFabric.this.APPENDS.get(group).forEach(entries::accept);
            }
            creativeTabListeners.forEach(listener -> listener.accept(group, entries));
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

    private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(BiFunction<T, ServerPlayNetworking.Context, ? extends @Nullable CustomPacketPayload> packetFunction) implements ServerPlayNetworking.PlayPayloadHandler<T> {
        @Override
        public void receive(T payload, ServerPlayNetworking.Context context) {
            var returnPayload = packetFunction.apply(payload, context);

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
        return (DataValue<T>) AttachmentRegistry.<T>create(DimensionalDoors.id(name), new Consumer<AttachmentRegistry.Builder<T>>() {
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

    public void initBuiltinPacks() {
        ResourceManagerHelper.registerBuiltinResourcePack(id("default"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.DEFAULT_ENABLED);
        ResourceManagerHelper.registerBuiltinResourcePack(id("classic"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.DEFAULT_ENABLED);
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
    public RecipeBookType getTesselatingRecipeBookType() {
        return TESSELLATING.get();
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
}
