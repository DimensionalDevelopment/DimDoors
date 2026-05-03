package org.dimdev.dimdoors.compat.sable.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntranceRiftBlockEntity.class)
public class EntranceRiftSubLevelFixMixin {
    
    @WrapOperation(
        method = "receiveEntity",
        at = @At(
            value = "INVOKE",
            target = "Lorg/dimdev/dimdoors/api/util/TeleportUtil;teleport(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Rotations;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/Entity;"
        )
    )
    private static Entity fixTeleport(Entity entity, Level level, Vec3 targetPos,
                                       Rotations relativeAngle, Vec3 relativeVelocity,
                                       Operation<Entity> original) {

        var correctedTarget = SableCompanion.INSTANCE.projectOutOfSubLevel(level, targetPos);

        return original.call(entity, level, correctedTarget, relativeAngle, relativeVelocity);
    }


    @WrapOperation(
            method = "hasTraversed",
            at = @At(value = "INVOKE", target = "Lorg/dimdev/dimdoors/rift/RiftUtils$PortalPlane;isTraversed(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Z")
    )
    public boolean isTraversedRedirect(RiftUtils.PortalPlane instance, Level level, Vec3 previousPos, Vec3 currentPos, Operation<Boolean> original) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, instance.origin());

        if (subLevel != null) {
            previousPos = subLevel.lastPose().transformPositionInverse(previousPos);
            currentPos = subLevel.logicalPose().transformPositionInverse(currentPos);
        }

        return original.call(instance, level, previousPos, currentPos);
    }
}