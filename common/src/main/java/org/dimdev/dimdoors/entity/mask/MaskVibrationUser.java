package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

final class MaskVibrationUser implements VibrationSystem.User {
    private static final int LISTENER_RADIUS = 8;

    private final MaskEntity mask;
    private final PositionSource positionSource;

    MaskVibrationUser(MaskEntity mask) {
        this.mask = mask;
        positionSource = new EntityPositionSource(mask, mask.getEyeHeight());
    }

    @Override
    public int getListenerRadius() {
        return LISTENER_RADIUS;
    }

    @Override
    public PositionSource getPositionSource() {
        return positionSource;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Override
    public boolean canReceiveVibration(
            ServerLevel level,
            BlockPos pos,
            Holder<GameEvent> gameEvent,
            GameEvent.Context context
    ) {
        if (mask.getMaskType() != MaskType.SCULKING
                || !mask.getMode().isPassive()
                || mask.isFrozen()
                || mask.isNoAi()
                || mask.isDeadOrDying()
                || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }

        return isValidPlayer(playerFrom(context.sourceEntity()));
    }

    @Override
    public void onReceiveVibration(
            ServerLevel level,
            BlockPos pos,
            Holder<GameEvent> gameEvent,
            Entity sourceEntity,
            Entity projectileOwner,
            float distance
    ) {
        Player player = playerFrom(sourceEntity);
        if (!isValidPlayer(player)) {
            player = playerFrom(projectileOwner);
        }

        if (isValidPlayer(player)) {
            MaskAlert.alertPocket(mask, player);
        }
    }

    private static Player playerFrom(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }

        if (entity instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            return player;
        }

        return null;
    }

    private static boolean isValidPlayer(Player player) {
        return player != null && !player.isCreative() && !player.isSpectator();
    }
}
