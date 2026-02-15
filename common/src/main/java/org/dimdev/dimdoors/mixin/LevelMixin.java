package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.CustomBreakHandling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class LevelMixin {

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"))
    public boolean custmomDetoryedState(Level instance, BlockPos arg, BlockState arg2, int i, int j, Operation<Boolean> original, @Local BlockState blockState) {
        if(blockState.getBlock() instanceof CustomBreakHandling custom) {
            var result = custom.customDestroy(instance, arg, blockState, i, j);

            if(result != null) return result;
        }

        return original.call(instance, arg, arg2, i, j);
    }
}