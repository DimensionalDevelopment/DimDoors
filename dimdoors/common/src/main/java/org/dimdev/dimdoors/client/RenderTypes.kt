package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer.Companion.TESSERACT_PATH
import java.util.function.Supplier

object RenderTypes {

    private val monolithTypes = mutableMapOf<ResourceLocation, RenderType>()

    val chromaticRenderType: RenderType = RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState(RenderStateShard.TextureStateShard(TESSERACT_PATH, false, false)).setOutputState(
        RiftCrackRenderer.ChromaticAberration.outShard).setTransparencyState(
        RenderStateShard.NO_TRANSPARENCY)
        .setCullState(RenderStateShard.NO_CULL)
        .setLightmapState(RenderStateShard.LIGHTMAP)
        .setOverlayState(RenderStateShard.OVERLAY).createCompositeState(true).let { RenderType.create("chromatic", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, it) }

    fun getMonolith(texture: ResourceLocation) = monolithTypes.computeIfAbsent(texture, RenderTypes::createMonolith)

    private fun createMonolith(texture: ResourceLocation): RenderType { val multiPhaseParameters = RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(texture, false, false))
            .setShaderState(RenderStateShard.ShaderStateShard(Supplier { GameRenderer.getRendertypeEntitySolidShader() }))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY) //        .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false)

        return RenderType.create(
            "monolith",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            multiPhaseParameters
        )
    }
}