package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public class MyRenderLayer extends RenderLayer {
    public MyRenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, i, j, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer CRACK = RenderLayer.of("crack", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, MultiPhaseParameters.builder()
            .cull(DISABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .texture(NO_TEXTURE)
            .transparency(new Transparency("crack_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }))
            .build(false));

    public static RenderLayer TESSERACT = RenderLayer.of("tesseract", VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, MultiPhaseParameters.builder()
            .cull(DISABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .texture(new Texture(new Identifier("dimdoors:textures/other/tesseract.png"),false, false))
            .build(false));
}
