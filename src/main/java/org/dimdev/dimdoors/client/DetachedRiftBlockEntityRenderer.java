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
    public static final Identifier tesseract_path = new Identifier("dimdoors:textures/other/tesseract.png");

    private static final Tesseract tesseract = new Tesseract();
    private static final LSystem.PolygonInfo CURVE = LSystem.curves.get(0);
    public static long showRiftCoreUntil = 0;

    public DetachedRiftBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DetachedRiftBlockEntity rift, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcs, int breakProgress, int alpha) {
        if (ModConfig.GRAPHICS.showRiftCore) {
            renderTesseract(vcs.getBuffer(RenderLayer.getTranslucent()), rift, matrices, tickDelta);
        } else {
            long timeLeft = showRiftCoreUntil - System.currentTimeMillis();
            if (timeLeft >= 0) {
                renderTesseract(vcs.getBuffer(RenderLayer.getTranslucent()), rift, matrices, tickDelta);
            }
        }

        renderCrack(vcs.getBuffer(MyRenderLayer.CRACK), matrices, rift);
    }

    private void renderCrack(VertexConsumer vc, MatrixStack matricees, DetachedRiftBlockEntity rift) {
        matricees.push();
        matricees.translate(0.5, 0.5, 0.5);
        RiftCrackRenderer.drawCrack(vc, 0, CURVE, ModConfig.GRAPHICS.riftSize * rift.size, 0xF1234568L * rift.getPos().hashCode());
        matricees.pop();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, MatrixStack matrices, float tickDelta) {
        double radian = nextAngle(rift, tickDelta) * TrigMath.DEG_TO_RAD;
        float[] color = rift.getColor();
        if (color == null) color = new float[]{1, 0.5f, 1, 1};

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);

        tesseract.draw(vc, color, radian);

        matrices.pop();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float partialTicks) {
        rift.renderAngle = (rift.renderAngle + 5 * partialTicks) % 360;
        return rift.renderAngle;
    }
}
