package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.dimdev.dimdoors.api.client.DimensionalDoorsRendertargets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(GameConfig gameConfig, CallbackInfo ci) {
        DimensionalDoorsRendertargets.init();
    }
}
