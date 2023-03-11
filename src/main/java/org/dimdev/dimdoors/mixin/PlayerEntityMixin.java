package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.mixin.accessor.EntityAccessor;
import org.dimdev.dimdoors.world.ModDimensions;

@Mixin(value = Player.class, priority = 900)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow
	public abstract void awardStat(ResourceLocation stat);

	public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}


	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void handleLimboFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (ModDimensions.isLimboDimension(level)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	public void checkDeath(DamageSource source, CallbackInfo ci) {
		this.doOnDeathStuff(source, ci);
	}

	@Unique
	protected void doOnDeathStuff(DamageSource source, CallbackInfo ci) {
		if (ModDimensions.isPocketDimension(this.level) || DimensionalDoors.getConfig().getLimboConfig().universalLimbo) {
			((EntityAccessor) this).setRemovalReason(null);
			this.dead = false;
			this.setHealth(this.getMaxHealth());
			ci.cancel();
		}
	}
}
