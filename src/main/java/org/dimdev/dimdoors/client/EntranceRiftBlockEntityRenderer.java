package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer extends BlockEntityRenderer<EntranceRiftBlockEntity> {
    private static final Identifier KEY_PATH = new Identifier("dimdoors:textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = new Identifier("dimdoors:textures/other/keyhole_light.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> LAYERS = IntStream.range(0, 16).mapToObj((i) -> {
        return MyRenderLayer.getDimensionalPortal(i + 1);
    }).collect(ImmutableList.toImmutableList());

    public EntranceRiftBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EntranceRiftBlockEntity entrance, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.DIRT), ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, light, overlay, matrices, vertexConsumers);
        if (MinecraftClient.getInstance().world == null
                || !MinecraftClient.getInstance().world.isChunkLoaded(entrance.getPos().getX() >> 4, entrance.getPos().getZ() >> 4)) {
            return;
        }
        Direction orientation = entrance.getOrientation();
        Vector3f vec = orientation.getOpposite().getUnitVector();
        this.renderVertices(entrance, matrices, vertexConsumers, orientation, vec);

//        Vec3d offset = new Vec3d(vec);
//        DimensionalPortalRenderer.renderDimensionalPortal(
//                entrance.getPos().getX() + offset.x,
//                entrance.getPos().getY() + offset.y,
//                entrance.getPos().getZ() + offset.z,
//                //entrance.orientation.getHorizontalAngle(),
//                //entrance.orientation.getDirectionVec().getY() * 90,
//                orientation,
//                16,
//                16,
//                entrance.getColors(16),
//                matrices,
//                vertexConsumers.getBuffer(LAYERS.get(0)));
        matrices.pop();
    }

    private void renderVertices(EntranceRiftBlockEntity entrance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Direction orientation, Vector3f vec) {
        vec.scale((float) (orientation == Direction.NORTH || orientation == Direction.WEST || orientation == Direction.UP ? 0.01 : 0.01 - 1));
        double d = entrance.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int k = this.getOffset(d);
        float g = 0.75F;
        Matrix4f matrix4f = matrices.peek().getModel();
        this.drawAllVertices(entrance, g, 0.15F, matrix4f, vertexConsumers.getBuffer(LAYERS.get(0)), orientation);
        matrices.scale(0.99F, 0.99F, 0.99F);

        for(int l = 1; l < k; ++l) {
            this.drawAllVertices(entrance, g, 2.0F / (float)(18 - l), matrix4f, vertexConsumers.getBuffer(LAYERS.get(l)), orientation);
        }
    }

    protected int getOffset(double d) {
        if (d > 36864.0D) {
            return 1;
        } else if (d > 25600.0D) {
            return 3;
        } else if (d > 16384.0D) {
            return 5;
        } else if (d > 9216.0D) {
            return 7;
        } else if (d > 4096.0D) {
            return 9;
        } else if (d > 1024.0D) {
            return 11;
        } else if (d > 576.0D) {
            return 13;
        } else {
            return d > 256.0D ? 14 : 15;
        }
    }

    private void drawAllVertices(EntranceRiftBlockEntity blockEntity, float u, float v, Matrix4f matrix4f, VertexConsumer vertexConsumer, Direction dir) {
        float red = (RANDOM.nextFloat() * 0.5F + 0.1F) * v;
        float green = (RANDOM.nextFloat() * 0.5F + 0.4F) * v;
        float blue = (RANDOM.nextFloat() * 0.5F + 0.5F) * v;
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, red, green, blue, Direction.SOUTH);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, red, green, blue, Direction.NORTH);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, red, green, blue, Direction.EAST);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, red, green, blue, Direction.WEST);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, red, green, blue, Direction.DOWN);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, red, green, blue, Direction.UP);
    }

    private void drawVertices(EntranceRiftBlockEntity endPortalBlockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, float red, float green, float blue, Direction direction) {
        RenderSystem.clearTexGen();
//        vertexConsumer.vertex(matrix4f, x1, y1, z1).color(red, green, blue, 1.0F).next();
//        vertexConsumer.vertex(matrix4f, x2, y1, z2).color(red, green, blue, 1.0F).next();
//        vertexConsumer.vertex(matrix4f, x2, y2, z3).color(red, green, blue, 1.0F).next();
//        vertexConsumer.vertex(matrix4f, x1, y2, z4).color(red, green, blue, 1.0F).next();
        if (direction == endPortalBlockEntity.getOrientation() || direction.getOpposite() == endPortalBlockEntity.getOrientation()) {
            vertexConsumer.vertex(matrix4f, x1, y1, z1 + 0.5F).color(red, green, blue, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x2, y1, z2 + 0.5F).color(red, green, blue, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x2, y2, z3 + 0.5F).color(red, green, blue, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x1, y2, z4 + 0.5F).color(red, green, blue, 1.0F).next();
        }
    }
}
