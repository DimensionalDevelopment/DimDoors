package org.dimdev.dimdoors.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.mask.MaskEntity;
import org.dimdev.dimdoors.entity.mask.MaskMode;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChasedEffect extends MobEffect {
    private static final int BLACK_MASK_DELAY_TICKS = 200;
    private static final Map<UUID, PocketVisit> ACTIVE_CHASES = new HashMap<>();

    public ChasedEffect() {
        super(MobEffectCategory.HARMFUL, 0x3F3447);
    }

    public static void giveTo(ServerPlayer player) {
        ACTIVE_CHASES.put(player.getUUID(), new PocketVisit(player.level().dimension(), player.blockPosition()));
        player.addEffect(new MobEffectInstance(ModMobEffects.chased(), MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
        player.displayClientMessage(Component.literal("Something follows you"), true);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            tickPlayer(player);
        }
        return true;
    }

    private static void tickPlayer(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!ModDimensions.isPocketDimension(player.level())) {
            if (!ModDimensions.isLimboDimension(player.level())) {
                ACTIVE_CHASES.remove(id);
                player.removeEffect(ModMobEffects.chased());
            }
            return;
        }

        PocketVisit visit = ACTIVE_CHASES.get(id);
        ResourceKey<Level> dimension = player.level().dimension();
        if (visit == null || !visit.dimension.equals(dimension)) {
            visit = new PocketVisit(dimension, player.blockPosition());
            ACTIVE_CHASES.put(id, visit);
        }

        visit.ticks++;
        if (!visit.spawned && visit.ticks >= BLACK_MASK_DELAY_TICKS) {
            spawnBlackMask(player.serverLevel(), visit.entryPos, player);
            visit.spawned = true;
        }
    }

    private static void spawnBlackMask(ServerLevel level, BlockPos entryPos, ServerPlayer target) {
        MaskEntity mask = ModEntityTypes.MASK.create(level);
        if (mask == null) {
            return;
        }

        BlockPos spawnPos = entryPos.above();
        mask.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, target.getYRot() + 180.0F, 0.0F);
        mask.setMaskType(MaskType.BLACK);
        mask.setMode(MaskMode.CHASE);
        level.addFreshEntity(mask);
        level.playSound(null, spawnPos, SoundEvents.WARDEN_AGITATED, SoundSource.HOSTILE, 1.1F, 0.55F);
    }

    private static class PocketVisit {
        private final ResourceKey<Level> dimension;
        private final BlockPos entryPos;
        private int ticks;
        private boolean spawned;

        private PocketVisit(ResourceKey<Level> dimension, BlockPos entryPos) {
            this.dimension = dimension;
            this.entryPos = entryPos.immutable();
        }
    }
}
