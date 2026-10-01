package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import org.dimdev.dimdoors.entity.MonolithEntity
import kotlin.math.sin

class MonolithModel(context: EntityRendererProvider.Context) :
    EntityModel<MonolithEntity>(RenderTypes::getMonolith) {
    private val body: ModelPart = context.bakeLayer(ModEntityModelLayers.MONOLITH)
    private var aggro = 0
    private var id = 0

    override fun renderToBuffer(
        matrixStack: PoseStack,
        consumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val minScaling = 0f
        val maxScaling = 0.001f

        // Use linear interpolation to scale how much jitter we want for our given aggro level
        val aggroScaling = minScaling + (maxScaling - minScaling) * aggro

        // Calculate jitter - include entity ID to give Monoliths individual jitters
        val time = ((Minecraft.getInstance().frameTimeNs + -0xedcba98 * id) % 200000) / 50.0f
        // We use random constants here on purpose just to get different wave forms
        val jitterX = (aggroScaling * sin((1.1f * time).toDouble()) * sin((0.8f * time).toDouble())).toFloat()
        val jitterY = (aggroScaling * sin((1.2f * time).toDouble()) * sin((0.9f * time).toDouble())).toFloat()
        val jitterZ = (aggroScaling * sin((1.3f * time).toDouble()) * sin((0.7f * time).toDouble())).toFloat()

        matrixStack.pushPose()
        matrixStack.translate(jitterX, jitterY, jitterZ)
        this.body.render(matrixStack, consumer, packedLight, packedOverlay)
        matrixStack.popPose()
    }

    override fun setupAnim(
        monolith: MonolithEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.body.yRot = netHeadYaw * 0.017453292f
        this.body.xRot = headPitch * 0.017453292f

        this.aggro = monolith.aggro
        this.id = monolith.id
    }

    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val modelData =
                    MeshDefinition()
                val modelPartData = modelData.getRoot()
                modelPartData.addOrReplaceChild(
                    "body",
                    CubeListBuilder.create().texOffs(1, 0)
                        .addBox(-23.5f, -54f, -6f, 47f, 108f, 12f, false),
                    PartPose.ZERO
                )
                return LayerDefinition.create(modelData, 128, 128)
            }
    }
}
