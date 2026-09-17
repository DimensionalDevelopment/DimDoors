package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.BEFORE), cancellable = true)
    public void onExplodeHitConvert(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, CallbackInfo ci) {
        if (this instanceof ExplosionConvertibleBlock convertibleBlock) {
            convertibleBlock.onBlockExploded(blockState, level, blockPos, explosion);
            ci.cancel();
        }
    }
}
