package org.dimdev.dimdoors.compat.iris

import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings
import net.irisshaders.iris.uniforms.CapturedRenderingState
import net.minecraft.client.renderer.RenderType
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.client.ShaderPackDetector

/*
 * Iris compat for dimensional portal rendering by feeding a entity solid RenderType with the warp.png's path when shaders are active and use the normal one when not.
 */
class IrisCompat : ShaderPackDetector {
    override fun wrap(type: (RenderType) -> Unit) {
        if (IrisApi.getInstance().isShaderPackInUse) {
            val state = CapturedRenderingState.INSTANCE
            val previous = state.currentRenderedBlockEntity
            val id = WorldRenderingSettings.INSTANCE.blockStateIds?.let { it[ModBlocks.DIMENSIONAL_PORTAL.value().defaultBlockState()] } ?: -1


            try {
                state.setCurrentBlockEntity(id)
                type.invoke(DimensionalPortalRenderer.IRIS_DIMENSIONAL_PORTAL_RENDER_LAYER)
            } finally {
                state.setCurrentBlockEntity(previous)
            }
        } else {
            type.invoke(DimensionalPortalRenderer.VANILLA_DIMENSIONAL_PORTAL_RENDER_LAYER)
        }
    }
}
