package org.dimdev.dimcore.api.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Loader-side lookup answered after every mod {@link TransferType#lookup} listener has passed. */
public interface TransferBridge {
    <U extends Unit<U>> @Nullable Handle<U> find(TransferType<U> type, Level level, BlockPos pos, @Nullable Direction side);
}
