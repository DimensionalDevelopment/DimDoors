package org.dimdev.dimdoors.compat.simulated.mixin;

import dev.simulated_team.simulated.util.assembly.SimAssemblyContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimAssemblyContraption.class)
public class SimAssemblyContraptionMixin {

    @Inject(method = "movementAllowed", at = @At("HEAD"), cancellable = true)
    public void onMovementCheck(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(state.is(ModBlocks.DETACHED_RIFT) || state.is(ModBlocks.DIMENSIONAL_PORTAL)) {
            cir.setReturnValue(true);
        }
    }
}
