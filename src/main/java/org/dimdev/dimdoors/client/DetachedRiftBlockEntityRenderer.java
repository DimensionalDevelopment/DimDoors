package org.dimdev.dimdoors.client;

import java.util.Objects;

import com.flowpowered.math.TrigMath;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.dimdev.dimdoors.api.util.RGBA;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DetachedRiftBlockEntityRenderer implements BlockEntityRenderer<DetachedRiftBlockEntity> {
    public static final Identifier TESSERACT_PATH = new Identifier("dimdoors:textures/other/tesseract.png");
    private static final RGBA DEFAULT_COLOR = new RGBA(1, 0.5f, 1, 1);

    private static final Tesseract TESSERACT = new Tesseract();
    private static final RiftCurves.PolygonInfo CURVE = RiftCurves.CURVES.get(0);

    @Override
    public void render(DetachedRiftBlockEntity rift, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcs, int breakProgress, int alpha) {
    	if (DimensionalDoorsInitializer.getConfig().getGraphicsConfig().showRiftCore) {
            this.renderTesseract(vcs.getBuffer(MyRenderLayer.TESSERACT), rift, matrices, tickDelta);
        } else {
            long timeLeft = RiftBlockEntity.showRiftCoreUntil - System.currentTimeMillis();
            if (timeLeft >= 0) {
                this.renderTesseract(vcs.getBuffer(MyRenderLayer.TESSERACT), rift, matrices, tickDelta);
            }
        }

        this.renderCrack(vcs.getBuffer(MyRenderLayer.CRACK), matrices, rift);
    }

    private void renderCrack(VertexConsumer vc, MatrixStack matrices, DetachedRiftBlockEntity rift) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        RiftCrackRenderer.drawCrack(matrices.peek().getPositionMatrix(), vc, 0, CURVE, DimensionalDoorsInitializer.getConfig().getGraphicsConfig().riftSize * rift.size / 150, 0);//0xF1234568L * rift.hashCode());
        matrices.pop();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, MatrixStack matrices, float tickDelta) {
        double radian = this.nextAngle(rift, tickDelta) * TrigMath.DEG_TO_RAD;
        RGBA color = rift.getColor();
        if (Objects.equals(color, RGBA.NONE)) {
            color = DEFAULT_COLOR;
        }

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);

        TESSERACT.draw(matrices.peek().getPositionMatrix(), vc, color, radian);

        matrices.pop();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float tickDelta) {
        rift.renderAngle = (rift.renderAngle + 5 * tickDelta) % 360;
        return rift.renderAngle;
    }
}
