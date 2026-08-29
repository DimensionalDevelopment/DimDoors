package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.compat.sable.SableCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * Moves a player out of a Sable plot whose level space cannot be resolved.
 *
 * <p>A player left standing in an occupied plot with no live chunk holder has no valid ground, so
 * DimDoors relocates them. Recovery is attempted first: the plot may simply be waiting on chunks,
 * and teleporting the player away from a base that was about to load is far more disruptive than
 * waiting a moment.</p>
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerPlotRescueMixin {
    /**
     * Ticks between attempts to materialize the plot before relocating the player.
     *
     * <p>Recovery can scan holding-region files on disk. Running it on every tick would stall the
     * server for as long as the player remained in an unresolvable plot, which is precisely the
     * situation this code exists to handle.</p>
     */
    @Unique
    private static final int dimdoors$RECOVERY_INTERVAL_TICKS = 20;

    @Unique
    private int dimdoors$recoveryCooldown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void dimdoors$rescueFromMissingSablePlotHolder(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel level = player.serverLevel();

        if (!SableCompat.HELPER.isLevelSpaceUnavailableNow(level, player.blockPosition())) {
            this.dimdoors$recoveryCooldown = 0;
            return;
        }

        if (this.dimdoors$recoveryCooldown > 0) {
            this.dimdoors$recoveryCooldown--;
            return;
        }

        this.dimdoors$recoveryCooldown = dimdoors$RECOVERY_INTERVAL_TICKS;

        // Give Sable a chance to materialize the plot before giving up on the player's position.
        if (!SableCompat.HELPER.isLevelSpaceUnavailable(level, player.blockPosition())) {
            return;
        }

        this.dimdoors$rescue(player, level);
    }

    /**
     * Relocates a player away from an unresolvable plot.
     *
     * <p>Destinations are tried in order of how little they disrupt the player: their respawn point,
     * then the spawn of the level they are already in, and only then the overworld. The plot grid
     * occupies one region of a level, so staying in that level is normally viable and avoids
     * dropping someone into a different dimension because a contraption failed to load.</p>
     *
     * <p>Each candidate is checked as well, since a respawn point can itself sit inside a Sable
     * sub-level such as a bed on a moving base.</p>
     */
    @Unique
    private void dimdoors$rescue(ServerPlayer player, ServerLevel level) {
        ServerLevel destLevel = null;
        BlockPos destPos = null;

        ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnLevel != null && respawnPos != null
                && !SableCompat.HELPER.isLevelSpaceUnavailableNow(respawnLevel, respawnPos)) {
            destLevel = respawnLevel;
            destPos = respawnPos;
        }

        if (destLevel == null) {
            BlockPos spawn = level.getSharedSpawnPos();

            if (!SableCompat.HELPER.isLevelSpaceUnavailableNow(level, spawn)) {
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
