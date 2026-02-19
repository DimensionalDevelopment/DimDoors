package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange,
                                        List<AutoSyncedAddon> addons) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("sync_pocket_addons");
    public static final Type<SyncPocketAddonsS2CPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = StreamCodec.of(SyncPocketAddonsS2CPacket::write, SyncPocketAddonsS2CPacket::read);

    public static SyncPocketAddonsS2CPacket read(FriendlyByteBuf buf) {
        return new SyncPocketAddonsS2CPacket(buf.readResourceKey(Registries.DIMENSION),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                AutoSyncedAddon.readAutoSyncedAddonList(buf));
    }

    public static void write(FriendlyByteBuf buf, SyncPocketAddonsS2CPacket packet) {
        buf.writeResourceKey(packet.world());
        buf.writeInt(packet.gridSize());
        buf.writeInt(packet.pocketId());
        buf.writeInt(packet.pocketRange());
        AutoSyncedAddon.writeAutoSyncedAddonList(buf, packet.addons());
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}