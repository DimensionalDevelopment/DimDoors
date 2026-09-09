package org.dimdev.dimdoors.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.world.fray.ModDataValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderPearlEntityMixin extends ThrowableItemProjectile {
    public ThrownEnderPearlEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    private static boolean isAllowedToTeleportOwner(Entity entity, Level level) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    public void onHit(EntityHitResult result, CallbackInfo ci) {
        if(ModDataValues.FIRED_BY_FARSHOT.has(this)) {
            Entity owner = this.getOwner();
            Entity target = result.getEntity();

            if (owner != null && owner != target
                    && this.level() instanceof ServerLevel level
                    && owner.level() == level) {

                Vec3 ownerPos = owner.position();
                Vec3 targetPos = target.position();
                float ownerYRot = owner.getYRot(), ownerXRot = owner.getXRot();
                float targetYRot = target.getYRot(), targetXRot = target.getXRot();

                owner.unRide();
                target.unRide();

                for (Vec3 pos : new Vec3[]{ownerPos, targetPos}) {
                    level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 48, 0.35, 0.75, 0.35, 0.35);
                    level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                owner.resetFallDistance();
                if (owner instanceof ServerPlayer player) {
                    player.connection.teleport(targetPos.x, targetPos.y, targetPos.z, ownerYRot, ownerXRot);
                } else {
                    owner.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                }

                target.resetFallDistance();
                if (target instanceof ServerPlayer player) {
                    player.connection.teleport(ownerPos.x, ownerPos.y, ownerPos.z, targetYRot, targetXRot);
                } else {
                    target.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z);
                }

                this.discard();
            }
        }
    }
}
