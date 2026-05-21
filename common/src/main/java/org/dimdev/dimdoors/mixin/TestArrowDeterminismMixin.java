package org.dimdev.dimdoors.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class TestArrowDeterminismMixin {
    // Temporary deterministic projectile test hook; remove after portal-arrow repro work.
    @ModifyArg(
            method = "shoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;shoot(DDDFF)V"
            ),
            index = 4
    )
    private float dimdoors$removeArrowLaunchUncertainty(float uncertainty) {
        return 0.0F;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.99F))
    private float dimdoors$removeArrowAirDrag(float inertia) {
        return 1.0F;
    }

    @Inject(method = "getWaterInertia", at = @At("HEAD"), cancellable = true)
    private void dimdoors$removeArrowWaterDrag(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0F);
    }
}
