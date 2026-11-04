package org.dimdev.dimdoors.listener

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorItemRegistrar
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
        val registrar = getDimensionalDoorItemRegistrar()
        val item = stack.item
        if (!registrar.isRegistered(item)) return InteractionResult.PASS //item instanceof RiftRemoverItem || (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof DimensionalDoorBlock) ? InteractionResult.PASS : InteractionResult.FAIL;

        val eye = player.eyePosition
        val result = world.clip(object : ClipContext(eye, eye.add(player.lookAngle.scale(player.blockInteractionRange())), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player) {
            override fun getBlockShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape =
                if (state.`is`(ModBlocks.DETACHED_RIFT)) Shapes.block() else super.getBlockShape(state, level, pos)
        })
        if (!world.getBlockState(result.blockPos).`is`(ModBlocks.DETACHED_RIFT)) return InteractionResult.PASS

        return registrar.place(item, DimDoorBlockPlaceContext(player, hand, stack, result))!!
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

        constructor(context: UseOnContext) : this(
            context.player,
            context.hand,
            context.itemInHand,
            BlockHitResult(context.clickLocation, context.clickedFace, context.clickedPos, context.isInside)
        )

        fun setToProperReplaced() {
            this.replaceClicked = level.getBlockState(hitResult.blockPos).canBeReplaced(this)
        }
    }
}
