package org.dimdev.dimcore;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimcore.api.Platform;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class FabricPlatform implements Platform {
    private MinecraftServer server;

    public FabricPlatform() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
    }

    @Override
    public MinecraftServer getServer() {
        return Objects.requireNonNull(server, "Minecraft server is not available");
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
    public Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void onServerStarting(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STARTING.register(consumer::accept);
    }

    @Override
    public void onServerStarted(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STARTED.register(consumer::accept);
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STOPPING.register(consumer::accept);
    }

    @Override
    public void onServerStopped(Consumer<MinecraftServer> consumer) {
        ServerLifecycleEvents.SERVER_STOPPED.register(consumer::accept);
    }

    @Override
    public void onPlayerJoin(Consumer<ServerPlayer> consumer) {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> consumer.accept(handler.player));
    }

    @Override
    public void onPlayerQuit(Consumer<ServerPlayer> consumer) {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> consumer.accept(handler.player));
    }

    @Override
    public void onServerLevelTick(Consumer<ServerLevel> consumer) {
        ServerTickEvents.START_WORLD_TICK.register(consumer::accept);
    }

    @Override
    public void onPlayerChangeWorld(TriConsumer<ServerPlayer, ServerLevel, ServerLevel> consumer) {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(consumer::accept);
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
    public void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher));
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(player, type);
    }

    @Override
    public void registerFuel(ItemLike item, int amount) {
        FuelRegistry.INSTANCE.add(item, amount);
    }

    @Override
    public void registerStrippable(Block source, Block target) {
        StrippableBlockRegistry.register(source, target);
    }

    @Override
    public void registerFlammable(Block block, int encouragement, int flammability) {
        FlammableBlockRegistry.getDefaultInstance().add(block, encouragement, flammability);
    }
}
