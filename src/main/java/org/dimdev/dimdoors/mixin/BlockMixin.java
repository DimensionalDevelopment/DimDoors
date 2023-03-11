package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.criteria.ModCriteria;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "onBreak", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/Block;spawnBreakParticles(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	public void triggerTagBlockBreak(Level world, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
		if (!world.isClientSide()) {
			ModCriteria.TAG_BLOCK_BREAK.trigger((ServerPlayer) player, state);
		}
	}
}
