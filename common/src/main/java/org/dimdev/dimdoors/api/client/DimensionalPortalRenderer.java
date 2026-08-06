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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.ModShaders;
import org.dimdev.limlib.api.client.RenderLayerFactory;

import java.util.*;

public final class DimensionalPortalRenderer {
    public static final ResourceLocation WARP_PATH;
    private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER;
    public static final RenderType RENDER_LAYER;

    public static void renderDimensionalPortal(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
        renderDimensionalPortal(null, matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay, true);
    }

    public static void renderDimensionalPortal(BlockState state, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay, boolean tall) {

    }

    public static void renderDimensionalPortal(BlockState state, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Transformer transformer, float tickDelta, int light, int overlay) {
        var model = switch (state.getBlock()) {
            case DoorBlock doorBlock -> getModelFromDirection(state.getValue(DoorBlock.FACING));
            case TrapDoorBlock trapDoorBlock -> state.getValue(TrapDoorBlock.HALF) == Half.TOP ? TRAPDOOR_TOP : TRAPDOOR_BOTTOM;
            case DimensionalPortalBlock dimensionalPortalBlock -> getModelFromDirection(state.getValue(DoorBlock.FACING));
            default -> DOOR_NORTH;
        };

        renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, transformer, tickDelta, light, overlay);
    }

    public static ModelPart getModelFromDirection(Direction direction) {
        return switch (direction) {
            case EAST -> DOOR_EAST;
            case SOUTH -> DOOR_SOUTH;
            case WEST -> DOOR_WEST;
            default -> DOOR_NORTH;
        };
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

    private static final ModelPart DOOR_SOUTH;
    private static final ModelPart DOOR_NORTH;
    private static final ModelPart DOOR_WEST;
    private static final ModelPart DOOR_EAST;
    private static final ModelPart TRAPDOOR_BOTTOM;
    private static final ModelPart TRAPDOOR_TOP;

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
                        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                        .setTextureState(
                                RenderStateShard.MultiTextureStateShard.builder()
                                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                                        .add(WARP_PATH, false, false)
                                        .build()
                        )
                        .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                        .createCompositeState(false)
        );
        Set<Direction> directions = new HashSet<>(List.of(Direction.values()));

        DOOR_SOUTH = create(0.0F, 0.0F, -3.0F, 16.0F, 32.0F, 3.0F);
        DOOR_NORTH = create(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 3.0F);
        DOOR_WEST = create(0.0F, 0.0F, -3.0F, 16.0F, 32.0F, 3.0F);
        DOOR_EAST = create(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 3.0f);
        TRAPDOOR_BOTTOM = create(0.0F, 0.0F, 0.0F, 16.0F, 3.0F, 16.0F);
        TRAPDOOR_TOP = create(0.0F, 13.0F, 0.0F, 16.0F, 16.0F, 16.0F);
    }

    public static ModelPart create(float x, float y, float z, float width, float height, float length) {
        return new ModelPart(
                List.of(new ModelPart.Cube(
                        0,
                        0,
                        x,y,z,width, height, length,
                        0.0F,
                        0.0F,
                        0.0F,
                        false,
                        16.0F,
                        16.0F,
                        EnumSet.allOf(Direction.class)
                )),
                Map.of()
        );
    }
}
