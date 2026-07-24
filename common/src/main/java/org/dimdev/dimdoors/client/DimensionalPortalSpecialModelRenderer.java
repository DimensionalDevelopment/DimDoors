package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry;

public final class DimensionalPortalSpecialModelRenderer {
    public static final ResourceLocation RENDERER_ID = DimensionalDoors.id("dimensional_portal");
    public static final ResourceLocation SHADER_ID = DimensionalDoors.id("dimensional_portal_special_model");
    private static boolean registered;

    public static synchronized void register() {
        if (registered) {
            return;
        }

        SpecialModelShaderRegistry.register(RENDERER_ID, SHADER_ID, DefaultVertexFormat.BLOCK, DimensionalPortalSpecialModelRenderer::setupShader);
        registered = true;
    }

    private static void setupShader(ShaderInstance shader) {
        var textureManager = Minecraft.getInstance().getTextureManager();
        shader.setSampler("Sampler0", textureManager.getTexture(TheEndPortalRenderer.END_SKY_LOCATION));
        shader.setSampler("Sampler1", textureManager.getTexture(DimensionalPortalRenderer.WARP_PATH));
    }

    private DimensionalPortalSpecialModelRenderer() {
    }
}
