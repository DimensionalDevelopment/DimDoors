package org.dimdev.dimdoors.mixin.client;

import java.io.IOException;

import org.dimdev.dimdoors.client.ModShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_5944;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	protected abstract class_5944 method_34522(ResourceFactory arg, String string, VertexFormat vertexFormat) throws IOException;

	@Inject(method = "method_34538", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/render/GameRenderer;method_34522(Lnet/minecraft/resource/ResourceFactory;Ljava/lang/String;Lnet/minecraft/client/render/VertexFormat;)Lnet/minecraft/class_5944;"))
	public void onReload(ResourceManager resourceManager, CallbackInfo ci) throws IOException {
		ModShaders.setDimensionalPortal(this.method_34522(resourceManager, "dimensional_portal", VertexFormats.POSITION));
	}
}
