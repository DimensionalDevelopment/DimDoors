package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.Rift;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar.transferProperty;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin implements DoorSoundProvider, RiftVariantProvider {

    @Shadow
    @Final
    private BlockSetType type;

    @Shadow
    @Final
    public static EnumProperty<DoubleBlockHalf> HALF;

    @Override
    public BlockSetType getSetType() {
        return this.type;
    }

    @Override
    public @Nullable Rift convertToRiftProvider(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        @Nullable BlockState blockState = this.getRiftProviderState(state);
        if (blockState == null) return null;

        world.setBlockAndUpdate(pos, blockState);
        world.setBlockAndUpdate(pos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER));

        return ((RiftVariantProvider) blockState.getBlock()).convertToRiftProvider(world, pos, blockState);
    }

    @Override
    public @Nullable BlockState getRiftProviderState(@NotNull BlockState state) {
        Block dimensionalDoor = DimensionalDoors.getDimensionalDoorBlockRegistrar().getDimensionalVariant((Block) (Object) this);
        return dimensionalDoor instanceof RiftVariantProvider ? dimensionalDoor.withPropertiesOf(state) : null;

    }
}
