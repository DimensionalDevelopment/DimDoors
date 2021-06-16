package org.dimdev.dimdoors.mixin.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.dimdev.dimdoors.client.ModShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	protected abstract Shader loadShader(ResourceFactory arg, String string, VertexFormat vertexFormat) throws IOException;

	@Inject(method = "loadShaders", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 1, target = "java/util/List.add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onReload(ResourceManager manager, CallbackInfo ci, List list, List list2) throws IOException {
		list2.add(Pair.of(new Shader(manager, "dimensional_portal", VertexFormats.POSITION), (Consumer<Shader>) ModShaders::setDimensionalPortal));
	}
}
