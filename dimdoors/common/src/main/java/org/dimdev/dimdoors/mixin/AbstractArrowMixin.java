package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.dimdev.dimdoors.enchantment.TranscendentProjectiles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @ModifyExpressionValue(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;",
                    ordinal = 0
            )
    )
    private EntityType<?> dimdoors$transcendentProjectilesApplyEndermanHitEffects(EntityType<?> type) {
        // Vanilla returns before arrow post-hit effects for Endermen; marked arrows should use the normal hit path.
        if (type == EntityType.ENDERMAN && TranscendentProjectiles.isMarked((Entity) (Object) this)) {
            return ((Entity) (Object) this).getType();
        }

        return type;
    }
}
