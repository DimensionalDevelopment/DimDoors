package org.dimdev.dimdoors.api.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.DimensionalPortalBlock
import org.dimdev.dimdoors.client.DimensionalDoorsClient
import org.dimdev.dimdoors.client.ModShaders
import org.dimdev.dimdoors.client.RenderUtils

object DimensionalPortalRenderer {
    private val DIMENSIONAL_PORTAL_SHADER = RenderStateShard.ShaderStateShard { ModShaders.dimensionalPortal }

    private val PORTAL_LAYERING = RenderStateShard.LayeringStateShard(
        "dimensional_portal_offset",
        {
            RenderSystem.enablePolygonOffset()
            RenderSystem.polygonOffset(1.0f, 1.0f)
        },
        {
            RenderSystem.polygonOffset(0.0f, 0.0f)
            RenderSystem.disablePolygonOffset()
        }
    )

    val WARP_PATH: ResourceLocation = DimensionalDoors.id("textures/other/warp.png")

    val VANILLA_DIMENSIONAL_PORTAL_RENDER_LAYER: RenderType = createRenderType(
        "vanilla_dimensional_portal",
        DefaultVertexFormat.POSITION,
        256,
        false
    ) {
        setShaderState(DIMENSIONAL_PORTAL_SHADER)
        setLayeringState(PORTAL_LAYERING)
        setTextureState(RenderStateShard.TextureStateShard(WARP_PATH, false, false))
        createCompositeState(false)
    }

    @JvmField
    val IRIS_DIMENSIONAL_PORTAL_RENDER_LAYER: RenderType = createRenderType(
        "iris_dimensional_portal",
        DefaultVertexFormat.NEW_ENTITY,
        1536,
        true) {
        setShaderState(RenderStateShard.RENDERTYPE_ENTITY_SOLID_SHADER)
        setLayeringState(PORTAL_LAYERING)
        setTextureState(RenderStateShard.TextureStateShard(WARP_PATH, false, false))
        setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
        setLightmapState(RenderStateShard.LIGHTMAP)
        setOverlayState(RenderStateShard.OVERLAY)

    }

    

    private val SOUTH_AABB: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 3.0)
    private val NORTH_AABB: VoxelShape = Block.box(0.0, 0.0, 13.0, 16.0, 32.0, 16.0)
    private val WEST_AABB: VoxelShape = Block.box(13.0, 0.0, 0.0, 16.0, 32.0, 16.0)
    private val EAST_AABB: VoxelShape = Block.box(0.0, 0.0, 0.0, 3.0, 32.0, 16.0)

    @JvmStatic
    fun renderDimensionalPortal(
        state: BlockState,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val model = when (state.block) {
           is DoorBlock -> getModelFromDirection(state.getValue(DoorBlock.FACING))
           is TrapDoorBlock -> if (state.getValue(TrapDoorBlock.HALF) == Half.TOP) TrapDoorBlock.TOP_AABB else TrapDoorBlock.BOTTOM_AABB
           is DimensionalPortalBlock -> getModelFromDirection(state.getValue(DoorBlock.FACING))
            else -> TrapDoorBlock.TOP_AABB
        }

        renderModelWithPortalShader(model, matrixStack, vertexConsumerProvider, light, overlay)
    }

    fun renderModelWithPortalShader(
        model: VoxelShape,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        matrixStack.pushPose()
        try {
            DimensionalDoorsClient.detector.wrap { type ->
                RenderUtils.renderCube(
                    model,
                    matrixStack,
                    vertexConsumerProvider.getBuffer(type),
                    light,
                    overlay
                )
            }
        } finally {
            matrixStack.popPose()
        }
    }

    fun getModelFromDirection(direction: Direction): VoxelShape {
        return when (direction) {
            Direction.EAST -> EAST_AABB
            Direction.NORTH -> NORTH_AABB
            Direction.WEST -> WEST_AABB
            else -> SOUTH_AABB
        }
    }


    private fun createRenderType(
        name: String,
        format: VertexFormat,
        bufferSize: Int,
        affectsCrumbling: Boolean,
        builderConsumer: RenderType.CompositeState.CompositeStateBuilder.() -> Unit
    ): RenderType {
        val state = RenderType.CompositeState.builder().also(builderConsumer).createCompositeState(false)

        return RenderType.create(
            name,
            format,
            VertexFormat.Mode.QUADS,
            bufferSize,
            affectsCrumbling,
            false,
            state
        )
    }
}
