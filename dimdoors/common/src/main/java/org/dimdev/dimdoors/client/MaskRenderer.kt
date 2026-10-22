package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.DimensionalDoors.Companion.id
import org.dimdev.dimdoors.entity.mask.MaskEntity
import org.dimdev.dimdoors.entity.mask.MaskType

class MaskRenderer(ctx: EntityRendererProvider.Context) : MobRenderer<MaskEntity, MaskModel>(ctx, MaskModel(ctx), 0.7f) {
    override fun render(
        entity: MaskEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)

        val type = entity.maskType

        poseStack.pusPop {
            poseStack.translate(0.0, entity.eyeHeight.toDouble(), 0.0)
            if (type.isSighted) poseStack.mulPose(Axis.YP.rotationDegrees(type.sightHalfAngle.toFloat() - entityYaw - 90f))
            RenderUtils.renderSolidColorSphere(RenderType.debugQuads(), buffer, poseStack, type.detectionRange.toFloat(), 1f, 1f, 1f, 0.25f, 12, 12, if (type.isSighted) type.sightHalfAngle.toFloat() / 180f else 1f)
        }
    }

    override fun getTextureLocation(entity: MaskEntity): ResourceLocation {
        return TEXTURE[entity.maskType]!!
    }

    companion object {
        private val TEXTURE = MaskType.entries.associateWith { type -> id("textures/mob/mask/${type.serializedName}.png") }
    }
}
