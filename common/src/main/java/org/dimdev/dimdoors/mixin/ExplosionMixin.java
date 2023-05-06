package org.dimdev.dimdoors.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Mixin(Explosion.class)
public class ExplosionMixin {
	@Mutable
	@Shadow
	@Final
	private ObjectArrayList<BlockPos> toBlow;

	@Shadow
	@Final
	private Level level;

	@Inject(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;shuffle(Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/util/RandomSource;)V", ordinal = 0, shift = At.Shift.AFTER))
	private void handleExplosionConvertibleBlocks(boolean b1, CallbackInfo ci) {
		this.toBlow = this.toBlow.stream().filter(blockPos -> {
			BlockState state = this.level.getBlockState(blockPos);
			Block block = state.getBlock();
			if (!(block instanceof ExplosionConvertibleBlock)) {
				return true;
			}
			InteractionResult result = ((ExplosionConvertibleBlock) block).explode(this.level, blockPos, state, state.hasBlockEntity() ? this.level.getBlockEntity(blockPos) : null);
			return result == InteractionResult.PASS;
		}).collect(Collectors.toCollection(ObjectArrayList::new));
	}
}
