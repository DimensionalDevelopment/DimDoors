package org.dimdev.dimdoors.compat.create.mixin;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.dimdev.dimdoors.compat.create.SlidingDoorInterop;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlidingDoorBlock.class, remap = false)
public abstract class SlidingDoorBlockMixin {
    @Inject(method = "isDoubleDoor", at = @At("HEAD"), cancellable = true)
    private static void dimdoors$isMixedDoubleDoor(BlockState state, DoorHingeSide hinge, Direction facing, BlockState otherDoor, CallbackInfoReturnable<Boolean> cir) {
        if (SlidingDoorInterop.isMixedDoubleDoor(state, hinge, facing, otherDoor)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setOpen", at = @At("RETURN"))
    private void dimdoors$setDimensionalCounterpartOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open, CallbackInfo ci) {
        if (!state.is((Block) (Object) this) || state.getValue(DoorBlock.OPEN) == open) {
            return;
        }

        BlockState changedState = SlidingDoorInterop.setOpen(state, open, open);
        DoorHingeSide hinge = changedState.getValue(DoorBlock.HINGE);
        Direction facing = changedState.getValue(DoorBlock.FACING);
        BlockPos otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing);
        BlockState otherDoor = level.getBlockState(otherPos);

        if (!SlidingDoorInterop.isMixedDoubleDoor(changedState, hinge, facing, otherDoor)) {
            return;
        }

        BlockState changedOtherDoor = SlidingDoorInterop.setOpen(otherDoor, open, open);
        level.setBlock(otherPos, changedOtherDoor, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        SlidingDoorInterop.scheduleWaterTickIfNeeded(level, otherPos, changedOtherDoor);
    }
}
