package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.joml.Matrix4f;

import java.util.*;

import static net.minecraft.client.renderer.RenderStateShard.*;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {

    private static final RenderType RENDER_LAYER;

    private static final Plane NORTH_DOOR, EAST_DOOR, SOUTH_DOOR, WEST_DOOR, BOTTOM_TRAP_DOOR, TOP_TRAP_DOOR;

    private static final List<DoorInstance> instances = new ArrayList<>();

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

        sources.endBatch(RENDER_LAYER);
        instances.clear();
    }

    static {
        RENDER_LAYER = RenderLayerFactory.create(
                "dimensional_portal",
                DefaultVertexFormat.POSITION,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setOutputState(new RenderStateShard.OutputStateShard("dimensional_portal_target",
                                () -> {
                                    Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                                }, () -> {}))
                        .setShaderState(RenderStateShard.RENDERTYPE_END_PORTAL_SHADER)
                        .setCullState(NO_CULL)
                        .setTextureState(
                                RenderStateShard.MultiTextureStateShard.builder()
                                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                                        .add(DimensionalDoors.id("textures/other/warp.png"), false, false)
                                        .build()
                        )
                        .createCompositeState(false)
        );


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
                1.0f, 0.0625f, 1.0f,
                1.0f, 0.0625f, 0.0f
        );

        TOP_TRAP_DOOR = new Plane(
                0.0f, 0.9375f, 0.0f,
                0.0f, 0.9375f, 1.0f,
                1.0f, 0.9375f, 1.0f,
                1.0f, 0.9375f, 0.0f
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
