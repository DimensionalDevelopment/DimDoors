package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DetachedRiftBlockEntityRenderer extends RiftBlockEntityRenderer<DetachedRiftBlockEntity> {
    public static final ResourceLocation TESSERACT_PATH = DimensionalDoors.id("textures/other/tesseract.png");
    private static final RGBA DEFAULT_COLOR = new RGBA(1, 0.5f, 1, 1);
    private static final float DECAY_RADIUS_ALPHA = 0.18f;
    private static final int DECAY_RADIUS_LATITUDE_SEGMENTS = 12;
    private static final int DECAY_RADIUS_LONGITUDE_SEGMENTS = 24;

    public DetachedRiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull DetachedRiftBlockEntity rift, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vcs, int breakProgress, int alpha) {
        super.render(rift, tickDelta, matrices, vcs, breakProgress, alpha);

        float riftCoreVisibility = DimensionalDoors.getConfig().getGraphicsConfig().showRiftCore ? 1 : RiftUtils.showRiftCoreUntil.getVisibility();
        if (riftCoreVisibility > 0) {
            this.renderTesseract(vcs.getBuffer(RenderType.entityTranslucent(TESSERACT_PATH)), rift, matrices, riftCoreVisibility);
        }

        if (this.shouldRenderDecayRadiusDebug()) {
            RenderType renderType = RenderType.debugStructureQuads();
            this.renderDecayRadius(renderType, vcs.getBuffer(renderType), rift, matrices);
        }

        this.renderCrack(vcs.getBuffer(RenderType.entityCutoutNoCull(TESSERACT_PATH)), matrices, rift);
    }

    private boolean shouldRenderDecayRadiusDebug() {
        var minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL);
    }

    private void renderDecayRadius(RenderType renderType, VertexConsumer vc, DetachedRiftBlockEntity rift, PoseStack matrices) {
        int radius = rift.getDecayRadius();
        if (radius <= 0) {
            return;
        }

        RGBA color = rift.getColor();
        if (Objects.equals(color, RGBA.NONE)) {
            color = DEFAULT_COLOR;
        }

        float alpha = DECAY_RADIUS_ALPHA * color.alpha();

        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);

        RenderUtils.renderSolidColorSphere(renderType, vc, matrices, radius + 1, color.red(), color.green(), color.blue(), alpha, DECAY_RADIUS_LATITUDE_SEGMENTS, DECAY_RADIUS_LONGITUDE_SEGMENTS);

        matrices.popPose();
    }

    private void renderCrack(VertexConsumer vc, PoseStack matrices, DetachedRiftBlockEntity rift) {
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.mulPose(Axis.YP.rotationDegrees(rift.riftYaw));
        RiftCrackRenderer.drawCrack(matrices.last().pose(), vc, 0, RiftCurves.CURVES.get(rift.getCurveID()), DimensionalDoors.getConfig().getGraphicsConfig().riftSize * rift.getData().getSize() / 150, 0);//0xF1234568L * rift.hashCode());
        matrices.popPose();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, PoseStack matrices, float alphaMultiplier) {
        float radian = (DimensionalDoorsClient.INSTANCE.getRenderTick() * 10 % 360) * Mth.DEG_TO_RAD;
        RGBA color = rift.getColor();
        if (Objects.equals(color, RGBA.NONE)) {
            color = DEFAULT_COLOR;
        }
        color = new RGBA(color.red(), color.green(), color.blue(), color.alpha() * alphaMultiplier);

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);

        Tesseract.draw(matrices.last().pose(), vc, color, radian);

        matrices.popPose();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float tickDelta) {
        return 0;

//        rift.renderAngle = (rift.renderAngle + 5 * tickDelta) % 360;
//        return rift.renderAngle;
    }
}
