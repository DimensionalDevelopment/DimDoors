package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.dimdev.dimdoors.DimensionalDoors;

public class MyRenderLayer extends RenderType {
    public static final Identifier WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
    private static final Identifier KEY_PATH = DimensionalDoors.id("textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = DimensionalDoors.id("textures/other/keyhole_light.png");
    private static final RandomSource RANDOM = RandomSource.create(31100L);

    public MyRenderLayer(String string, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, drawMode, j, bl, bl2, runnable, runnable2);
    }

    public static net.minecraft.client.renderer.rendertype.RenderType getMonolith(Identifier texture) {
    CompositeState multiPhaseParameters = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false))
        .setShaderState(new ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader))
        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
//        .setCullState(RenderStateShard.NO_CULL)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false);
    return RenderType.create("monolith", RenderSetup.builder(RenderPipelines.ENTITY_SOLID).withTexture().useLightmap().useOverlay(), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, multiPhaseParameters);
    }
}
