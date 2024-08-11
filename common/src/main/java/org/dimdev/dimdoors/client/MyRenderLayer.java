package org.dimdev.dimdoors.client;

import com.flowpowered.math.vector.VectorNi;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.dimdev.dimdoors.DimensionalDoors;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

@Environment(EnvType.CLIENT)
public class MyRenderLayer extends RenderType {
    public static final ResourceLocation WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
    public static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);
    private static final ResourceLocation KEY_PATH = DimensionalDoors.id("textures/other/keyhole.png");
    private static final ResourceLocation KEYHOLE_LIGHT = DimensionalDoors.id("textures/other/keyhole_light.png");
    private static final RandomSource RANDOM = RandomSource.create(31100L);

    public MyRenderLayer(String string, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, drawMode, j, bl, bl2, runnable, runnable2);
    }

    public static RenderType CRACK = RenderType.create("crack",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.TRIANGLES,
			256,
			false,
			false,
			CompositeState.builder()
					.setCullState(CullStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.setTextureState(RenderStateShard.NO_TEXTURE)
					.setTransparencyState(new TransparencyStateShard("crack_transparency",
							() -> {
								RenderSystem.enableBlend();
								RenderSystem.blendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
							},
							() -> {
								RenderSystem.disableBlend();
								RenderSystem.defaultBlendFunc();
							})
					)
					.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
					.createCompositeState(false)
	);

    public static RenderType TESSERACT = RenderType.create("tesseract",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			false,
			false,
			CompositeState.builder()
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.setTextureState(new TextureStateShard(DetachedRiftBlockEntityRenderer.TESSERACT_PATH,
							false,
							false)
					)
					.setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
					.createCompositeState(false)
	);

	public static RenderType getMonolith(ResourceLocation texture) {
		RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false))
				.setShaderState(new ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader))
				.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
//				.setCullState(RenderStateShard.NO_CULL)
				.setLightmapState(RenderStateShard.LIGHTMAP)
				.setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false);
		return RenderType.create("monolith", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, multiPhaseParameters);
	}
}
