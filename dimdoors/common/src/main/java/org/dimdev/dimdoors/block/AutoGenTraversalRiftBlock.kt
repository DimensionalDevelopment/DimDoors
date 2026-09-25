package org.dimdev.dimdoors.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

interface AutoGenTraversalRiftBlock<T : EntranceRiftBlockEntity<T>> : TraversableRiftBlock<T> {
    val originalBlock: Block

    override fun getVisualBlockState(state: BlockState): BlockState = originalBlock.withPropertiesOf(state)
}
