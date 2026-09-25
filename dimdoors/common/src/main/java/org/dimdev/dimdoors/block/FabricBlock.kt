package org.dimdev.dimdoors.block

import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.tag.ModBlockTags

class FabricBlock internal constructor(color: DyeColor) : Block(
    Properties.ofFullCopy(Blocks.STONE).mapColor(color).strength(1.2f).lightLevel { 15 }
) {
    public override fun canBeReplaced(blockState: BlockState, context: BlockPlaceContext): Boolean {
        if (context.getPlayer()!!.isShiftKeyDown) return false
        val heldBlock = byItem(context.player!!.getItemInHand(context.getHand()).item)


        if (heldBlock.builtInRegistryHolder().`is`(ModBlockTags.DOES_NOT_REPLACE_FABRIC) || !heldBlock.defaultBlockState()
                .isCollisionShapeFullBlock(context.level, context.clickedPos)
        ) return false
        return heldBlock !is EntityBlock && heldBlock !is FabricBlock
    }
}
