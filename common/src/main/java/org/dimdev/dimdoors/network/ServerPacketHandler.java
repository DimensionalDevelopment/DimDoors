package org.dimdev.dimdoors.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerPacketHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<UUID, PlayerSyncData> DATA_MAP = new HashMap<>();

    public static <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
        DimensionalDoors.getSided().sendPacket(player, packet);
    }

    public static void syncPocketAddonsIfNeeded(ServerPlayer player, ServerLevel level, BlockPos pos) {
        var syncPacket = getSyncData(player).syncPocketAddonsIfNeeded(player, level, pos);

        if(syncPacket != null) sendPacket(player, syncPacket);
    }

    private static PlayerSyncData getSyncData(ServerPlayer player) {
        return DATA_MAP.computeIfAbsent(player.getUUID(), uuid -> new PlayerSyncData());
    }

    // TODO: attach this to some event to detect other kinds teleportation

    public static void sync(ServerPlayer player, ItemStack stack, InteractionHand hand) {
    if (hand == InteractionHand.OFF_HAND) {
        sendPacket(player, new PlayerInventorySlotUpdateS2CPacket(45, stack));
    } else {
        sendPacket(player, new PlayerInventorySlotUpdateS2CPacket(player.getInventory().selected, stack));
    }
    }

    public static @Nullable CustomPacketPayload onAttackBlock(ServerPlayer player, HitBlockWithItemC2SPacket packet) {
    player.getServer().execute(() -> {
        Item item = player.getItemInHand(packet.hand()).getItem();
        if (item instanceof ExtendedItem) {
        ((ExtendedItem) item).onAttackBlock(player.level(), player, packet.hand(), packet.pos(), packet.direction());
        }
    });

        return null;
    }

    public static SyncPocketAddonsS2CPacket onNetworkHandlerInitialized(ServerPlayer player) {
        return getSyncData(player).onNetworkHandlerInitialized(player);
    }


    public static class PlayerSyncData {
        private ResourceKey<Level> lastSyncedPocketWorld;
        private int lastSyncedPocketId = Integer.MIN_VALUE;
        private boolean pocketSyncDirty = true;

        public PlayerSyncData() {}

        public SyncPocketAddonsS2CPacket syncPocketAddonsIfNeeded(ServerPlayer player, Level world, BlockPos pos) {
            if (!ModDimensions.isPocketDimension(world)) return null;
            PocketDirectory directory = DimensionalRegistry.getPocketDirectory(world.dimension());
            Pocket<?, ?> pocket = directory.getPocketAt(pos);
            if (pocket == null) return null;
            if ((pocketSyncDirty || pocket.getId() != lastSyncedPocketId || !world.dimension().location().equals(lastSyncedPocketWorld.location()))) {
                pocketSyncDirty = false;
                lastSyncedPocketId = pocket.getId();
                lastSyncedPocketWorld = world.dimension();
                return new SyncPocketAddonsS2CPacket(world.dimension(), directory.getGridSize(), pocket.getId(), pocket.getRange(), pocket.getAddons(a -> a.getType().isSyncable()));
            }

            return null;
        }

        public SyncPocketAddonsS2CPacket onNetworkHandlerInitialized(ServerPlayer player) {
            return syncPocketAddonsIfNeeded(player, player.level(), player.blockPosition());
        }

        public void markPocketSyncDirty(int id) {
            if (lastSyncedPocketId == id) pocketSyncDirty = true;
        }
    }
}
