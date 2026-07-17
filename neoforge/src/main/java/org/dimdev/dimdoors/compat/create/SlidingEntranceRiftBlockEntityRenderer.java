package org.dimdev.dimdoors.compat.create;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.client.DefaultTransformation;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.api.client.Transformer;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.dimdev.dimdoors.client.RiftBlockEntityRenderer;

import java.util.List;

public class SlidingEntranceRiftBlockEntityRenderer extends RiftBlockEntityRenderer<SlidingEntranceRiftBlockEntity> {
    public SlidingEntranceRiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SlidingEntranceRiftBlockEntity blockEntity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        super.render(blockEntity, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);

        BlockState blockState = blockEntity.getBlockState();
        BlockState renderState = blockEntity.getRenderBlockState();

        if (blockEntity.shouldRenderSliding(blockState)) {
            renderSlidingDoor(blockEntity, renderState, tickDelta, matrixStack, vertexConsumerProvider, light, overlay);
        } else {
            renderDoorBlockState(renderState, blockEntity.getLevel().getRandom(), matrixStack, vertexConsumerProvider, light, overlay);
        }

        DimensionalPortalRenderer.renderDimensionalPortal(matrixStack, vertexConsumerProvider, getTransformer(blockEntity), tickDelta, light, overlay, blockEntity.isTall());
    }

    private void renderDoorBlockState(BlockState state, RandomSource random, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        renderBlockState(state, random, matrixStack, vertexConsumerProvider, light, overlay);
        if (state.getBlock() instanceof DoorBlock) {
            matrixStack.pushPose();
            matrixStack.translate(0, 1, 0);
            renderBlockState(state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), random, matrixStack, vertexConsumerProvider, light, overlay);
            matrixStack.popPose();
        }
    }

    private void renderSlidingDoor(SlidingEntranceRiftBlockEntity blockEntity, BlockState renderState, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        Direction facing = renderState.getValue(DoorBlock.FACING);
        Direction movementDirection = facing.getClockWise();

        if (renderState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
            movementDirection = movementDirection.getOpposite();
        }

        float value = blockEntity.getAnimation(partialTicks);
        float value2 = Mth.clamp(value * 10, 0, 1);

        Vec3 offset = Vec3.atLowerCornerOf(movementDirection.getNormal())
                .scale(value * value * 13 / 16f)
                .add(Vec3.atLowerCornerOf(facing.getNormal())
                        .scale(value2 * 1 / 32f));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        if (((SlidingDoorBlock) renderState.getBlock()).isFoldingDoor()) {
            boolean flip = renderState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            for (boolean left : new boolean[] {true, false}) {
                float f = flip ? -1 : 1;
                BakedModel model = getFoldingModel(renderState, left ^ flip);

                matrixStack.pushPose();
                matrixStack.translate(0, -1 / 512f, 0);
                Vec3 facingOffset = Vec3.atLowerCornerOf(facing.getNormal())
                        .scale(value2 * 1 / 32f);
                matrixStack.translate(facingOffset.x, facingOffset.y, facingOffset.z);
                rotateCenteredY(matrixStack, facing.getClockWise().toYRot());

                if (flip) {
                    matrixStack.translate(0, 0, 1);
                }
                matrixStack.mulPose(Axis.YP.rotationDegrees(91 * f * value * value));

                if (!left) {
                    matrixStack.translate(0, 0, f / 2f);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-181 * f * value * value));
                }

                if (flip) {
                    matrixStack.translate(0, 0, -1 / 2f);
                }

                renderModel(model, renderState, blockEntity.getLevel().getRandom(), matrixStack, vertexConsumer, light, overlay);
                matrixStack.popPose();
            }

            return;
        }

        RandomSource random = blockEntity.getLevel().getRandom();
        for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
            matrixStack.pushPose();
            matrixStack.translate(offset.x, offset.y + (half == DoubleBlockHalf.UPPER ? 1 - 1 / 512f : 0), offset.z);
            renderBlockState(renderState.setValue(DoorBlock.OPEN, false)
                    .setValue(DoorBlock.HALF, half), random, matrixStack, buffer, light, overlay);
            matrixStack.popPose();
        }
    }

    private BakedModel getFoldingModel(BlockState renderState, boolean right) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(renderState.getBlock());
        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + (right ? "/fold_right" : "/fold_left")
        );
        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(modelId));
    }

    private void rotateCenteredY(PoseStack matrixStack, float degrees) {
        matrixStack.translate(.5f, .5f, .5f);
        matrixStack.mulPose(Axis.YP.rotationDegrees(degrees));
        matrixStack.translate(-.5f, -.5f, -.5f);
    }

    private Transformer getTransformer(SlidingEntranceRiftBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getBlock() instanceof DimensionalTrapDoorBlock) {
            return blockEntity.getBlockState().getValue(TrapDoorBlock.HALF) == Half.TOP ? DefaultTransformation.TOP_TRAPDOOR : DefaultTransformation.BOTTOMM_TRAPDOOR;
        }

        return DefaultTransformation.fromDirection(blockEntity.getOrientation());
    }

    private void renderBlockState(BlockState renderState, RandomSource random, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(renderState);
        var renderType = ItemBlockRenderTypes.getRenderType(renderState, false);
        var vertexConsumer = vertexConsumerProvider.getBuffer(renderType);

        renderModel(model, renderState, random, matrixStack, vertexConsumer, light, overlay);
    }

    private void renderModel(BakedModel model, BlockState renderState, RandomSource random, PoseStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
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
