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
import org.dimdev.dimdoors.network.packet.s2c.ClearPocketS2CPacket;
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.jetbrains.annotations.NotNull;
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

    public static void syncPocketAddonsIfNeeded(ServerPlayer player, Pocket<?, ?> pocket) {
        var syncPacket = getSyncData(player).syncPocketAddonsIfNeeded(pocket);

        if(syncPacket != null) sendPacket(player, syncPacket);
    }

    public static void clearPocketIfNeeded(ServerPlayer player) {
        if (getSyncData(player).clearPocketIfNeeded()) sendPacket(player, new ClearPocketS2CPacket());
    }

    private static PlayerSyncData getSyncData(ServerPlayer player) {
        return DATA_MAP.computeIfAbsent(player.getUUID(), uuid -> new PlayerSyncData());
    }

    public static void clear() {
        DATA_MAP.clear();
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

    public static class PlayerSyncData {
        private ResourceKey<Level> lastSyncedPocketWorld;
        private int lastSyncedPocketId = Integer.MIN_VALUE;
        private boolean pocketSyncDirty = true;

        public PlayerSyncData() {}

        public SyncPocketAddonsS2CPacket syncPocketAddonsIfNeeded(@NotNull Pocket<?, ?> pocket) {
            if ((pocketSyncDirty || pocket.getId() != lastSyncedPocketId || !pocket.getWorld().location().equals(lastSyncedPocketWorld.location()))) {
                pocketSyncDirty = false;
                lastSyncedPocketId = pocket.getId();
                lastSyncedPocketWorld = pocket.getWorld();

                return new SyncPocketAddonsS2CPacket(pocket.getWorld(), pocket.getBox(), pocket.getAddons(a -> a.getType().isSyncable()));
            }

            return null;
        }

        /** Resets to the "no pocket" state, reporting whether the player was in one. */
        public boolean clearPocketIfNeeded() {
            if (lastSyncedPocketWorld == null && lastSyncedPocketId == Integer.MIN_VALUE) return false;

            lastSyncedPocketWorld = null;
            lastSyncedPocketId = Integer.MIN_VALUE;
            // Keeps the next syncPocketAddonsIfNeeded from reading the now-null world.
            pocketSyncDirty = true;

            return true;
        }

        public static Pocket<?, ?> getPocket(Level world, BlockPos pos) {
            if (!ModDimensions.isPocketDimension(world)) return null;

            PocketDirectory directory = PocketRegistry.getInstance().getPocketDirectory(world.dimension());

            return directory.getPocketAt(pos);
        }

        public void markPocketSyncDirty(int id) {
            if (lastSyncedPocketId == id) pocketSyncDirty = true;
        }
    }
}
