package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class GeneratedDoorItemModel extends BakedModelWrapper<BakedModel> {
    private final BakedModel portalModel;

    /**
     * @param portalModel a portal model already shaped to match {@code doorModel}, see
     *                    {@link GeneratedDoorModelMappings#portalModelFor}
     */
    public GeneratedDoorItemModel(
            BakedModel doorModel,
            BakedModel portalModel
    ) {
        super(doorModel);
        this.portalModel = portalModel;
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
                portalModel
        );
    }

    @Override
    public @NotNull List<BakedModel> getRenderPasses(
            @NotNull ItemStack stack,
            boolean fabulous
    ) {
        var door = originalModel.getRenderPasses(stack, fabulous);
        var portal = portalModel.getRenderPasses(stack, fabulous);

        var models = new ArrayList<BakedModel>(
                door.size() + portal.size()
        );

        // The portal goes first so the door overwrites it wherever the door is opaque.
        models.addAll(portal);
        models.addAll(door);

        return models;
    }
}
