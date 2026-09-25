package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.SaplingBlock
import net.minecraft.world.level.block.grower.TreeGrower
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.world.feature.ModFeatures
import java.util.*

class DriftwoodSaplingBlock(properties: Properties) : SaplingBlock(
    TreeGrower(
        "driftwood",
        0.0f,
        Optional.empty(),
        Optional.empty(),
        Optional.of(ModFeatures.Configured.DRIFTWOOD_TREE),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    ), properties
) {
    override fun mayPlaceOn(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos) = blockState.`is`(ModBlocks.UNRAVELLED_FABRIC)
}
