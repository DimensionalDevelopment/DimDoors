package org.dimdev.dimdoors.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @ModifyExpressionValue(
            method = "crack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private static BlockState crackModify(BlockState state) {
        return checkIfDimensionalDoor(state);
    }

    @ModifyVariable(method = "destroy", at = @At("HEAD"), index = 2, argsOnly = true)
    private static BlockState destroyModify(BlockState state) {
        return checkIfDimensionalDoor(state);
    }

    @Unique
    private static BlockState checkIfDimensionalDoor(BlockState state) {
        if (state.getBlock() instanceof DimensionalDoorBlock dimensionalDoorBlock)
            return dimensionalDoorBlock.getEffectiveBlockState(state);
        else if (state.getBlock() instanceof DimensionalTrapDoorBlock dimensionalDoorBlock)
            return dimensionalDoorBlock.getEffectiveBlockState(state);
        return state;
    }
}
