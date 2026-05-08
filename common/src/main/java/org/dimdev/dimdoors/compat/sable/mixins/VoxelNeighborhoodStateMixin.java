package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VoxelNeighborhoodState.class)
public class VoxelNeighborhoodStateMixin {
//    @Inject(method = "isSolid", at = @At("HEAD"), cancellable = true)
//    private static void dimdoors$treatDetachedRiftAsSolid(BlockGetter blockGetter, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
//        if (state.is(ModBlocks.DETACHED_RIFT)) {
//            cir.setReturnValue(true);
//        }
//    }
//
//    @Inject(method = "isFullBlock", at = @At("HEAD"), cancellable = true)
//    private static void dimdoors$treatDetachedRiftAsFullBlock(BlockGetter blockGetter, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
//        if (state.is(ModBlocks.DETACHED_RIFT)) {
//            cir.setReturnValue(true);
//        }
//    }
}
