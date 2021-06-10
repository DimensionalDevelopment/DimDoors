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
	private static final int RANDOM_ACTION_BOUND = 200;
	private static final int CHANCE_TO_DECREASE_ARMOR_DURABILITY = 10;
	private static final int CHANCE_TO_REPLACE_ITEMSLOT_WITH_UNRAVLED_FABRIC = 30;
	Random random = new Random();

	@Shadow
	public abstract void incrementStat(Identifier stat);

	public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void mobTickMixin(CallbackInfo ci) {
		if (PlayerModifiersComponent.getFray(this) >= 125) {
			if (random.nextInt(RANDOM_ACTION_BOUND) == 0) {
				doRandomFunction(this);
			}
		}
	}

	private void doRandomFunction(LivingEntity player) {
		switch (random.nextInt(2)) {
			case 0:
				decreaseArmorDurability((PlayerEntity) player);
				break;
			case 1:
				addRandomUnravledFabric((PlayerEntity) player);
				break;
			default:
		}

	}

	private void addRandomUnravledFabric(PlayerEntity player) {
		if(random.nextInt(CHANCE_TO_REPLACE_ITEMSLOT_WITH_UNRAVLED_FABRIC) == 0) {
			int slot = random.nextInt(player.getInventory().main.size());
			if(player.getInventory().main.get(slot).isEmpty() || player.getInventory().main.get(slot).getItem() == ModItems.UNRAVELLED_FABRIC) {
				if(player.getInventory().main.get(slot).getCount() < 64)
					player.getInventory().main.set(slot, new ItemStack(ModItems.UNRAVELLED_FABRIC, 1+player.getInventory().main.get(slot).getCount()));
			}
		}
	}

	private void decreaseArmorDurability(PlayerEntity player) {
		for (int i = 0; i < player.getInventory().armor.size(); i++)
			if (random.nextInt(CHANCE_TO_DECREASE_ARMOR_DURABILITY) == 0)
				player.getArmorItems().forEach((itemStack) -> {
					itemStack.setDamage(itemStack.getDamage() + 1);
				});
	}

	@Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
	public void handleLimboFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (this.world.getDimension().equals(ModDimensions.LIMBO_DIMENSION.getDimension())) {
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
