package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.tesseract.Tesseract;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class DetachedRiftBlockEntityRenderer extends RiftBlockEntityRenderer<DetachedRiftBlockEntity> {
    public static final ResourceLocation TESSERACT_PATH = DimensionalDoors.id("textures/other/tesseract.png");
    private static final RGBA DEFAULT_COLOR = new RGBA(1, 0.5f, 1, 1);

    private static final RiftCurves.PolygonInfo CURVE = RiftCurves.CURVES.get(1);

    public DetachedRiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DetachedRiftBlockEntity rift, float tickDelta, PoseStack matrices, MultiBufferSource vcs, int breakProgress, int alpha) {
        super.render(rift, tickDelta, matrices, vcs, breakProgress, alpha);

        if (DimensionalDoors.getConfig().getGraphicsConfig().showRiftCore || RiftBlockEntity.showRiftCoreUntil - System.currentTimeMillis() >= 0)
            this.renderTesseract(vcs.getBuffer(RenderType.entityCutoutNoCull(TESSERACT_PATH)), rift, matrices, tickDelta);
        this.renderCrack(vcs.getBuffer(RenderType.entityCutoutNoCull(TESSERACT_PATH)), matrices, rift);
    }

    private void renderCrack(VertexConsumer vc, PoseStack matrices, DetachedRiftBlockEntity rift) {
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);
        RiftCrackRenderer.drawCrack(matrices.last().pose(), vc, 0, RiftCurves.CURVES.get(rift.getCurveID()), DimensionalDoors.getConfig().getGraphicsConfig().riftSize * rift.size / 150, 0);//0xF1234568L * rift.hashCode());
        matrices.popPose();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, PoseStack matrices, float tickDelta) {
        float radian = (float) (this.nextAngle(rift, tickDelta) * Mth.DEG_TO_RAD);
        RGBA color = rift.getColor();
        if (Objects.equals(color, RGBA.NONE)) {
            color = DEFAULT_COLOR;
        }

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);

        Tesseract.draw(matrices.last().pose(), vc, color, radian);

        matrices.popPose();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float tickDelta) {
        rift.renderAngle = (rift.renderAngle + 5 * tickDelta) % 360;
        return rift.renderAngle;
    }
}
