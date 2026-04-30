package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.joml.Matrix4f;

import java.util.List;

public abstract class RiftBlockEntityRenderer<T extends RiftBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;

    public RiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(T rift, float f, PoseStack matrices, MultiBufferSource multiBufferSource, int i, int j) {
        var minecraft = Minecraft.getInstance();

        if(minecraft.player != null && minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.RIFT_CONFIGURATION_TOOL)) {
            matrices.pushPose();

            matrices.translate(0.5D, 1.25D, 0.5D);
            matrices.mulPose(minecraft.getBlockEntityRenderDispatcher().camera.rotation());
            matrices.scale(0.025F, -0.025F, 0.025F);

            renderTextLines(List.of(Component.literal("Closing: " + rift.closing), Component.literal("Size: " + rift.size)), matrices, multiBufferSource, LightTexture.FULL_BRIGHT);

            matrices.popPose();
        }
    }

    protected void renderTextLines(
            List<Component> lines,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        Font font = context.getFont();
        Matrix4f matrix4f = poseStack.last().pose();

        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;

        int lineHeight = font.lineHeight;
        float startY = -((lines.size() - 1) * lineHeight) / 2.0F;

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            Component line = lines.get(lineIndex);
            if (line == null) {
                continue;
            }

            float textX = (float)(-font.width(line) / 2);
            float textY = startY + lineIndex * lineHeight;

            font.drawInBatch(
                    line,
                    textX,
                    textY,
                    553648127,
                    false,
                    matrix4f,
                    buffer,
                    Font.DisplayMode.SEE_THROUGH,
                    backgroundColor,
                    packedLight
            );

            font.drawInBatch(
                    line,
                    textX,
                    textY,
                    -1,
                    false,
                    matrix4f,
                    buffer,
                    Font.DisplayMode.NORMAL,
                    0,
                    packedLight
            );
        }
    }

}
