package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer extends BlockEntityRenderer<EntranceRiftBlockEntity> {
    private final Identifier keyPath = new Identifier("dimdoors:textures/other/keyhole.png");
    private final Identifier keyholeLight = new Identifier("dimdoors:textures/other/keyhole_light.png");

    public EntranceRiftBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EntranceRiftBlockEntity entrance, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction orientation = entrance.getOrientation();
        Vector3f vec = orientation.getOpposite().getUnitVector();
        vec.scale((float) (orientation == Direction.NORTH || orientation == Direction.WEST || orientation == Direction.UP ? 0.01 : 0.01 - 1));
        Vec3d offset = new Vec3d(vec);
        DimensionalPortalRenderer.renderDimensionalPortal(
                vertexConsumerProvider,
                entrance.getPos().getX() + offset.x,
                entrance.getPos().getY() + offset.y,
                entrance.getPos().getZ() + offset.z,
                //entrance.orientation.getHorizontalAngle(),
                //entrance.orientation.getDirectionVec().getY() * 90,
                orientation,
                16,
                16,
                entrance.getColors(16));
    }
}
