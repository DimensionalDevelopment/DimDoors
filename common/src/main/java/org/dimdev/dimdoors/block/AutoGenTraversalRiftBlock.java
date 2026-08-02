package org.dimdev.dimdoors.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import static org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar.transferProperty;

public interface AutoGenTraversalRiftBlock<T extends EntranceRiftBlockEntity<?>> extends TraversableRiftBlock<T> {
    @Override
    default BlockState getVisualBlockState(BlockState state) {
        var baseState = getOriginalBlock().defaultBlockState();

        return state.getProperties().stream()
                .filter(baseState::hasProperty)
                .reduce(
                        baseState,
                        (newState, property) -> transferProperty(state, newState, property),
                        (a, b) -> b
                );
    }

    Block getOriginalBlock();
}
