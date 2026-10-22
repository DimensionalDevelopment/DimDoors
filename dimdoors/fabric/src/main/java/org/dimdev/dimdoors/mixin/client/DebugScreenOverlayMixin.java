package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.Minecraft;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.Platform;
import org.dimdev.dimcore.api.client.ClientPlatform;
import org.dimdev.dimcore.api.event.DebugTextEvents;
import org.dimdev.dimcore.client.FabricClientPlatform;
import org.dimdev.dimdoors.world.DataValues;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
abstract public class DebugScreenOverlayMixin {
    @Inject(method = "getSystemInformation", at = @At("TAIL"))
    private void addDebugText(CallbackInfoReturnable<List<String>> cir) {
        var list = cir.getReturnValue();
        DebugTextEvents.SYSTEM.invoker().invoke(list);
    }

    @Inject(method = "getGameInformation", at = @At("TAIL"))
    private void addGameText(CallbackInfoReturnable<List<String>> cir) {
        var list = cir.getReturnValue();
        DebugTextEvents.GAME.invoker().invoke(list);
    }
}
