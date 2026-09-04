package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.dimdev.dimcore.api.event.PlayerTeleportEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListRespawnMixin {
    // Respawn builds a fresh ServerPlayer instead of teleporting, so ServerPlayerTeleportMixin never sees it.
    // ordinal 1 because the method parameter, the dead player, is the ordinal 0 ServerPlayer local.
    @Inject(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;createCommonSpawnInfo(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/network/protocol/game/CommonPlayerSpawnInfo;"
            )
    )
    private void dimcore$beforeRespawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer respawned) {
        PlayerTeleportEvents.BEFORE.invoker().accept(respawned, respawned.serverLevel(), respawned.position());
    }

    @Inject(method = "respawn", at = @At("RETURN"))
    private void dimcore$afterRespawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer respawned = cir.getReturnValue();
        PlayerTeleportEvents.AFTER.invoker().accept(respawned, respawned.serverLevel(), respawned.position());
    }
}
