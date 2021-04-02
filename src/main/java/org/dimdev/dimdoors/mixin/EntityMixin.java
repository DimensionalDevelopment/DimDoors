package org.dimdev.dimdoors.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 1) // Stop Better End thinking. f*** your mod, only ours will load fluids properly
public abstract class EntityMixin {

	@Shadow
	public World world;

	@Shadow
	public abstract Vec3d getPos();

	@Shadow
	public abstract BlockPos getBlockPos();

	@Shadow public abstract Text getName();

	@Redirect(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isIn(FluidState state, Tag<Fluid> tag) {
		if(!(((Object) this) instanceof EndermanEntity)) {
			if (!state.getFluid().isIn(tag) && !tag.equals(FluidTags.LAVA)) {
				return !state.isEmpty();
			} else {
				return state.isIn(tag);
			}
		}
		return state.isIn(tag);
	}

	@Inject(method = "isWet", at = @At("HEAD"), cancellable = true)
	private void isRaining(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(world.isRaining() || this.world.getFluidState(getBlockPos()).isIn(FluidTags.WATER) && world.getBlockState(getBlockPos()).getBlock() != Blocks.AIR);
	}
}
