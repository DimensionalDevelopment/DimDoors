package org.dimdev.dimdoors.network.packet.s2c

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.network.packet.type

data class PlayerInventorySlotUpdateS2CPacket(val slot: Int, val stack: ItemStack) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val TYPE = "player_inventory_slot_update".type<PlayerInventorySlotUpdateS2CPacket>()

        val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, PlayerInventorySlotUpdateS2CPacket::slot,
                ItemStack.STREAM_CODEC, PlayerInventorySlotUpdateS2CPacket::stack,
                ::PlayerInventorySlotUpdateS2CPacket
            )
    }
}
