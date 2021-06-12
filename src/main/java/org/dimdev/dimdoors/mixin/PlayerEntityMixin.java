package org.dimdev.dimdoors.mixin;

import net.minecraft.item.ItemStack;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.mixin.accessor.EntityAccessor;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.component.PlayerModifiersComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Random;

@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow
	public abstract void incrementStat(Identifier stat);

	public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}


	@Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
	public void handleLimboFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (ModDimensions.isLimboDimension(world)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	public void checkDeath(DamageSource source, CallbackInfo ci) {
		this.doOnDeathStuff(source, ci);
	}

	@Unique
	protected void doOnDeathStuff(DamageSource source, CallbackInfo ci) {
		if (ModDimensions.isPocketDimension(this.world) || DimensionalDoorsInitializer.getConfig().getLimboConfig().universalLimbo) {
			((EntityAccessor) this).setRemovalReason(null);
			this.dead = false;
			this.setHealth(this.getMaxHealth());
			ci.cancel();
		}
	}
}
