package org.dimdev.dimdoors.mixin.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.dimdoors.client.ModShaders;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	protected abstract ShaderInstance preloadShader(ResourceProvider resourceProvider, String name, VertexFormat format);

	@Inject(method = "reloadShaders", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 1, target = "java/util/List.add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onReload(ResourceProvider manager, CallbackInfo ci, List list, List list2) throws IOException {
		list2.add(Pair.of(new ShaderInstance(manager, "dimensional_portal", DefaultVertexFormat.POSITION), (Consumer<ShaderInstance>) ModShaders::setDimensionalPortal));
	}
}
