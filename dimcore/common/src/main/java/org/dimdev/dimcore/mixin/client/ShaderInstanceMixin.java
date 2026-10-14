package org.dimdev.dimcore.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Shader;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {
    @Unique
    private static final String DIMCORE_GEOMETRY = "dimcore:geometry";
    @Unique
    private int dimcore$geometry = -1;

    @Inject(method = {
        "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;)V",
        "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"
    }, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/shaders/ProgramManager;linkShader(Lcom/mojang/blaze3d/shaders/Shader;)V"))
    private void dimcore$loadGeometry(CallbackInfo ci, @Local(argsOnly = true) ResourceProvider provider, @Local JsonObject json) throws IOException {
        if (!json.has(DIMCORE_GEOMETRY)) return;
        ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(json, DIMCORE_GEOMETRY));
        String source;
        try (Reader reader = provider.openAsReader(id.withPath(p -> "shaders/core/" + p + ".gsh"))) {
            source = IOUtils.toString(reader);
        }
        int shader = GlStateManager.glCreateShader(GL32.GL_GEOMETRY_SHADER);
        GlStateManager.glShaderSource(shader, List.of(source));
        GlStateManager.glCompileShader(shader);
        if (GlStateManager.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            String log = GlStateManager.glGetShaderInfoLog(shader, 32768);
            GlStateManager.glDeleteShader(shader);
            throw new IOException("Couldn't compile geometry program " + id + ": " + log);
        }
        dimcore$geometry = shader;
    }

    @Inject(method = "attachToProgram", at = @At("HEAD"))
    private void dimcore$attachGeometry(CallbackInfo ci) {
        if (dimcore$geometry != -1) GlStateManager.glAttachShader(((Shader) this).getId(), dimcore$geometry);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void dimcore$closeGeometry(CallbackInfo ci) {
        if (dimcore$geometry != -1) GlStateManager.glDeleteShader(dimcore$geometry);
    }
}
