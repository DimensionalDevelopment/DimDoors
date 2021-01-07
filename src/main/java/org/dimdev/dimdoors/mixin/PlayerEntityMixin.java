package org.dimdev.dimdoors.mixin;

import org.dimdev.dimdoors.world.ModBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
	public void handleLimboFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> cir) {
		if (this.world.getBiome(this.getBlockPos()) == ModBiomes.LIMBO_BIOME) {
			cir.setReturnValue(false);
		}
	}
}
