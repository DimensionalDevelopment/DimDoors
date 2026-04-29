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
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

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
    public Optional<? extends RiftBlockEntity> convertToRiftProvider(ServerLevel world, BlockPos pos, BlockState state) {
        if (DimensionalDoors.getDimensionalDoorBlockRegistrar().getDimensionalVariant((Block) (Object) this) instanceof DimensionalDoorBlock dimensionalDoor) {
            var baseState = dimensionalDoor.defaultBlockState();

            var blockState = state.getProperties().stream()
                    .filter(baseState::hasProperty)
                    .reduce(
                            baseState,
                            (newState, property) -> transferProperty(state, newState, property),
                            (a, b) -> b
                    );

            world.setBlockAndUpdate(pos, blockState);
            world.setBlockAndUpdate(pos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER));

            return world.getBlockEntity(pos, ModBlockEntityTypes.ENTRANCE_RIFT).map(RiftUtils::registerFunction);
        }
        return Optional.empty();
    }
}
