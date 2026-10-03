package org.dimdev.dimdoors.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsDetachedRift
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext

class PlaceOnlyOnRiftBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.FAIL

        return placeOnDetachedRift(player, context.hand, context.itemInHand)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        val result = placeOnDetachedRift(player, hand, stack)
        if (result.consumesAction()) {
            return InteractionResultHolder<ItemStack?>(result, stack)
        }

        return InteractionResultHolder.pass<ItemStack?>(stack)
    }

    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val player = ctx.player ?: return InteractionResult.FAIL

        val context = ctx as? DimDoorBlockPlaceContext ?: DimDoorBlockPlaceContext(ctx, findDetachRift(player, RaycastHelper.DETACH))

        if (!context.level.getBlockState(context.clickedPos).`is`(ModBlocks.DETACHED_RIFT)) return InteractionResult.FAIL

        if (context.level.isClientSide) return super.place(context)

        val detachedRiftBlockEntity = context.level.getBlockEntity(context.clickedPos)?.castOrNull<DetachedRiftBlockEntity>() ?: return InteractionResult.FAIL

        val result = super.place(context)
        if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
            context.level.getBlockEntity(context.clickedPos)?.castOrNull<Rift>()?.also { rift ->
                rift.copyFrom(detachedRiftBlockEntity)
                rift.updateType()
            }
        }

        return result
    }

    private fun placeOnDetachedRift(player: Player, hand: InteractionHand, stack: ItemStack): InteractionResult {
        val hitResult = findDetachRift(player, RaycastHelper.DETACH)
        if (!hitsDetachedRift(hitResult, player.level())) {
            return InteractionResult.FAIL
        }

        return place(DimDoorBlockPlaceContext(player, hand, stack, hitResult))
    }
}