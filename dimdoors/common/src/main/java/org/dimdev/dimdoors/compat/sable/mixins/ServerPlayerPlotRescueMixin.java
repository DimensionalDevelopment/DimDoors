package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.compat.sable.SableLevelSpaceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerPlotRescueMixin {
    @Unique
    private static final int dimdoors$RECOVERY_INTERVAL_TICKS = 20;

    @Unique
    private int dimdoors$recoveryCooldown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void dimdoors$rescueFromMissingSablePlotHolder(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel level = player.serverLevel();

        if (!SableLevelSpaceHelper.INSTANCE.isUnavailableNow(level, player.blockPosition())) {
            this.dimdoors$recoveryCooldown = 0;
            return;
        }

        if (this.dimdoors$recoveryCooldown > 0) {
            this.dimdoors$recoveryCooldown--;
            return;
        }

        this.dimdoors$recoveryCooldown = dimdoors$RECOVERY_INTERVAL_TICKS;

        if (!SableLevelSpaceHelper.INSTANCE.isUnavailable(level, player.blockPosition())) {
            return;
        }

        this.dimdoors$rescue(player, level);
    }

    @Unique
    private void dimdoors$rescue(ServerPlayer player, ServerLevel level) {
        ServerLevel destLevel = null;
        BlockPos destPos = null;

        ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnLevel != null && respawnPos != null
                && !SableLevelSpaceHelper.INSTANCE.isUnavailableNow(respawnLevel, respawnPos)) {
            destLevel = respawnLevel;
            destPos = respawnPos;
        }

        if (destLevel == null) {
            BlockPos spawn = level.getSharedSpawnPos();

            if (!SableLevelSpaceHelper.INSTANCE.isUnavailableNow(level, spawn)) {
                destLevel = level;
                destPos = spawn;
            }
        }

        if (destLevel == null) {
            destLevel = player.server.getLevel(Level.OVERWORLD);

            if (destLevel == null) {
                return;
            }

            destPos = destLevel.getSharedSpawnPos();
        }

        Vec3 target = Vec3.atBottomCenterOf(destPos);
        player.teleportTo(destLevel, target.x(), target.y(), target.z(), Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
    }
}
