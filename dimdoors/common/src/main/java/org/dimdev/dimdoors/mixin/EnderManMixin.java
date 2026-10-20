package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimcore.api.ext.EntityExtensionsKt;
import org.dimdev.dimdoors.item.MaskWearerEffects;
import org.dimdev.dimdoors.world.DataValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

        var projectile = EntityExtensionsKt.getProjectile(source);
        return projectile == null || !DataValues.TRANSCENDENT_PROJECTILE.has(projectile);
    }

    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void dimdoors$ignoreMaskWearers(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (MaskWearerEffects.wornType(player) != null) {
            cir.setReturnValue(false);
        }
    }
}
