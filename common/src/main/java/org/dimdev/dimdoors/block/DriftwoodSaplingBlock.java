package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.world.feature.ModFeatures;

import java.util.Optional;

public class DriftwoodSaplingBlock extends SaplingBlock {
    public DriftwoodSaplingBlock(BlockBehaviour.Properties properties) {
        super(new TreeGrower(
                "driftwood",
                0.0f,
                Optional.empty(),
                Optional.empty(),
                Optional.of(ModFeatures.Configured.DRIFTWOOD_TREE),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ), properties);
    }

    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.is(ModBlocks.UNRAVELLED_FABRIC);
    }

}
