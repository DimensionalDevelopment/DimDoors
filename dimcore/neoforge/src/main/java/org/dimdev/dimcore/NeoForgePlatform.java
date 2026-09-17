package org.dimdev.dimcore;

import com.mojang.brigadier.CommandDispatcher;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimcore.api.Platform;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NeoForgePlatform implements Platform {
    private final Object2IntMap<ItemLike> fuels = new Object2IntLinkedOpenHashMap<>();
    private final Map<Block, Block> strippables = new HashMap<>();

    public NeoForgePlatform() {
        NeoForge.EVENT_BUS.addListener(this::getFuelBurnTime);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::modifyBlockWithTool);
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
    public Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
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
    public void onServerStopping(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStoppingEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    @Override
    public void onServerStopped(Consumer<MinecraftServer> consumer) {
        NeoForge.EVENT_BUS.<ServerStoppedEvent>addListener(event -> consumer.accept(event.getServer()));
    }

    @Override
    public void onPlayerJoin(Consumer<ServerPlayer> consumer) {
        NeoForge.EVENT_BUS.<PlayerEvent.PlayerLoggedInEvent>addListener(event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                consumer.accept(player);
            }
        });
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
    public void onPlayerChangeWorld(TriConsumer<ServerPlayer, ServerLevel, ServerLevel> consumer) {
        NeoForge.EVENT_BUS.<PlayerEvent.PlayerChangedDimensionEvent>addListener(event -> {
            if (!(event.getEntity() instanceof ServerPlayer player)) {
                return;
            }

            ServerLevel origin = player.getServer().getLevel(event.getFrom());
            ServerLevel destination = player.getServer().getLevel(event.getTo());

            if (origin != null && destination != null) {
                consumer.accept(player, origin, destination);
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
    public void onUseItem(UseItemCallback callback) {
        NeoForge.EVENT_BUS.<PlayerInteractEvent.RightClickItem>addListener(event -> {
            InteractionResult result = callback.use(event.getEntity(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    @Override
    public void onUseBlock(UseBlockCallback callback) {
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
    public void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        NeoForge.EVENT_BUS.<RegisterCommandsEvent>addListener(event -> consumer.accept(event.getDispatcher()));
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacket(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return player.connection.hasChannel(type);
    }

    @Override
    public void registerFuel(ItemLike item, int amount) {
        fuels.put(item, amount);
    }

    @Override
    public void registerStrippable(Block source, Block target) {
        strippables.put(source, target);
    }

    @Override
    public void registerFlammable(Block block, int encouragement, int flammability) {
        ((FireBlock) Blocks.FIRE).setFlammable(block, encouragement, flammability);
    }
}
