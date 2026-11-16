package org.dimdev.dimdoors.item.door

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.block.ModBlocks

class UseDoorItemOnBlockCallbackListener : UseItemOnBlockCallback {
    override fun useItemOnBlock(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        val stack = player.getItemInHand(hand)
        val registrar = DimensionalDoors.getDimensionalDoorItemRegistrar()
        val item = stack.item
        if (!registrar.isRegistered(item)) return InteractionResult.PASS

        if (!world.getBlockState(hitResult.blockPos).`is`(ModBlocks.DETACHED_RIFT)) return InteractionResult.PASS

        return registrar.place(item, DimDoorBlockPlaceContext(player, hand, stack, hitResult)) ?: InteractionResult.PASS
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
    }
}