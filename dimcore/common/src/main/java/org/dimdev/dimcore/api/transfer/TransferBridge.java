package org.dimdev.dimcore.api.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface TransferBridge {

    <U extends Unit<U>> @Nullable Handle<U> find(TransferType<U> type, Level level, BlockPos pos, @Nullable Direction side);

    boolean interactWithFluid(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side);
}
