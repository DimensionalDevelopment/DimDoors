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
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.item.RaycastHelper.DETACH
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsDetachedRift
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import org.dimdev.dimdoors.sound.ModSoundEvents
import java.util.function.Consumer

class RiftStabilizerItem(settings: Properties) : Item(settings) {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        val hit = findDetachRift(player, DETACH)

        if (world.isClientSide) {
            if (hitsDetachedRift(hit, world)) {
                // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
                return InteractionResultHolder<ItemStack?>(InteractionResult.SUCCESS, stack)
            } else {
                player.displayClientMessage(Component.translatable("tools.rift_miss"), true)
                triggerRiftCoreHighlight()
                return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, stack)
            }
        }

        if (hitsDetachedRift(hit, world)) {
            val rift = world.getBlockEntity(hit.getBlockPos()) as DetachedRiftBlockEntity?
            if (rift!!.getWeight() > 0) {
                rift.setStabilized()
                world.playSound(
                    null,
                    player.blockPosition(),
                    ModSoundEvents.RIFT_CLOSE,
                    SoundSource.BLOCKS,
                    0.6f,
                    1f
                ) // TODO: different sound

                val serverPlayer = player as ServerPlayer

                stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer, Consumer { a: Item? -> })
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stabilized"), true)
                return InteractionResultHolder<ItemStack?>(InteractionResult.SUCCESS, stack)
            } else {
                player.displayClientMessage(
                    Component.translatable(this.getDescriptionId() + ".already_stabilized"),
                    true
                )
            }
        }
        return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, stack)
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
