package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.block.DoorBlock;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity;

import static org.dimdev.dimdoors.client.EntranceRiftBlockEntityRenderer.getTransformer;

public class DialingDoorBlockEntityRenderer implements BlockEntityRenderer<DialingDoorBlockEntity> {
    public static final float VOXEL_SIZE = 1.0F / 16.0F;
    private static float HEIGHT_TEXT_SCALE = VOXEL_SIZE * 4.6f/7f;
    public static float WIDTH_TEXT_SCALE = VOXEL_SIZE * 0.6f;
    private static final double TEXT_FORWARD_OFFSET = 0.501D + VOXEL_SIZE;

    private final BlockEntityRendererProvider.Context context;

    public DialingDoorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(DialingDoorBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        DimensionalPortalRenderer.renderDimensionalPortal(blockEntity.getBlockState(), matrixStack, vertexConsumerProvider, light, overlay);

        if(blockEntity.getBlockState().getValue(DoorBlock.OPEN)) return;
        renderDialingText(blockEntity, matrixStack, vertexConsumerProvider, light);
    }

    private void renderDialingText(DialingDoorBlockEntity blockEntity, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
        String text = "0";
        Font font = context.getFont();
        Direction front = blockEntity.getOrientation().getOpposite();
        matrixStack.pushPose();

        matrixStack.translate(
                0.5D + front.getStepX() * TEXT_FORWARD_OFFSET,
                1,
                0.5D + front.getStepZ() * TEXT_FORWARD_OFFSET
        );

        matrixStack.mulPose(Axis.YP.rotationDegrees(-front.toYRot()));


        var address = blockEntity.getAddress();

        matrixStack.pushPose();
        matrixStack.translate(-1.5 * VOXEL_SIZE, 10.1 * VOXEL_SIZE, 0);
        renderText(String.valueOf(address.dial1()), matrixStack, vertexConsumerProvider, light, font);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(-1.5 * VOXEL_SIZE, 3.7 * VOXEL_SIZE, 0);
        renderText(String.valueOf(address.dial2()), matrixStack, vertexConsumerProvider, light, font);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(-1.5 * VOXEL_SIZE, -3.3 * VOXEL_SIZE, 0);
        renderText(String.valueOf(address.dial3()), matrixStack, vertexConsumerProvider, light, font);
        matrixStack.popPose();

        matrixStack.popPose();
    }

    private void renderText(String text, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, Font font) {
        matrixStack.scale(WIDTH_TEXT_SCALE, -HEIGHT_TEXT_SCALE, 1);

        font.drawInBatch(
                text,
                0,
                0,
                0x000000,
                false,
                matrixStack.last().pose(),
                vertexConsumerProvider,
                Font.DisplayMode.NORMAL,
                0x00000000,
                light
        );
    }
}
