package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import org.dimdev.dimdoors.client.RenderUtils;
import org.dimdev.limlib.api.client.RenderLayerFactory;

public final class DimensionalPortalRenderer {
    private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER;
    private static final RenderStateShard.LayeringStateShard PORTAL_LAYERING;
    public static final ResourceLocation WARP_PATH;
    public static final RenderType RENDER_LAYER;


    private static final VoxelShape SOUTH_AABB;
    private static final VoxelShape NORTH_AABB;
    private static final VoxelShape WEST_AABB;
    private static final VoxelShape EAST_AABB;

    public static void renderDimensionalPortal(BlockState state, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        var model = switch (state.getBlock()) {
            case DoorBlock ignored -> getModelFromDirection(state.getValue(DoorBlock.FACING));
            case TrapDoorBlock ignored -> state.getValue(TrapDoorBlock.HALF) == Half.TOP ? TrapDoorBlock.TOP_AABB : TrapDoorBlock.BOTTOM_AABB;
            case DimensionalPortalBlock ignored -> getModelFromDirection(state.getValue(DoorBlock.FACING));
            default -> TrapDoorBlock.TOP_AABB;
        };

        renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, light, overlay);
    }

    public static void renderModelWithPortalShader(VoxelShape model, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {


        matrixStack.pushPose();
        try {
            DimensionalDoorsClient.detector.wrap(type -> RenderUtils.renderCube(model, matrixStack, vertexConsumerProvider.getBuffer(type), light, overlay));
        } finally {
            matrixStack.popPose();
        }
    }

    public static VoxelShape getModelFromDirection(Direction direction) {

        return switch (direction) {
            case EAST -> EAST_AABB;
            case NORTH -> NORTH_AABB;
            case WEST -> WEST_AABB;
            default -> SOUTH_AABB;
        };
    }

    static {
        WARP_PATH = DimensionalDoors.id("textures/other/warp.png");
        PORTAL_LAYERING = new RenderStateShard.LayeringStateShard(
                "dimensional_portal_offset",
                () -> {
                    RenderSystem.enablePolygonOffset();
                    RenderSystem.polygonOffset(1.0F, 1.0F);
                },
                () -> {
                    RenderSystem.polygonOffset(0.0F, 0.0F);
                    RenderSystem.disablePolygonOffset();
                }
        );

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
                        .setLayeringState(PORTAL_LAYERING)
                        .setTextureState(
                                RenderStateShard.MultiTextureStateShard.builder()
                                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                                        .add(WARP_PATH, false, false)
                                        .build()
                        )
                        .createCompositeState(false)
        );

        SOUTH_AABB = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 3.0F);
        NORTH_AABB = Block.box(0.0F, 0.0F, 13.0F, 16.0F, 32.0F, 16.0F);
        WEST_AABB = Block.box(13.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F);
        EAST_AABB = Block.box(0.0F, 0.0F, 0.0F, 3.0F, 32.0F, 16.0F);
    }

}
