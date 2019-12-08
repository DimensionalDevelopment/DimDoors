package org.dimdev.dimdoors.client;

import com.flowpowered.math.TrigMath;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.dimdev.util.lsystem.LSystem;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class DetachedRiftBlockEntityRenderer extends BlockEntityRenderer<DetachedRiftBlockEntity> {
    private static final Identifier tesseract_path = new Identifier("dimdoors:textures/other/tesseract.png");

    private static final Tesseract tesseract = new Tesseract();
    private static final LSystem.PolygonInfo CURVE = LSystem.curves.get(0);
    public static long showRiftCoreUntil = 0;

    public DetachedRiftBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DetachedRiftBlockEntity rift, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcs, int breakProgress, int alpha) {
        if (ModConfig.GRAPHICS.showRiftCore) {
            renderTesseract(vcs.getBuffer(RenderLayer.getTranslucent()), rift, tickDelta);
        } else {
            long timeLeft = showRiftCoreUntil - System.currentTimeMillis();
            if (timeLeft >= 0) {
                renderTesseract(vcs.getBuffer(RenderLayer.getTranslucent()), rift, tickDelta);
            }
        }

        renderCrack(vcs.getBuffer(RenderLayer.getTranslucent()), rift);
    }

    private void renderCrack(VertexConsumer vc, DetachedRiftBlockEntity rift) {
        GL11.glPushMatrix();

        // Make the rift render on both sides, disable texture mapping and lighting
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();

        RenderSystem.translated(0.5, 0.5, 0.5);
        RiftCrackRenderer.drawCrack(vc, 0, CURVE, ModConfig.GRAPHICS.riftSize * rift.size, 0xF1234568L * rift.getPos().hashCode());

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableLighting();

        GL11.glPopMatrix();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, float tickDelta) {
        double radian = nextAngle(rift, tickDelta) * TrigMath.DEG_TO_RAD;
        float[] color = rift.getColor();
        if (color == null) color = new float[]{1, 0.5f, 1, 1};

        RenderSystem.disableLighting();
        RenderSystem.pushMatrix();
        RenderSystem.disableCull();

        MinecraftClient.getInstance().getTextureManager().bindTexture(tesseract_path);

        RenderSystem.translated(0.5, 0.5, 0.5);
        RenderSystem.scaled(0.25, 0.25, 0.25);

        tesseract.draw(vc, color, radian);

        RenderSystem.enableCull();
        RenderSystem.popMatrix();
        RenderSystem.enableLighting();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float partialTicks) {
        rift.renderAngle = (rift.renderAngle + 5 * partialTicks) % 360;
        return rift.renderAngle;
    }
}
