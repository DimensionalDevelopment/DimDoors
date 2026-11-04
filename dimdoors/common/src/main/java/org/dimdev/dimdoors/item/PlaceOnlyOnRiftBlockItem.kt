package org.dimdev.dimdoors.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext

class PlaceOnlyOnRiftBlockItem(block: Block, properties: Properties) : BlockItem(block, properties), DetachRiftInteracting {
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.player == null) return InteractionResult.FAIL

        return place(DimDoorBlockPlaceContext(context))
    }

    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val context = ctx as? DimDoorBlockPlaceContext ?: return InteractionResult.FAIL

        if (!context.level.getBlockState(context.clickedPos).`is`(ModBlocks.DETACHED_RIFT)) return InteractionResult.FAIL

        if (context.level.isClientSide) return super.place(context)

        val detachedRiftBlockEntity = context.level.getBlockEntity(context.clickedPos)?.castOrNull<DetachedRiftBlockEntity>() ?: return InteractionResult.FAIL

        val result = super.place(context)
        if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
            context.level.getBlockEntity(context.clickedPos)?.castOrNull<Rift<*>>()?.also { rift ->
                rift.copyFrom(detachedRiftBlockEntity)
                rift.updateType()
            }
        }

        return result
    }
}