package org.dimdev.dimdoors.mixin.forge;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;

    @Shadow @Final protected ServerPlayer player;

    @Unique
    private Pair<BlockState, BlockEntity> dimdoors$pair;

    /**
     * @author Waterpicker
     * @reason In blunt terms, Inject was being a pain in the but. This overwrite honors the contract forge does.
     */
//    @Overwrite(remap = false)
//    private boolean removeBlock(BlockPos arg, boolean canHarvest) {
//        BlockState state = this.level.getBlockState(arg);
//        var be = level.getBlockEntity(arg);
//
//        boolean removed = state.onDestroyedByPlayer(this.level, arg, this.player, canHarvest, this.level.getFluidState(arg));
//        if (removed) {
//            state.getBlock().destroy(this.level, arg, state);
//            DimensionalDoors.afterBlockBreak(this.level, this.player, arg, state, be);
//        }
//        return removed;
//    }

    @Inject(method = "removeBlock", at = @At(value = "HEAD"), remap = false)
    private void onBlockBrokenFront(BlockPos arg, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
        dimdoors$pair = new Pair<>(this.level.getBlockState(arg), this.level.getBlockEntity(arg));
    }


    @Inject(at = @At(value = "TAIL"), method = "removeBlock", remap = false)
    private void onBlockBrokenBack(BlockPos arg, boolean canHarvest, CallbackInfoReturnable<Boolean> cir) {
        DimensionalDoors.afterBlockBreak(this.level, this.player, arg, dimdoors$pair.getFirst(), dimdoors$pair.getSecond());
    }
}
