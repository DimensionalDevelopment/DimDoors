package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import org.dimdev.dimdoors.api.client.DefaultTransformation;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.api.client.Transformer;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.rift.targets.IdMarker;

import java.util.List;

public class EntranceRiftBlockEntityRenderer extends RiftBlockEntityRenderer<EntranceRiftBlockEntity<?>> {
    public EntranceRiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntranceRiftBlockEntity<?> blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        super.render(blockEntity, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);

        var state = blockEntity.getRenderBlockState();

        DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, getTransformer(blockEntity), tickDelta, light, overlay, blockEntity.isTall());

        renderBlockState(state, blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
        if (state.getBlock() instanceof DoorBlock) {
            matrixStack.pushPose();

            matrixStack.translate(0, 1, 0);
            renderBlockState(state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
            matrixStack.popPose();

        }

    }

    public static Transformer getTransformer(EntranceRiftBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getBlock() instanceof DimensionalTrapDoorBlock) {
            return blockEntity.getBlockState().getValue(TrapDoorBlock.HALF) == Half.TOP ? DefaultTransformation.TOP_TRAPDOOR : DefaultTransformation.BOTTOMM_TRAPDOOR;
        }

        return DefaultTransformation.fromDirection(blockEntity.getOrientation());
    }

    private void renderBlockState(BlockState renderState, RandomSource random, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(renderState);
        var renderType = ItemBlockRenderTypes.getRenderType(renderState, false);
        var vertexConsumer = vertexConsumerProvider.getBuffer(renderType);

        for (var direction : Direction.values()) {
            var quads = model.getQuads(renderState, direction, random);
            renderQuads(matrixStack, vertexConsumer, quads, light, overlay);
        }

        var quads = model.getQuads(renderState, null, random);
        renderQuads(matrixStack, vertexConsumer, quads, light, overlay);
    }

    private void renderQuads(PoseStack stack, VertexConsumer consumer, List<BakedQuad> quads, int light, int overlay) {
        var pose = stack.last();
        for (var quad : quads) {
            consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
        }
    }
}
