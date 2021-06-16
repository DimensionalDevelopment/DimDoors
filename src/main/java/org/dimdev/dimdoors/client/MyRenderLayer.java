package org.dimdev.dimdoors.client;

import java.util.Random;

import com.flowpowered.math.vector.VectorNi;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import org.dimdev.dimdoors.api.client.RenderLayerFactory;

import net.minecraft.util.Identifier;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

@Environment(EnvType.CLIENT)
public class MyRenderLayer extends RenderLayer {
    public static final Identifier WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
    public static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);
    private static final Identifier KEY_PATH = new Identifier("dimdoors:textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = new Identifier("dimdoors:textures/other/keyhole_light.png");
    private static final Random RANDOM = new Random(31100L);

    public MyRenderLayer(String string, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, drawMode, j, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer CRACK = RenderLayerFactory.create("crack",
			VertexFormats.POSITION_COLOR,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			false,
			MultiPhaseParameters.builder()
					.cull(DISABLE_CULLING)
					.lightmap(RenderPhase.DISABLE_LIGHTMAP)
					.texture(NO_TEXTURE)
					.transparency(new Transparency("crack_transparency",
							() -> {
								RenderSystem.enableBlend();
								RenderSystem.blendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
							},
							() -> {
								RenderSystem.disableBlend();
								RenderSystem.defaultBlendFunc();
							})
					)
					.shader(RenderPhase.COLOR_SHADER)
					.build(false)
	);

    public static RenderLayer TESSERACT = RenderLayerFactory.create("tesseract",
			VertexFormats.POSITION_COLOR_TEXTURE,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			false,
			MultiPhaseParameters.builder()
					.cull(DISABLE_CULLING)
					.lightmap(RenderPhase.DISABLE_LIGHTMAP)
					.texture(new Texture(DetachedRiftBlockEntityRenderer.TESSERACT_PATH,
							false,
							false)
					)
					.shader(RenderPhase.POSITION_COLOR_TEXTURE_SHADER)
					.build(false)
	);

	public static RenderLayer getMonolith(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, false)).shader(new Shader(GameRenderer::getRenderTypeEntityTranslucentShader)).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).depthTest(RenderPhase.ALWAYS_DEPTH_TEST).overlay(ENABLE_OVERLAY_COLOR).build(false);
		return RenderLayerFactory.create("monolith", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
	}
}
