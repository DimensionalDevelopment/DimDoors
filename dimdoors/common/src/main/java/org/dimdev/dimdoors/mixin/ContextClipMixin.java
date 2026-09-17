package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.RaycastHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ClipContext.class)
public abstract class ContextClipMixin {
    @Inject(method = "getBlockShape", at = @At("HEAD"), cancellable = true)
    public void checkIfRift(BlockState blockState, BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if(RaycastHelper.predicate != null && RaycastHelper.predicate.test(level.getBlockEntity(pos))) cir.setReturnValue(Shapes.block());
    }
}
