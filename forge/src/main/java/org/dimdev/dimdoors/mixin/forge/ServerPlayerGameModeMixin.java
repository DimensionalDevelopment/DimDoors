package org.dimdev.dimdoors.mixin.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;

    @Shadow @Final protected ServerPlayer player;

    /**
     * @author Waterpicker
     * @reason In blunt terms, Inject was being a pain in the but. This overwrite honors the contract forge does.
     */
    @Overwrite(remap = false)
    private boolean removeBlock(BlockPos arg, boolean canHarvest) {
        BlockState state = this.level.getBlockState(arg);
        var be = level.getBlockEntity(arg);

        boolean removed = state.onDestroyedByPlayer(this.level, arg, this.player, canHarvest, this.level.getFluidState(arg));
        if (removed) {
            state.getBlock().destroy(this.level, arg, state);
            DimensionalDoors.afterBlockBreak(this.level, this.player, arg, state, be);
        }
        return removed;
    }


//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"), method = "removeBlock", locals = LocalCapture.PRINT)
//    private void onBlockBroken(BlockPos arg, boolean canHarvest, CallbackInfoReturnable<Boolean> cir, BlockState state, boolean removed) {
//        DimensionalDoors.afterBlockBreak(this.level, this.player, arg, state, this.level.getBlockEntity(arg));
//    }
}
