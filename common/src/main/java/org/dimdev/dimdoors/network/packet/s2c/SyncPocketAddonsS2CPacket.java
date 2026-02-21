package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncPocketAddonsS2CPacket(ResourceKey<Level> world, int gridSize, int pocketId, int pocketRange,
                                        List<PocketAddon> addons) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("sync_pocket_addons");
    public static final Type<SyncPocketAddonsS2CPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPocketAddonsS2CPacket> STREAM_CODEC = StreamCodec.composite(
      ResourceKey.streamCodec(Registries.DIMENSION), SyncPocketAddonsS2CPacket::world,
            ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::gridSize,
            ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::pocketId,
            ByteBufCodecs.VAR_INT, SyncPocketAddonsS2CPacket::pocketRange,
            PocketAddon.LIST_STREAM_CODEC, SyncPocketAddonsS2CPacket::addons,
            SyncPocketAddonsS2CPacket::new

    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}