package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {

    @Inject(method = "preventCreativeDropFromBottomPart", at = @At("HEAD"), cancellable = true)
    private static void stuff(Level level, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
        if(state.getBlock() instanceof DimensionalDoorBlock) ci.cancel();
    }
}
