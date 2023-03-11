package org.dimdev.dimdoors.mixin;

import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.dimdev.dimdoors.api.block.ExplosionConvertibleBlock;

@Mixin(Explosion.class)
public class ExplosionMixin {
	@Mutable
	@Shadow
	@Final
	private ObjectArrayList<BlockPos> affectedBlocks;

	@Shadow
	@Final
	private Level world;

	@Inject(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;shuffle(Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/util/math/random/Random;)V", ordinal = 0, shift = At.Shift.AFTER))
	private void handleExplosionConvertibleBlocks(boolean b1, CallbackInfo ci) {
		this.affectedBlocks = this.affectedBlocks.stream().filter(blockPos -> {
			BlockState state = this.world.getBlockState(blockPos);
			Block block = state.getBlock();
			if (!(block instanceof ExplosionConvertibleBlock)) {
				return true;
			}
			InteractionResult result = ((ExplosionConvertibleBlock) block).explode(this.world, blockPos, state, state.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null);
			return result == InteractionResult.PASS;
		}).collect(Collectors.toCollection(ObjectArrayList::new));
	}
}
