package org.dimdev.dimdoors.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.blockentity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.item.door.UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext

class PlaceOnlyOnRiftBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.player == null) return InteractionResult.FAIL

        return place(DimDoorBlockPlaceContext(context.player, context.hand, context.itemInHand, BlockHitResult(context.clickLocation, context.clickedFace, context.clickedPos, context.isInside)))
    }

    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val context = ctx //as? DimDoorBlockPlaceContext ?: return InteractionResult.FAIL

        if (!context.level.getBlockState(context.clickedPos).`is`(ModBlocks.DETACHED_RIFT)) return InteractionResult.FAIL

        if (context.level.isClientSide) return super.place(context)

        val detachedRiftBlockEntity = context.level.getBlockEntity(context.clickedPos)?.castOrNull<DetachedRiftBlockEntity>() ?: return InteractionResult.FAIL

        val result = super.place(context)
        if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
            context.level.getBlockEntity(context.clickedPos)?.castOrNull<BlockEntityRift<*>>()?.also { rift ->
                rift.copyFrom(detachedRiftBlockEntity)
                rift.updateType()
            }
        }

        return result
    }
}