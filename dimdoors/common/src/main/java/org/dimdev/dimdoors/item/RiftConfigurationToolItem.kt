package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimcore.api.errata.session.Errata
import org.dimdev.dimdoors.block.entity.RiftBlockEntity
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsRift

class RiftConfigurationToolItem internal constructor(properties: Properties) : Item(properties.stacksTo(1)) {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        if (world.isClientSide || player !is ServerPlayer) return InteractionResultHolder.fail<ItemStack?>(stack)

        val hit = findDetachRift(player, RaycastHelper.RIFT)
        if (hitsRift(hit, world)) (world.getBlockEntity(hit.blockPos) as? RiftBlockEntity<*>)?.let { Errata.open(player, it.configSubject(hand, this)) }

        return InteractionResultHolder.success<ItemStack?>(stack)
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        ToolTipHelper.processTranslation(list, "${this.descriptionId}.info")
    }

    companion object {

        const val ID: String = "rift_configuration_tool"
    }
}
