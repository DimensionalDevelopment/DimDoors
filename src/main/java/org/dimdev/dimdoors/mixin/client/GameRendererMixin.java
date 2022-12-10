package org.dimdev.dimdoors.mixin.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.dimdev.dimdoors.client.ModShaders;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	protected abstract ShaderProgram preloadProgram(ResourceFactory arg, String string, VertexFormat vertexFormat) throws IOException;

	@Inject(method = "loadPrograms", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 1, target = "java/util/List.add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onReload(ResourceFactory manager, CallbackInfo ci, List list, List list2) throws IOException {
		list2.add(Pair.of(new ShaderProgram(manager, "dimensional_portal", VertexFormats.POSITION), (Consumer<ShaderProgram>) ModShaders::setDimensionalPortal));
	}
}
