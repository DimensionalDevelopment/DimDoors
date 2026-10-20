package org.dimdev.dimcore.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimcore.FabricPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void dimcore$afterTick(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            FabricPlatform.PLAYER_TICK.invoker().invoke(player);
        }
    }
}
