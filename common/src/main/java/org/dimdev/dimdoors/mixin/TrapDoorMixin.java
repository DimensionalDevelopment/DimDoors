package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar.transferProperty;

@Mixin(TrapDoorBlock.class)
public class TrapDoorMixin implements DoorSoundProvider, RiftVariantProvider {

    @Shadow
    @Final
    private BlockSetType type;

    @Override
    public BlockSetType getSetType() {
    return this.type;
    }

    @Override
    public Optional<? extends RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        Optional<BlockState> providerState = this.getRiftProviderState(state);
        if (providerState.isEmpty()) {
            return Optional.empty();
        }

        BlockState blockState = providerState.get();
        world.setBlockAndUpdate(pos, blockState);

        return ((RiftVariantProvider) blockState.getBlock()).convertToRiftProvider(world, pos, blockState);
    }

    @Override
    public Optional<BlockState> getRiftProviderState(BlockState state) {
        Block dimensionalDoor = DimensionalDoors.getDimensionalDoorBlockRegistrar().getDimensionalVariant((Block) (Object) this);

        if (dimensionalDoor instanceof RiftVariantProvider) {
            var baseState = dimensionalDoor.defaultBlockState();
            return Optional.of(state.getProperties().stream()
                    .filter(baseState::hasProperty)
                    .reduce(
                            baseState,
                            (newState, property) -> transferProperty(state, newState, property),
                            (a, b) -> b
                    ));
        }

        return Optional.empty();
    }
}
