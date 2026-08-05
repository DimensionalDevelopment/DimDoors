package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModShaders;
import org.dimdev.limlib.api.client.RenderLayerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DimensionalPortalRenderer {
    public static final ResourceLocation WARP_PATH;
    private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER;
    public static final RenderType RENDER_LAYER;
    private static final ModelPart MODEL;
    private static final ModelPart TALL_MODEL;

    public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
        renderDimensionalPortal(matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay, true);
    }

    public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay, boolean tall) {
        ModelPart model = tall ? TALL_MODEL : MODEL;
        renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay);
    }

    public static void renderModelWithPortalShader(ModelPart model, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
        matrixStack.pushPose();
        try {
            transformer.transform(matrixStack);

            DimensionalDoorsClient.detector.wrap(type -> model.render(matrixStack, vertexConsumerProvider.getBuffer(type), light, overlay));
        } finally {
            matrixStack.popPose();
        }
    }

    private static final RenderStateShard.LayeringStateShard DIMENSIONAL_PORTAL_LAYERING = new RenderStateShard.LayeringStateShard(
            "dimensional_portal_layering",
            () -> {
                RenderSystem.polygonOffset(1.0F, 10.0F);
                RenderSystem.enablePolygonOffset();
            },
            () -> {
                RenderSystem.polygonOffset(0.0F, 0.0F);
                RenderSystem.disablePolygonOffset();
            });

    static {
        WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
        DIMENSIONAL_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getDimensionalPortal);
        RENDER_LAYER = RenderLayerFactory.create(
                "dimensional_portal",
                DefaultVertexFormat.POSITION,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,

                RenderType.CompositeState.builder()
                        .setShaderState(DIMENSIONAL_PORTAL_SHADER)
                        .setLayeringState(DIMENSIONAL_PORTAL_LAYERING)
                        .setTextureState(
                                RenderStateShard.MultiTextureStateShard.builder()
                                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                                        .add(WARP_PATH, false, false)
                                        .build()
                        )
                        .createCompositeState(false)
        );
        Set<Direction> directions = new HashSet<>(List.of(Direction.values()));
        MODEL = create(
                0.0f,
                0.0f,
                -3.0f,
                16f,
                16f,
                3f,
                directions);
        TALL_MODEL = create(
                0f,
                0f,
                -3f,
                16f,
                32f,
                3f,
                directions);
    }

    public static ModelPart create(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, Set<Direction> visibleFaces) {
        var cube = new ModelPart.Cube(0, 0,
                originX,
                originY,
                originZ,
                dimensionX,
                dimensionY,
                dimensionZ,
                0,
                0,
                0,
                false,
                0,
                0,
                visibleFaces);
        return new ModelPart(Collections.singletonList(cube), Collections.emptyMap());
    }
}
