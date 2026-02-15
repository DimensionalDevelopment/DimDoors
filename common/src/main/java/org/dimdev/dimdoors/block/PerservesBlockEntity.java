package org.dimdev.dimdoors.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface PerservesBlockEntity {

    boolean isCompatible(BlockState oldState);

    void attemptTransfer(BlockEntity blockEntity, @Nullable BlockEntity blockEntityToBetransfered);
}
