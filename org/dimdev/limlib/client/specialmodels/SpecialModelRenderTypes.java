package org.dimdev.limlib.client.specialmodels;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SpecialModelRenderTypes {

    private static final RenderStateShard.LayeringStateShard SPECIAL_MODEL_LAYERING = new RenderStateShard.LayeringStateShard(
        "limlib_special_model_layering",
        () -> {
            RenderSystem.polygonOffset(1.0F, 10.0F);
            RenderSystem.enablePolygonOffset();
        },
        () -> {
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
        });

    private static final Map<ResourceLocation, RenderType> specialModelRenderTypes = new LinkedHashMap<>();
    private static volatile List<RenderType> chunkBufferLayers = List.of();

    public static synchronized void clearSpecialModelRenderTypes() {
        specialModelRenderTypes.clear();
        chunkBufferLayers = List.of();
    }

    @Nullable
    public static synchronized RenderType getOrCreateSpecialModelRenderType(ResourceLocation id) {
        RenderType existing = specialModelRenderTypes.get(id);

        if (existing != null) {
            return existing;
        }

        if (!SpecialModelShaderRegistry.isRegistered(id)) {
            return null;
        }

        RenderType created = createSpecialModelRenderType(id);
        specialModelRenderTypes.put(id, created);
        chunkBufferLayers = List.copyOf(specialModelRenderTypes.values());
        return created;
    }

    public static boolean isSpecialModelRenderType(RenderType renderType) {
        return !IrisCompat.shouldDisableSpecialModelRenderTypes() && isKnownSpecialModelRenderType(renderType);
    }

    public static boolean isKnownSpecialModelRenderType(RenderType renderType) {
        return chunkBufferLayers.contains(renderType);
    }

    public static List<RenderType> chunkBufferLayers() {
        if (IrisCompat.shouldDisableSpecialModelRenderTypes()) {
            return List.of();
        }

        return chunkBufferLayers;
    }

    private static RenderType createSpecialModelRenderType(ResourceLocation rendererId) {
        RenderStateShard.ShaderStateShard shader = new RenderStateShard.ShaderStateShard(() -> SpecialModelShaderRegistry.getShader(rendererId));
        VertexFormat vertexFormat = SpecialModelShaderRegistry.getVertexFormat(rendererId);

        return RenderType
            .create(
                "limlib_special_model_" + rendererId.toString().replace(':', '_').replace('/', '_'),
                vertexFormat,
                VertexFormat.Mode.QUADS,
                RenderType.BIG_BUFFER_SIZE,
                true,
                true,
                RenderType.CompositeState
                    .builder()
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setShaderState(shader)
                    .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLayeringState(SPECIAL_MODEL_LAYERING)
                    .createCompositeState(true));
    }

    private SpecialModelRenderTypes() {
    }
}
