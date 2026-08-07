package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class GeneratedDoorItemModel extends BakedModelWrapper<BakedModel> {
    private final BakedModel portalModel;
    private final boolean trapdoor;

    public GeneratedDoorItemModel(
            BakedModel doorModel,
            BakedModel portalModel,
            boolean trapdoor
    ) {
        super(doorModel);
        this.portalModel = portalModel;
        this.trapdoor = trapdoor;
    }

    @Override
    public @NotNull BakedModel applyTransform(
            @NotNull ItemDisplayContext context,
            @NotNull PoseStack poseStack,
            boolean leftHand
    ) {
        var transformed = originalModel.applyTransform(
                context,
                poseStack,
                leftHand
        );

        if (transformed == originalModel) {
            return this;
        }

        return new GeneratedDoorItemModel(
                transformed,
                portalModel,
                trapdoor
        );
    }

    @Override
    public @NotNull List<BakedModel> getRenderPasses(
            @NotNull ItemStack stack,
            boolean fabulous
    ) {
        var door = originalModel.getRenderPasses(stack, fabulous);

        var portal = trapdoor
                ? List.<BakedModel>of(new TransformedPortalModel(portalModel))
                : portalModel.getRenderPasses(stack, fabulous);

        var models = new ArrayList<BakedModel>(
                door.size() + portal.size()
        );

        models.addAll(portal);
        models.addAll(door);

        return models;
    }

    private static final class TransformedPortalModel
            extends BakedModelWrapper<BakedModel> {

        private TransformedPortalModel(BakedModel model) {
            super(model);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(
                @Nullable BlockState state,
                @Nullable Direction side,
                @NotNull RandomSource random,
                @NotNull ModelData modelData,
                @Nullable RenderType renderType
        ) {

            var transform = new Transformation(
                    new Vector3f(
                            0.0F,  // X
                            0.0F,  // Y
                            0.0F   // Z
                    ),
                    new Quaternionf().rotationXYZ(
                            (float) Math.toRadians(90.0F), // X rotation
                            (float) Math.toRadians(0.0F),  // Y rotation
                            (float) Math.toRadians(0.0F)   // Z rotation
                    ),
                    new Vector3f(
                            1.6F, // X scale
                            1.0F, // Y scale
                            3.0F  // Z scale
                    ),
                    null
            ).blockCenterToCorner();

            return QuadTransformers.applying(transform).process(
                    originalModel.getQuads(
                            state,
                            side,
                            random,
                            modelData,
                            renderType
                    )
            );
        }
    }
}