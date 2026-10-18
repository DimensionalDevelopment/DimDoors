package org.dimdev.dimcore.api.errata.subject

import com.mojang.serialization.DataResult
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntity
import org.dimdev.dimcore.api.errata.schema.StructNode

interface ConfigSubject {
    val title: Component

    fun schema(player: ServerPlayer): StructNode

    fun read(player: ServerPlayer): CompoundTag

    fun write(player: ServerPlayer, value: CompoundTag, paths: List<List<String>>): DataResult<Unit>

    fun isValid(player: ServerPlayer): Boolean
}

abstract class BlockEntitySubject<T : BlockEntity>(val blockEntity: T, private val hand: InteractionHand, private val item: Item) : ConfigSubject {
    override fun isValid(player: ServerPlayer): Boolean {
        val level = blockEntity.level ?: return false
        val pos = blockEntity.blockPos

        return !blockEntity.isRemoved && level.getBlockEntity(pos) === blockEntity && player.level() === level && player.canInteractWithBlock(pos, 0.0) && player.getItemInHand(hand).`is`(item)
    }
}
