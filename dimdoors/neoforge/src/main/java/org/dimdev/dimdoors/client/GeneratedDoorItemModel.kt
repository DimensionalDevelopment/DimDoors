package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.model.BakedModelWrapper

class GeneratedDoorItemModel
/**
 * @param portalModel a portal model already shaped to match `doorModel`, see
 * [GeneratedDoorModelMappings.portalModelFor]
 */(
    doorModel: BakedModel,
    private val portalModel: BakedModel
) : BakedModelWrapper<BakedModel>(doorModel) {
    override fun applyTransform(
        context: ItemDisplayContext,
        poseStack: PoseStack,
        leftHand: Boolean
    ): BakedModel {
        val transformed = originalModel!!.applyTransform(
            context,
            poseStack,
            leftHand
        )

        if (transformed === originalModel) {
            return this
        }

        return GeneratedDoorItemModel(
            transformed,
            portalModel
        )
    }

    override fun getRenderPasses(
        stack: ItemStack,
        fabulous: Boolean
    ): MutableList<BakedModel> {
        val door = originalModel!!.getRenderPasses(stack, fabulous)
        val portal = portalModel.getRenderPasses(stack, fabulous)

        val models = mutableListOf<BakedModel>()

        // The portal goes first so the door overwrites it wherever the door is opaque.
        models.addAll(portal)
        models.addAll(door)

        return models
    }
}
