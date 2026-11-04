package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimcore.api.errata.session.Errata
import org.dimdev.dimdoors.block.entity.Rift

class RiftConfigurationToolItem internal constructor(properties: Properties) : Item(properties.stacksTo(1)), DetachRiftInteracting {
    override fun useOn(context: UseOnContext): InteractionResult {
        val rift = context.level.getBlockEntity(context.clickedPos) as? Rift<*> ?: return InteractionResult.PASS
        if (context.level.isClientSide) return InteractionResult.SUCCESS
        val player = context.player as? ServerPlayer ?: return InteractionResult.PASS

        Errata.open(player, rift.configSubject(context.hand, this))
        return InteractionResult.SUCCESS
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
