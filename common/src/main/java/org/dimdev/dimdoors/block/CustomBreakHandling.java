package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.jetbrains.annotations.Nullable;

public interface CustomBreakHandling {
    @Nullable Boolean customDestroy(Level instance, BlockPos arg, BlockState blockState, int i, int j);
}
