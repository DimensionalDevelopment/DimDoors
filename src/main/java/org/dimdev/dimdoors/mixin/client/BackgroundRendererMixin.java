package org.dimdev.dimdoors.mixin.client;

import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @ModifyVariable(
            method = "render",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private static double modifyVoidColor(double scale) {
        if(ModDimensions.isPrivatePocketDimension(MinecraftClient.getInstance().world)) {
            scale = 1.0;
        }
        return scale;
    }
}