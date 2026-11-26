package org.dimdev.dimcore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.dimdev.dimcore.api.event.PlayerTeleportEvents;
import org.dimdev.dimcore.api.ext.ServerPlayerExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @WrapOperation(method = "performTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"))
    private static boolean dimcore$teleportAsCommand(Entity entity, ServerLevel level, double x, double y, double z, Set<RelativeMovement> relative, float yRot, float xRot, Operation<Boolean> original) {
        if (!(entity instanceof ServerPlayerExt player)) return original.call(entity, level, x, y, z, relative, yRot, xRot);

        return player.dimcore$teleportAs(PlayerTeleportEvents.Cause.COMMAND, () -> original.call(entity, level, x, y, z, relative, yRot, xRot));
    }
}
