package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.block.PerservesBlockEntity;
import org.dimdev.dimdoors.util.schematic.RelativeBlockSample;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    private @Nullable BlockEntity blockEntityToBetransfered;

    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos blockPos);

    @Shadow
    public abstract BlockState getBlockState(BlockPos blockPos);

    @WrapOperation(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    public void onPlaceCheck(BlockState instance, Level level, BlockPos blockPos, BlockState state, boolean b, Operation<Void> original) {
        //This is a much more brute force version of how world edit does it.
        if(RelativeBlockSample.shouldUpdate) {
            original.call(instance, level, blockPos, state, b);
        }
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onRemove(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    public void blockEntityStore(BlockPos blockPos, BlockState blockState, boolean bl, CallbackInfoReturnable<BlockState> cir2) {
        if(blockState.getBlock() instanceof PerservesBlockEntity b && b.isCompatible(getBlockState(blockPos))) {
            this.blockEntityToBetransfered = this.getBlockEntity(blockPos);
        }
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;addAndRegisterBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"))
    public void blockEntityRetrieve(BlockPos blockPos, BlockState newState, boolean moved, CallbackInfoReturnable<BlockState> cir, @Local(name = "blockEntity") BlockEntity blockEntity) {
        if(blockEntityToBetransfered != null) {
            if(newState.getBlock() instanceof PerservesBlockEntity perservesBlockEntity) {
                perservesBlockEntity.attemptTransfer(blockEntity, blockEntityToBetransfered);
                blockEntityToBetransfered = null;
            };
        }
    }
}
