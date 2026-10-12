package org.dimdev.dimcore.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {
    @Unique
    private Program dimcore$geometry;

    @Shadow
    private static Program getOrCreate(ResourceProvider resourceProvider, Program.Type programType, String name) throws IOException {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/shaders/ProgramManager;linkShader(Lcom/mojang/blaze3d/shaders/Shader;)V"))
    private void dimcore$loadGeometry(CallbackInfo ci, @Local(argsOnly = true) ResourceProvider provider, @Local JsonObject json) throws IOException {
        Program.Type type = Program.Type.valueOf("DIMCORE_GEOMETRY");
        if (json.has(type.getName())) dimcore$geometry = getOrCreate(provider, type, GsonHelper.getAsString(json, type.getName()));
    }

    @Inject(method = "attachToProgram", at = @At("HEAD"))
    private void dimcore$attachGeometry(CallbackInfo ci) {
        if (dimcore$geometry != null) dimcore$geometry.attachToShader((Shader) this);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void dimcore$closeGeometry(CallbackInfo ci) {
        if (dimcore$geometry != null) dimcore$geometry.close();
    }
}
