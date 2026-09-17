package org.dimdev.dimcore.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimcore.api.event.PlayerTeleportEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerTeleportMixin {
    // All cross-dimension moves.
    @Inject(method = "changeDimension", at = @At("HEAD"))
    private void dimcore$beforeChangeDimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        PlayerTeleportEvents.BEFORE.invoker().accept(dimcore$player(), transition.newLevel(), transition.pos());
    }

    // Null return means the move was refused.
    @Inject(method = "changeDimension", at = @At("RETURN"))
    private void dimcore$afterChangeDimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() != null) {
            dimcore$fireAfter();
        }
    }

    // Same-dimension moves; cross-level goes through changeDimension.
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"))
    private void dimcore$beforeTeleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot, CallbackInfoReturnable<Boolean> cir) {
        if (level == dimcore$player().level()) {
            PlayerTeleportEvents.BEFORE.invoker().accept(dimcore$player(), level, new Vec3(x, y, z));
        }
    }

    // Anchored to the same-level branch: by RETURN the cross-level branch has already moved the player into
    // level, so a level check there would pass and fire a second time on top of changeDimension.
    @Inject(
            method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFFLjava/util/Set;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void dimcore$afterTeleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot, CallbackInfoReturnable<Boolean> cir) {
        dimcore$fireAfter();
    }

    private void dimcore$fireAfter() {
        ServerPlayer player = dimcore$player();
        PlayerTeleportEvents.AFTER.invoker().accept(player, player.serverLevel(), player.position());
    }

    private ServerPlayer dimcore$player() {
        return (ServerPlayer) (Object) this;
    }
}
