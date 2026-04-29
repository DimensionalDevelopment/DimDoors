package org.dimdev.dimdoors.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {

    @Unique
    private static boolean unraveled$isPointedDripstoneOrSpike(BlockState state) {
        return state.is(Blocks.POINTED_DRIPSTONE)
                || state.is(ModBlocks.UNRAVELED_SPIKE);
    }

    @Redirect(
            method = {
                    "isTip",
                    "isPointedDripstoneWithDirection",
                    "method_33278",
                    "method_33275",
                    "method_33281"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
            )
    )
    private static boolean unraveled$redirectPointedDripstoneChecks(BlockState state, Block block) {
        if (block == Blocks.POINTED_DRIPSTONE) {
            return unraveled$isPointedDripstoneOrSpike(state);
        }

        return state.is(block);
    }
}