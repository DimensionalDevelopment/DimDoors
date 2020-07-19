package org.dimdev.dimdoors.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.world.limbo.LimboBiome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getTextureManager()Lnet/minecraft/client/texture/TextureManager;"), method = "renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V")
    public void renderVignetteOverlay(Entity entity, CallbackInfo info) {
        if(entity.getEntityWorld().getBiome(entity.getBlockPos()) instanceof LimboBiome) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
