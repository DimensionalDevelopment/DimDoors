package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.dimdev.dimdoors.item.MaskWearerEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public boolean dead;

    @ModifyReturnValue(method = "getVisibilityPercent", at = @At("RETURN"))
    private double dimdoors$enlightenedMaskVisibility(double original, @Local(argsOnly = true) Entity lookingEntity) {
        if (lookingEntity instanceof Enemy && (Object) this instanceof ServerPlayer player && MaskWearerEffects.isWearing(player, MaskType.ENLIGHTENED)) {
            return original * 1.5;
        }

        return original;
    }
}
