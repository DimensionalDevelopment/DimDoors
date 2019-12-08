package org.dimdev.dimdoors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer extends BlockEntityRenderer<EntranceRiftBlockEntity> {
    private final Identifier keyPath = new Identifier("dimdoors:textures/other/keyhole.png");
    private final Identifier keyholeLight = new Identifier("dimdoors:textures/other/keyhole_light.png");

    public EntranceRiftBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EntranceRiftBlockEntity entrance, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
//        Direction orientation = entrance.getOrientation();
//        Vec3d offset = new Vec3d(orientation.getOpposite().getDirectionVec()).scale(
//                orientation == Direction.NORTH ||
//                orientation == Direction.WEST ||
//                orientation == Direction.UP ? entrance.pushIn : entrance.pushIn - 1);
//        DimensionalPortalRenderer.renderDimensionalPortal(
//                x + offset.x,
//                y + offset.y,
//                z + offset.z,
//                //entrance.orientation.getHorizontalAngle(),
//                //entrance.orientation.getDirectionVec().getY() * 90,
//                orientation,
//                extendLeft + entrance.extendRight,
//                extendDown + entrance.extendUp,
//                getColors(16));
//
//        if (entrance.lockStatus >= 1) {
//            for (int i = 0; i < 1 + entrance.lockStatus; i++) {
//                renderKeyHole(entrance, x, y, z, i);
//            }
//        }
    }
}
