package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerPlotRescueMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void dimdoors$rescueFromMissingSablePlotHolder(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel level = player.serverLevel();

        SableHelper.INSTANCE.ensureSableSubLevelLoaded(level, player.blockPosition());
        if (!SableHelper.INSTANCE.isMissingSablePlotHolder(level, player.blockPosition())) {
            return;
        }

        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }

        BlockPos spawn = overworld.getSharedSpawnPos();
        Vec3 target = Vec3.atBottomCenterOf(spawn);
        player.teleportTo(overworld, target.x(), target.y(), target.z(), Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
    }
}
