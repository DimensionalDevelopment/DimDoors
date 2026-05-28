package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.dimdev.dimdoors.item.MaskItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("TAIL"))
    private void dimdoors$renderMaskOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.options.hideGui || minecraft.player == null || !MaskItem.isWearingMask(minecraft.player)) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int borderX = Math.max(20, width / 9);
        int borderY = Math.max(16, height / 8);
        int dark = 0xA8040305;
        int tint = 0x25000000;

        graphics.fill(0, 0, width, height, tint);
        graphics.fill(0, 0, width, borderY, dark);
        graphics.fill(0, height - borderY, width, height, dark);
        graphics.fill(0, borderY, borderX, height - borderY, dark);
        graphics.fill(width - borderX, borderY, width, height - borderY, dark);

        int bridgeWidth = Math.max(10, width / 48);
        int bridgeHeight = Math.max(22, height / 4);
        int centerX = width / 2;
        int centerY = height / 2;
        graphics.fill(centerX - bridgeWidth / 2, centerY - bridgeHeight / 2, centerX + bridgeWidth / 2, centerY + bridgeHeight / 2, 0x70040305);
    }
}
