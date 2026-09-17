package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FogRenderer.class)
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
