package org.dimdev.dimdoors.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.NotNull;

public record PlayerInventorySlotUpdateS2CPacket(int slot, ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = DimensionalDoors.id("player_inventory_slot_update");
    public static final Type<PlayerInventorySlotUpdateS2CPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInventorySlotUpdateS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerInventorySlotUpdateS2CPacket::slot,
            ItemStack.STREAM_CODEC, PlayerInventorySlotUpdateS2CPacket::stack,
            PlayerInventorySlotUpdateS2CPacket::new
    );


    @Override
    public @NotNull Type<PlayerInventorySlotUpdateS2CPacket> type() {
        return TYPE;
    }
}
