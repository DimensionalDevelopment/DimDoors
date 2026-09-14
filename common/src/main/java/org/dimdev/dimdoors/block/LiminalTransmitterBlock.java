package org.dimdev.dimdoors.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.block.entity.LiminalTransmitterBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.rift.targets.LocationProvider;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiminalTransmitterBlock extends WaterLoggableBlockWithEntity implements RiftProvider<LiminalTransmitterBlockEntity> {
    public static final MapCodec<LiminalTransmitterBlock> CODEC = simpleCodec(LiminalTransmitterBlock::new);

    public LiminalTransmitterBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<LiminalTransmitterBlockEntity> getRiftBlockEnityType() {
        return ModBlockEntityTypes.LIMINAL_TRANSMITTER;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT)) {
            return null;
        }

        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
            return defaultBlockState();
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (level.isClientSide()) return;

        int strength = level.getBestNeighborSignal(pos);

        var rift = getRift(level, pos, state);

        attemptRedstoneTransmission(strength, rift);
    }

    public static boolean attemptRedstoneTransmission(int strength, Rift rift) {
        rift.setStateDirty(false);

        // Attempt a teleport
        try {
            Target target = rift.getTarget();
            var location = target instanceof LocationProvider provider ? provider.getLocation() : null;

            return target.as(Targets.REDSTONE).recieveSignal(strength, location);
        } catch (Exception ignored) {
        }

        return false;
    }
}
