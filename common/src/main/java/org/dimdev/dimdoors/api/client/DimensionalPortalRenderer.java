package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.targets.RenderTargets;
import net.minecraft.client.Minecraft;
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
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

import static net.minecraft.client.renderer.RenderStateShard.*;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {

	public static final ResourceLocation WARP_PATH;
	private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER;
	private static final RenderType RENDER_LAYER;
	private static final ModelPart MODEL;
	private static final ModelPart TALL_MODEL;

    private static final Plane NORTH_DOOR, EAST_DOOR, SOUTH_DOOR, WEST_DOOR, BOTTOM_TRAP_DOOR, TOP_TRAP_DOOR;

    public static final List<DoorInstance> instances = new ArrayList<>();

    public static void addInstance(DoorInstance door) {
        instances.add(door);
    }

    public static void render() {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        var sources = Minecraft.getInstance().renderBuffers().bufferSource();
        var buffer = sources.getBuffer(RENDER_LAYER);

        for (var instance : instances) {
            (switch (instance.getShape()) {
                case NORTH_DOOR -> NORTH_DOOR;
                case EAST_DOOR -> EAST_DOOR;
                case SOUTH_DOOR -> SOUTH_DOOR;
                case WEST_DOOR -> WEST_DOOR;
                case TOP_TRAP_DOOR -> TOP_TRAP_DOOR;
                case BOTTOM_TRAP_DOOR -> BOTTOM_TRAP_DOOR;
            }).render(buffer, instance.getModelMatrix());
        }

        sources.endBatch(PORTAL_MASK);

//        DimensionalDoorsRendertargets.renderToScreen();
        instances.clear();
    }

    public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay, boolean tall) {
		ModelPart model = tall ? TALL_MODEL : MODEL;
		renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay);
	}

	public static void renderModelWithPortalShader(ModelPart model, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
		transformer.transform(matrixStack);
		model.render(matrixStack, vertexConsumerProvider.getBuffer(DimensionalDoorsClient.detector.shaderPackOn() ? RenderType.entitySolid(WARP_PATH) : RENDER_LAYER), light, overlay);
	}

    private static final RenderType PORTAL_MASK;

	static {
        WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
        DIMENSIONAL_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getDimensionalPortal);

        PORTAL_MASK = RenderLayerFactory.create(
                "portal_mask",
                DefaultVertexFormat.POSITION,
                VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setOutputState(new RenderStateShard.OutputStateShard("dimensional_portal_target",
                                () -> DimensionalDoorsRendertargets.getDimensionalPortalRenderTarget().bindWrite(false), () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)))
                        .setShaderState(RENDERTYPE_WATER_MASK_SHADER).setTextureState(NO_TEXTURE).createCompositeState(false));

        RENDER_LAYER = RenderLayerFactory.create(
                "dimensional_portal",
                DefaultVertexFormat.POSITION,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
//                        .setOutputState(new RenderStateShard.OutputStateShard("dimensional_portal_target",
//                                () -> DimensionalDoorsRendertargets.getDimensionalPortalRenderTarget().bindWrite(false), () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)))
                        .setShaderState(RenderStateShard.RENDERTYPE_END_PORTAL_SHADER)
                        .setCullState(NO_CULL)
                        .setTextureState(
                                RenderStateShard.MultiTextureStateShard.builder()
                                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                                        .add(WARP_PATH, false, false)
                                        .build()
                        )
                        .createCompositeState(false)
        );

        Set<Direction> directions = new HashSet<>(List.of(Direction.values()));
        ModelPart.Cube small = new ModelPart.Cube(0, 0, 0.2f, 0.2f, -0.1f, 15.8f, 15.8f, 0.01F, 0, 0, 0, false, 1024, 1024, directions);
        MODEL = new ModelPart(Collections.singletonList(small), Collections.emptyMap());
        ModelPart.Cube big = new ModelPart.Cube(0, 0, 0.2f, 0.2f, -0.1f, 15.8f, 31.8f, 0.01F, 0, 0, 0, false, 1024, 1024, directions);
        TALL_MODEL = new ModelPart(Collections.singletonList(big), Collections.emptyMap());

        SOUTH_DOOR = new Plane(
                0.0f, 0.0f, 0.0625f,
                1.0f, 0.0f, 0.0625f,
                1.0f, 2.0f, 0.0625f,
                0.0f, 2.0f, 0.0625f);
        NORTH_DOOR = new Plane(
                0.0f, 0.0f, 0.9375f,
                1.0f, 0.0f, 0.9375f,
                1.0f, 2.0f, 0.9375f,
                0.0f, 2.0f, 0.9375f);
        EAST_DOOR = new Plane(
                0.0625f, 0.0f, 0.0f,
                0.0625f, 0.0f, 1.0f,
                0.0625f, 2.0f, 1.0f,
                0.0625f, 2.0f, 0.0f);
        WEST_DOOR = new Plane(
                0.9375f, 0.0f, 0.0f,
                0.9375f, 0.0f, 1.0f,
                0.9375f, 2.0f, 1.0f,
                0.9375f, 2.0f, 0.0f);
        BOTTOM_TRAP_DOOR = new Plane(
                0.0f, 0.0625f, 0.0f,
                0.0f, 0.0625f, 1.0f,
                0.0f, 0.0625f, 1.0f,
                1.0f, 0.0625f, 1.0f
        );

        TOP_TRAP_DOOR = new Plane(
                0.0f, 0.9375f, 0.0f,
                0.0f, 0.9375f, 1.0f,
                0.0f, 0.9375f, 1.0f,
                1.0f, 0.9375f, 1.0f
        );
    }

    public record Plane(
            float v0x, float v0y, float v0z,
            float v1x, float v1y, float v1z,
            float v2x, float v2y, float v2z,
            float v3x, float v3y, float v3z
    ) {
        public void render(VertexConsumer consumer, Matrix4f modelMatrix) {
            consumer
                    .addVertex(modelMatrix, v0x, v0y, v0z)
                    .addVertex(modelMatrix, v1x, v1y, v1z)
                    .addVertex(modelMatrix, v2x, v2y, v2z)
                    .addVertex(modelMatrix, v3x, v3y, v3z);
        }
    }
}
