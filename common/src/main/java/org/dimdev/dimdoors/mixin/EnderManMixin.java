package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import org.dimdev.dimcore.api.util.EntityUtils;
import org.dimdev.dimdoors.enchantment.TranscendentProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
    @ModifyExpressionValue(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean dimdoors$transcendentProjectilesDoNotTeleport(boolean original, @Local(argsOnly = true) DamageSource source) {
        if (!original) {
            return false;
        }

        var projectile = EntityUtils.getProjectile(source);
        return projectile == null || !TranscendentProjectiles.isMarked(projectile);
    }
}
