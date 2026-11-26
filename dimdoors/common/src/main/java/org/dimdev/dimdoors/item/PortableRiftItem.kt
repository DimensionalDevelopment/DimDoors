package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.rift.Rift
import org.dimdev.dimdoors.rift.item.Holder
import org.dimdev.dimdoors.sound.ModSoundEvents

open class PortableRiftItem(properties: Properties) : Item(properties), ItemRift {
    override fun getDescriptionId(stack: ItemStack): String = "${super.getDescriptionId(stack)}.${if(stack.has(ModDataComponentTypes.RIFT_DATA)) "full" else "empty"}"

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level?.castOrNull<ServerLevel>()?: return InteractionResult.SUCCESS
        val player = context.player ?: return InteractionResult.PASS
        val stack = context.itemInHand
        val pos = context.clickedPos
        val facing = context.clickedFace
        val state = level.getBlockState(context.clickedPos)

        return useOn(level, player, stack, pos, facing, state)
    }

    open fun useOn(level: ServerLevel, player: Player, stack: ItemStack, pos: BlockPos, facing: Direction, state: BlockState): InteractionResult {
        val rift = state.block.castOrNull<RiftProvider<*>>()?.getRift(level, pos, state)?.takeUnless { it is IntrinsicRift } ?: return InteractionResult.PASS
        Rift.transfer(rift, rift.location(), rift, stack, Holder.entity(player), this)
        level.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_START, SoundSource.BLOCKS, 0.6f, 1f)
        return InteractionResult.CONSUME
    }

    override val name: String
        get() = "Portable Rift"
}