package org.dimdev.dimdoors.client;

import java.util.Objects;

import com.flowpowered.math.TrigMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.IdMarker;

@Environment(Dist.CLIENT)
public class DetachedRiftBlockEntityRenderer implements BlockEntityRenderer<DetachedRiftBlockEntity> {
    public static final ResourceLocation TESSERACT_PATH = DimensionalDoors.resource("textures/other/tesseract.png");
    private static final RGBA DEFAULT_COLOR = new RGBA(1, 0.5f, 1, 1);

    private static final Tesseract TESSERACT = new Tesseract();
    private static final RiftCurves.PolygonInfo CURVE = RiftCurves.CURVES.get(0);

    @Override
    public void render(DetachedRiftBlockEntity rift, float tickDelta, PoseStack matrices, MultiBufferSource vcs, int breakProgress, int alpha) {
		if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL) && rift.getData().getDestination() instanceof IdMarker idMarker) {
			matrices.pushPose();
			matrices.translate(0.5, 0.5, 0.5);

			Minecraft.getInstance().font.drawInBatch(Component.nullToEmpty(String.valueOf(idMarker.getId())), 0f,0f, 0xffffffff, false, matrices.last().pose(), vcs, true, 0x000000, LightTexture.FULL_BRIGHT);

			matrices.popPose();
		}

    	if (Constants.CONFIG_MANAGER.get().getGraphicsConfig().showRiftCore) {
            this.renderTesseract(vcs.getBuffer(MyRenderLayer.TESSERACT), rift, matrices, tickDelta);
        } else {
            long timeLeft = RiftBlockEntity.showRiftCoreUntil - System.currentTimeMillis();
            if (timeLeft >= 0) {
                this.renderTesseract(vcs.getBuffer(MyRenderLayer.TESSERACT), rift, matrices, tickDelta);
            }
        }

        this.renderCrack(vcs.getBuffer(MyRenderLayer.CRACK), matrices, rift);
    }

    private void renderCrack(VertexConsumer vc, PoseStack matrices, DetachedRiftBlockEntity rift) {
        matrices.pushPose();
        matrices.translate(0.5, 0.5, 0.5);
        RiftCrackRenderer.drawCrack(matrices.last().pose(), vc, 0, CURVE, Constants.CONFIG_MANAGER.get().getGraphicsConfig().riftSize * rift.size / 150, 0);//0xF1234568L * rift.hashCode());
        matrices.popPose();
    }

    private void renderTesseract(VertexConsumer vc, DetachedRiftBlockEntity rift, PoseStack matrices, float tickDelta) {
        double radian = this.nextAngle(rift, tickDelta) * TrigMath.DEG_TO_RAD;
        RGBA color = rift.getColor();
        if (Objects.equals(color, RGBA.NONE)) {
            color = DEFAULT_COLOR;
        }

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);

        TESSERACT.draw(matrices.last().pose(), vc, color, radian);

        matrices.popPose();
    }

    private double nextAngle(DetachedRiftBlockEntity rift, float tickDelta) {
        rift.renderAngle = (rift.renderAngle + 5 * tickDelta) % 360;
        return rift.renderAngle;
    }
}
