package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimcore.api.util.EntityUtils.chat
import org.dimdev.dimdoors.api.item.AttackBlockResult
import org.dimdev.dimdoors.api.item.AttackBlockResult.Companion.fail
import org.dimdev.dimdoors.api.item.AttackBlockResult.Companion.success
import org.dimdev.dimdoors.api.item.ExtendedItem
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsRift
import org.dimdev.dimdoors.item.component.IdCounter.count
import org.dimdev.dimdoors.item.component.IdCounter.get
import org.dimdev.dimdoors.item.component.IdCounter.getAndIncrement
import org.dimdev.dimdoors.item.component.IdCounter.set
import org.dimdev.dimdoors.network.ServerPacketHandler.sync
import org.dimdev.dimdoors.rift.targets.IdMarker

class RiftConfigurationToolItem internal constructor(properties: Properties) :
    Item(properties.stacksTo(1).durability(16)), ExtendedItem {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        val hit: HitResult = findDetachRift(player, RaycastHelper.RIFT)

        if (world.isClientSide) {
            return InteractionResultHolder.fail<ItemStack?>(stack)
        } else {
            if (hitsRift(hit, world)) {
                val rift = world.getBlockEntity((hit as BlockHitResult).blockPos) as Rift
                when (val destination = rift.data.destination) {
                    is IdMarker if destination.id < get(stack) -> {
                        chat(player, Component.literal("Id: ${destination.id}"))
                    }

                    else -> {
                        val id = getAndIncrement(stack)

                        sync(player as ServerPlayer, stack, hand)

                        chat(player, Component.literal("Rift stripped of data and set to target id: $id"))

                        rift.setDestination(IdMarker(id))
                    }
                }

                return InteractionResultHolder.success<ItemStack?>(stack)
            } else {
                chat(player, Component.literal("Current Count: " + count(stack)))
            }
        }

        return InteractionResultHolder.success<ItemStack?>(stack)
    }

    override fun onAttackBlock(
        world: Level,
        player: Player,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction
    ): AttackBlockResult {
        val stack = player.getItemInHand(hand)

        if (world.isClientSide) {
            if (player.isShiftKeyDown) {
                if (get(stack) != 0 || world.getBlockEntity(pos) is Rift) {
                    return success(true)
                }

                return fail(false)
            }
        } else {
            if (player.isShiftKeyDown) {
                when (val blockEntity = world.getBlockEntity(pos)) {
                    is Rift -> {
                        val destination = blockEntity.data.destination
                        if (destination !is IdMarker || destination.id != -1) {
                            blockEntity.setDestination(IdMarker(-1))
                            chat(player, Component.literal("Rift stripped of data and set to invalid id: -1"))
                            return success(false)
                        }
                    }
                    else -> if (get(stack) != 0) {
                        set(stack, 0)

                        sync(player as ServerPlayer, stack, hand)

                        chat(player, Component.literal("Counter has been reset."))
                        return success(false)
                    }
                }
            }
        }
        return success(false)
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
