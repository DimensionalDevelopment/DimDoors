package org.dimdev.dimdoors.client;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;

public class DimensionRenderering {
    private static final Identifier MOON_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_moon.png");
    private static final Identifier SUN_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_sun.png");

    public static void initClient() {
        DimensionRenderingRegistry.CloudRenderer noCloudRenderer = context -> {
        };
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.LIMBO, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.DUNGEON, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PERSONAL, noCloudRenderer);
        DimensionRenderingRegistry.registerCloudRenderer(ModDimensions.PUBLIC, noCloudRenderer);

        DimensionRenderingRegistry.registerSkyRenderer(ModDimensions.LIMBO, context -> renderLimboSky(context.matrixStack()));

        DimensionRenderingRegistry.SkyRenderer pocketRenderer = context -> {
            ClientWorld world = context.world();
            MatrixStack matrices = context.matrixStack();
            List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, world, context.camera().getBlockPos());
            SkyAddon skyAddon = null;
            if (skyAddons.size() > 0) {
                // There should really only be one of these.
                // If anyone needs to use multiple SkyAddons then go ahead and change this.
                skyAddon = skyAddons.get(0);
            }

            if (skyAddon != null) {
                RegistryKey<World> key = skyAddon.getWorld();

                DimensionRenderingRegistry.SkyRenderer skyRenderer = DimensionRenderingRegistry.getSkyRenderer(key);

                if (skyRenderer != null) {
                    skyRenderer.render(context);
                } else {

                    if (key.equals(World.END)) {
                        context.gameRenderer().getClient().worldRenderer.renderEndSky(matrices);
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

    private static void renderLimboSky(MatrixStack matrices) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        float s = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, 100.0F, -s).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, 100.0F, s).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix4f, -s, 100.0F, s).texture(0.0F, 1.0F).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -s, -100.0F, -s).texture(0.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, -100.0F, -s).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f, s, -100.0F, s).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix4f, -s, -100.0F, s).texture(0.0F, 1.0F).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

}
