package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {

    @ModifyArg(
            method = "preventDropFromBottomPart",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            ),
            index = 1
    )
    private static BlockState tweakThingy(BlockState arg2, @Local(argsOnly = true) BlockState blockState
    ) {
        return blockState.getBlock() instanceof DimensionalDoorBlock ? ModBlocks.DETACHED_RIFT.get().defaultBlockState() : arg2;
    }
}
