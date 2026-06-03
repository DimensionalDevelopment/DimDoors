package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModShaders {
    private static RenderPipeline DIMENSIONAL_PORTAL = RenderPipeline
            .builder()
            .build();

    public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
        sided.registerCoreShader(DimensionalDoors.id("dimensional_portal"),
                DefaultVertexFormat.POSITION,
                ModShaders::setDimensionalPortal);

        MyRenderLayer

        DIMENSIONAL_PORTAL = dimensionalPortal;
    }

    public static ShaderInstance getDimensionalPortal() {
    return DIMENSIONAL_PORTAL;
    }
}
