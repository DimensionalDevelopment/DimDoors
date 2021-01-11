package org.dimdev.dimdoors.client;

import java.util.Random;

import com.flowpowered.math.vector.VectorNi;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.util.Identifier;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public class MyRenderLayer extends RenderLayer {
    public static final Identifier WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
    public static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);
    private static final Identifier KEY_PATH = new Identifier("dimdoors:textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = new Identifier("dimdoors:textures/other/keyhole_light.png");
    private static final Random RANDOM = new Random(31100L);

    public MyRenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, i, j, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer CRACK = RenderLayer.of("crack",
			VertexFormats.POSITION_COLOR,
			GL11.GL_TRIANGLES,
			256,
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
					.build(false)
	);

    public static RenderLayer TESSERACT = RenderLayer.of("tesseract",
			VertexFormats.POSITION_COLOR_TEXTURE,
			GL11.GL_QUADS,
			256,
			MultiPhaseParameters.builder()
					.cull(DISABLE_CULLING)
					.lightmap(RenderPhase.DISABLE_LIGHTMAP)
					.texture(new Texture(DetachedRiftBlockEntityRenderer.TESSERACT_PATH,
							false,
							false)
					)
					.alpha(Alpha.HALF_ALPHA)
					.build(false)
	);

    public static RenderLayer getPortal(int layer) {
        RenderPhase.Transparency transparency;
        RenderPhase.Texture texture;
        if (layer <= 1) {
            transparency = TRANSLUCENT_TRANSPARENCY;
            texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE,
					false,
					false
			);
        } else {
            transparency = ADDITIVE_TRANSPARENCY;
            texture = new RenderPhase.Texture(WARP_PATH, false, false);
        }

        return of(
        		"dimensional_portal",
				VertexFormats.POSITION_COLOR,
				GL11.GL_QUADS,
				256,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
						.transparency(transparency)
						.texture(texture)
						.texturing(new RenderPhase.PortalTexturing(layer))
						.fog(BLACK_FOG)
						.build(false)
		);
    }
}
