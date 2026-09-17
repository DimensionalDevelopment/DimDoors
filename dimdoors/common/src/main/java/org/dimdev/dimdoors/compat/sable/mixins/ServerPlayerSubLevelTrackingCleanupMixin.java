package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public class ServerPlayerSubLevelTrackingCleanupMixin {
    @Inject(method = "changeDimension", at = @At("HEAD"))
    private void dimdoors$removeSourceSubLevelTracking(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel source = player.serverLevel();
        ServerLevel destination = transition.newLevel();

        if (source.dimension().equals(destination.dimension())) {
            return;
        }

        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(source);
        if (container == null) {
            return;
        }

        UUID playerId = player.getUUID();
        for (ServerSubLevel subLevel : container.getAllSubLevels()) {
            subLevel.getTrackingPlayers().remove(playerId);
        }
    }
}
