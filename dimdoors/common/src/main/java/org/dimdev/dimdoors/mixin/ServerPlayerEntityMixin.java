package org.dimdev.dimdoors.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.entity.limbo.LimboEntranceSource;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void checkDeathServer(DamageSource source, CallbackInfo ci) {
        this.doOnDeathStuff(source, ci);
        if (ci.isCancelled()) {
            if (ModDimensions.isPocketDimension(this.level())) {
                this.awardStat(ModStats.DEATHS_IN_POCKETS);
            }
            this.awardStat(ModStats.TIMES_SENT_TO_LIMBO);
            TeleportUtil.teleportRandom((Entity) (Object) this, ModDimensions.LIMBO_DIMENSION, 512);
            LimboEntranceSource.ofDamageSource(source).broadcast((Player) (Object) this, this.getServer());
        }
    }

    @Inject(method = "setRespawnPosition", at = @At("TAIL"))
    public void onSpawnPointSet(ResourceKey<Level> dimension, BlockPos pos, float angle, boolean spawnPointSet, boolean bl, CallbackInfo ci) {
        if (ModDimensions.isPocketDimension(dimension)) {
            ModCriteria.POCKET_SPAWN_POINT_SET.trigger((ServerPlayer) (Object) this);
        }
    }
}