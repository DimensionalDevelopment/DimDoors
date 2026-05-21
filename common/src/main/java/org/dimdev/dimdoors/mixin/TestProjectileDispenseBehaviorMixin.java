package org.dimdev.dimdoors.mixin;

import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ProjectileDispenseBehavior.class)
public abstract class TestProjectileDispenseBehaviorMixin {
    // Temporary deterministic dispenser test hook; remove after portal-arrow repro work.
    @ModifyArg(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ProjectileItem;shoot(Lnet/minecraft/world/entity/projectile/Projectile;DDDFF)V"
            ),
            index = 5
    )
    private float dimdoors$removeDispenserProjectileUncertainty(float uncertainty) {
        return 0.0F;
    }
}
