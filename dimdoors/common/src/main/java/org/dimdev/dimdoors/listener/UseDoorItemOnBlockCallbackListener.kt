package org.dimdev.dimdoors.listener

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorItemRegistrar
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.item.RaycastHelper.DETACH
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsDetachedRift

class UseDoorItemOnBlockCallbackListener : UseItemOnBlockCallback {
    override fun useItemOnBlock(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        val result = findDetachRift(player, DETACH)

        if (!hitsDetachedRift(result, world)) return InteractionResult.PASS
        val stack = player.getItemInHand(hand)
        val registrar = getDimensionalDoorItemRegistrar()
        val item = stack.item
        if (registrar.isRegistered(item)) {
            return registrar.place(
                item,
                DimDoorBlockPlaceContext(player, hand, stack, result)
            )!!
        }

        return InteractionResult.PASS //item instanceof RiftRemoverItem || (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof DimensionalDoorBlock) ? InteractionResult.PASS : InteractionResult.FAIL;
    }

    class DimDoorBlockPlaceContext(
        player: Player?,
        hand: InteractionHand,
        itemStack: ItemStack,
        hitResult: BlockHitResult
    ) : BlockPlaceContext(player, hand, itemStack, hitResult) {
        init {
            this.replaceClicked = true
        }

        constructor(context: BlockPlaceContext, result: BlockHitResult) : this(
            context.player,
            context.hand,
            context.itemInHand,
            result
        )

        fun setToProperReplaced() {
            this.replaceClicked = level.getBlockState(hitResult.blockPos).canBeReplaced(this)
        }
    }
}
