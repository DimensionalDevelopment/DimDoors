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

import java.util.function.Consumer;

public final class DimensionalPortalRenderer {
    private static final RenderStateShard.ShaderStateShard DIMENSIONAL_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getDimensionalPortal);

    private static final RenderStateShard.LayeringStateShard PORTAL_LAYERING = new RenderStateShard.LayeringStateShard(
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
    public static final ResourceLocation WARP_PATH = DimensionalDoors.id("textures/other/warp.png");;
    public static final RenderType VANILLA_DIMENSIONAL_PORTAL_RENDER_LAYER = createRenderType(
            "vanilla_dimensional_portal",
            DefaultVertexFormat.POSITION,
            256,
            false,
            builder -> builder
                    .setShaderState(DIMENSIONAL_PORTAL_SHADER)
                    .setLayeringState(PORTAL_LAYERING)
                    .setTextureState(new RenderStateShard.TextureStateShard(WARP_PATH, false, false))
                    .createCompositeState(false)
    );

    public static final RenderType IRIS_DIMENSIONAL_PORTAL_RENDER_LAYER = createRenderType(
                "iris_dimensional_portal",
                DefaultVertexFormat.NEW_ENTITY,
            1536,
                        true,
            builder -> builder
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_SOLID_SHADER)
                    .setLayeringState(PORTAL_LAYERING)
                    .setTextureState(new RenderStateShard.TextureStateShard(WARP_PATH, false, false))
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)

        );

    private static final VoxelShape SOUTH_AABB = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 32.0F, 3.0F);
    private static final VoxelShape NORTH_AABB = Block.box(0.0F, 0.0F, 13.0F, 16.0F, 32.0F, 16.0F);
    private static final VoxelShape WEST_AABB = Block.box(13.0F, 0.0F, 0.0F, 16.0F, 32.0F, 16.0F);
    private static final VoxelShape EAST_AABB = Block.box(0.0F, 0.0F, 0.0F, 3.0F, 32.0F, 16.0F);

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


    private static RenderType createRenderType(
            String name,
            VertexFormat format,
            int bufferSize,
            boolean affectsCrumbling,
            Consumer<RenderType.CompositeState.CompositeStateBuilder> builderConsumer
    ) {
        var state = RenderType.CompositeState.builder();
        builderConsumer.accept(state);

        return RenderType.create(name, format, VertexFormat.Mode.QUADS, bufferSize, affectsCrumbling, false, state.createCompositeState(false));
    }
}
