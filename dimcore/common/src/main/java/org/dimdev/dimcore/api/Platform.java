package org.dimdev.dimcore.api;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.function.TriConsumer;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Platform {
    MinecraftServer getServer();
    boolean isModLoaded(String id);
    boolean isClient();
    long bucketAmount();
    Path getConfigRoot();

    void onServerStarting(Consumer<MinecraftServer> consumer);
    void onServerStarted(Consumer<MinecraftServer> consumer);
    void onServerStopping(Consumer<MinecraftServer> consumer);
    void onServerStopped(Consumer<MinecraftServer> consumer);
    void onPlayerJoin(Consumer<ServerPlayer> consumer);
    void onPlayerQuit(Consumer<ServerPlayer> consumer);
    void onServerLevelTick(Consumer<ServerLevel> consumer);
    void onPlayerChangeWorld(TriConsumer<ServerPlayer, ServerLevel, ServerLevel> consumer);
    void onAttackBlock(AttackBlockCallback callback);
    void onUseItem(UseItemCallback callback);
    void onUseBlock(UseBlockCallback callback);
    void onBeforeBlockBreak(BlockBreakCallback callback);
    void onBeforeBlockPlace(BlockPlaceCallback callback);
    void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer);

    <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet);
    <T extends CustomPacketPayload> void sendPacket(T packet);
    /** Whether the player's client registered a receiver for this payload, ie. whether it has the mod. */
    boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type);

    void registerFuel(ItemLike item, int amount);
    void registerStrippable(Block source, Block target);
    void registerFlammable(Block block, int encouragement, int flammability);

    @FunctionalInterface
    interface AttackBlockCallback {
        InteractionResult attack(Player player, InteractionHand hand, BlockPos pos, Direction direction);
    }

    @FunctionalInterface
    interface UseItemCallback {
        InteractionResult use(Player player, InteractionHand hand);
    }

    @FunctionalInterface
    interface UseBlockCallback {
        InteractionResult use(Player player, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    interface BlockBreakCallback {
        boolean shouldCancel(Level level, BlockPos pos, BlockState state, Player player);
    }

    @FunctionalInterface
    interface BlockPlaceCallback {
        boolean shouldCancel(Level level, BlockPos pos, BlockState state, Entity placer);
    }
}
