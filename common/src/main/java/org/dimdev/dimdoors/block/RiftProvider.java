package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.Rift;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface RiftProvider<T extends BlockEntity & Rift> extends EntityBlock, RiftVariantProvider, PerservesBlockEntity {

    default T getRift(Level world, BlockPos pos, BlockState state) {
        var rifPos = getRiftPos(world, pos, state);

        return world.getBlockEntity(rifPos, getRiftBlockEnityType())
                .orElseGet(() -> {
                    DimensionalDoors.LOGGER.warn(providerType() + " at " + rifPos + " in world " + world + " contained no rift.");
                    return null;
                });
    }

    default BlockPos getRiftPos(Level world, BlockPos pos, BlockState state) {
        return pos;
    }

    @Override
    default Optional<? extends Rift> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state)  {
        pos = getRiftPos(world, pos, state);
        var rift = getRift(world, pos, state);
        return Optional.ofNullable(rift);
    }

    default String providerType() {
        return "Rift Block";
    }

    default boolean isTall(BlockState cachedState) {
        return false;
    }

    default boolean stateContainsRift(BlockState oldState) {
        return true;
    }

    @Override
    default boolean isCompatible(BlockState oldState) {
        return oldState.getBlock() instanceof RiftProvider<?> riftProvider && riftProvider.stateContainsRift(oldState);
    }

    @Override
    default void attemptTransfer(BlockEntity blockEntity, @Nullable BlockEntity blockEntityToBetransfered) {
        if (blockEntity instanceof Rift rift1 && blockEntityToBetransfered instanceof Rift rift2) {
            rift1.copyFrom(rift2);
        }
    }

    BlockEntityType<T> getRiftBlockEnityType();

    default @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return getRiftBlockEnityType().create(pos, state);
    }

    static <R extends BlockEntity & Rift> void tickRift(Level level, BlockPos blockPos, BlockState state, R rift) {
        rift.tick(level, blockPos, state);
    }
}
