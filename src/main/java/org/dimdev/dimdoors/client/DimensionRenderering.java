package org.dimdev.dimdoors.client;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;

public class DimensionRenderering {
    private static final ResourceLocation MOON_RENDER_PATH = DimensionalDoors.resource("textures/other/limbo_moon.png");
    private static final ResourceLocation SUN_RENDER_PATH = DimensionalDoors.resource("textures/other/limbo_sun.png");

    public static void initClient() {
        DimensionRenderingRegistry.CloudRenderer noCloudRenderer = context -> {
        };
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.LIMBO, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.DUNGEON, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PERSONAL, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PUBLIC, noCloudRenderer);

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.LIMBO, context -> renderLimboSky(context.matrixStack()));

        DimensionRenderingRegistry.SkyRenderer pocketRenderer = context -> {
            ClientLevel world = context.world();
            PoseStack matrices = context.matrixStack();
            List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, world, context.camera().getBlockPosition());
            SkyAddon skyAddon = null;
            if (skyAddons.size() > 0) {
                // There should really only be one of these.
                // If anyone needs to use multiple SkyAddons then go ahead and change this.
                skyAddon = skyAddons.get(0);
            }

            if (skyAddon != null) {
                ResourceKey<Level> key = skyAddon.getWorld();

                DimensionRenderingRegistry.SkyRenderer skyRenderer = DimensionRenderingRegistry.getSkyRenderer(key);

                if (skyRenderer != null) {
                    skyRenderer.render(context);
                } else {

                    if (key.equals(Level.END)) {
                        context.gameRenderer().getMinecraft().levelRenderer.renderEndSky(matrices);
                    } else if (key.equals(ModDimensions.LIMBO)) {
                        renderLimboSky(matrices);
                    }
                }
            }
        };

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.DUNGEON, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PERSONAL, pocketRenderer);
        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.PUBLIC, pocketRenderer);
    }

    private static void renderLimboSky(PoseStack matrices) {
        Matrix4f matrix4f = matrices.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        float s = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -s, 100.0F, -s).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, s, 100.0F, -s).uv(1.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, s, 100.0F, s).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, -s, 100.0F, s).uv(0.0F, 1.0F).endVertex();
		tessellator.end();
//        BufferRenderer.draw(bufferBuilder);
        RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -s, -100.0F, -s).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, s, -100.0F, -s).uv(1.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, s, -100.0F, s).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, -s, -100.0F, s).uv(0.0F, 1.0F).endVertex();
		tessellator.end();
//        BufferRenderer.draw(bufferBuilder);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

}
