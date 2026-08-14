package org.dimdev.dimdoors.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.RenderType;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.ShaderPackDetector;

import java.util.function.Consumer;

import static org.dimdev.dimdoors.client.MyRenderLayer.WARP_PATH;

/*
 * Iris compat for dimensional portal rendering by feeding a entity solid RenderType with the warp.png's path when shaders are active and use the normal one when not.
 */
public class IrisCompat implements ShaderPackDetector {
    @Override
    public void wrap(Consumer<RenderType> type) {
        if(IrisApi.getInstance().isShaderPackInUse()) {
            CapturedRenderingState state = CapturedRenderingState.INSTANCE;
            int previous = state.getCurrentRenderedBlockEntity();
            var id = WorldRenderingSettings.INSTANCE.getBlockStateIds().getOrDefault(ModBlocks.DIMENSIONAL_PORTAL.defaultBlockState(), -1);


            try {
                state.setCurrentBlockEntity(id);
                type.accept(RenderType.entitySolid(WARP_PATH));
            } finally {
                state.setCurrentBlockEntity(previous);
            }

        } else {
            type.accept(DimensionalPortalRenderer.RENDER_LAYER);
        }
    }
}
