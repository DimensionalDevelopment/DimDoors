package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import org.dimdev.dimdoors.sound.ModSoundEvents
import java.util.function.Consumer

class RiftStabilizerItem(settings: Properties) : Item(settings), DetachRiftInteracting {
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val rift = level.getBlockEntity(pos) as? DetachedRiftBlockEntity ?: return InteractionResult.PASS
        // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
        if (level.isClientSide) return InteractionResult.SUCCESS
        val serverPlayer = context.player as? ServerPlayer ?: return InteractionResult.PASS

        if (rift.weight > 0) {
            rift.setStabilized()
            level.playSound(
                null,
                serverPlayer.blockPosition(),
                ModSoundEvents.RIFT_CLOSE,
                SoundSource.BLOCKS,
                0.6f,
                1f
            ) // TODO: different sound

            context.itemInHand.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer, Consumer { a: Item? -> })
            serverPlayer.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stabilized"), true)
            return InteractionResult.SUCCESS
        } else {
            serverPlayer.displayClientMessage(
                Component.translatable(this.getDescriptionId() + ".already_stabilized"),
                true
            )
        }
        return InteractionResult.FAIL
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("tools.rift_miss"), true)
            triggerRiftCoreHighlight()
        }

        return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, player.getItemInHand(hand))
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        ToolTipHelper.processTranslation(list, "${this.descriptionId}.info")
    }
}
