package org.dimdev.dimdoors.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.BackgroundRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
//    @ModifyVariable(
//            method = "render",
//            at = @At(value = "STORE", ordinal = 0),
//            ordinal = 0
//    )
//    private static double modifyVoidColor(double scale) {
//        if(ModDimensions.isPrivatePocketDimension(MinecraftClient.getInstance().world)) {
//            scale = 1.0;
//        }
//        return scale;
//    }
}
