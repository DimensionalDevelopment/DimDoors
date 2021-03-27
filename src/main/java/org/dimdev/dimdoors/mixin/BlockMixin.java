package org.dimdev.dimdoors.mixin;

import org.dimdev.dimdoors.criteria.ModCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "onBreak", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/Block;spawnBreakParticles(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	public void triggerTagBlockBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
		if (!world.isClient()) {
			ModCriteria.TAG_BLOCK_BREAK.trigger((ServerPlayerEntity) player, state.getBlock());
		}
	}
}
