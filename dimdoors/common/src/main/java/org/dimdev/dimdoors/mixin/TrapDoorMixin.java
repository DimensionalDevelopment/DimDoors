package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.Rift;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
    public @Nullable Rift convertToRiftProvider(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        @Nullable var blockState = this.getRiftProviderState(state);
        if (blockState == null) return null;
        world.setBlockAndUpdate(pos, blockState);

        return ((RiftVariantProvider) blockState.getBlock()).convertToRiftProvider(world, pos, blockState);
    }

    @Override
    public @Nullable BlockState getRiftProviderState(@NotNull BlockState state) {
        Block dimensionalDoor = DimensionalDoors.getDimensionalDoorBlockRegistrar().getDimensionalVariant((Block) (Object) this);

        return dimensionalDoor instanceof RiftVariantProvider ? dimensionalDoor.withPropertiesOf(state) : null;

    }
}
