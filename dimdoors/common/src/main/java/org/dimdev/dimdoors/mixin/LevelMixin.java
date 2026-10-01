package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.CustomBreakHandling;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void dimdoors$restoreLimboAir(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if (cir.getReturnValue() && !level.isClientSide() && ModDimensions.isLimboDimension(level) && state.isAir()) {
            level.setBlock(pos, ModBlocks.LIMBO_AIR.defaultBlockState(), flags, recursionLeft);
        }
    }

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"))
    public boolean custmomDetoryedState(Level instance, BlockPos arg, BlockState arg2, int i, int j, Operation<Boolean> original, @Local BlockState blockState) {
        if(blockState.getBlock() instanceof CustomBreakHandling custom) {
            var result = custom.customDestroy(instance, arg, blockState, i, j);

            if(result != null) return result;
        }

        return original.call(instance, arg, arg2, i, j);
    }
}
