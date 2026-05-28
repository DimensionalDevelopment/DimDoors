package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

public final class MaskAlert {
    private MaskAlert() {
    }

    static void alertPocket(MaskEntity source, @Nullable Player player) {
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        var directory = DimensionalRegistry.getPocketDirectory(source.level().dimension());
        if (directory == null) {
            return;
        }

        var pocket = directory.getPocketAt(source.blockPosition());
        if (pocket == null) {
            return;
        }

        AABB room = AABB.of(pocket.getBox());

        for (MaskEntity mask : source.level().getEntitiesOfClass(MaskEntity.class, room)) {
            if (mask.getMode() == MaskMode.STUNNED || mask.isFrozen()) {
                continue;
            }

            mask.startChase(player);
        }

        source.level().playSound(
                null,
                source.blockPosition(),
                SoundEvents.CHAIN_HIT,
                SoundSource.HOSTILE,
                1.6F,
                0.55F
        );
    }

    public static void alertMasksNearBlock(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player.isCreative() || player.isSpectator()) {
            return;
        }

        AABB search = new AABB(pos).inflate(MaskConstants.BLOCK_ALERT_RANGE);

        for (MaskEntity mask : level.getEntitiesOfClass(MaskEntity.class, search)) {
            alertPocket(mask, player);
        }
    }

    public static void notifyEchoesOfPlacedBlock(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player.isCreative() || player.isSpectator()) {
            return;
        }

        AABB search = new AABB(pos).inflate(MaskConstants.ECHO_BLOCK_NOTICE_RANGE);

        for (MaskEntity mask : level.getEntitiesOfClass(MaskEntity.class, search)) {
            if (mask.canEchoTarget(pos)) {
                mask.setEchoBlockTarget(pos);
            }
        }
    }
}
