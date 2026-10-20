package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import org.dimdev.dimdoors.entity.mask.MaskEntity

class MaskModel(root: EntityRendererProvider.Context) : HierarchicalModel<MaskEntity?>() {
    private val root: ModelPart

    init {
        this.root = root.bakeLayer(ModEntityModelLayers.MASK)
    }

    override fun setupAnim(
        entity: MaskEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.root().getAllParts().forEach { obj: ModelPart? -> obj!!.resetPose() }

        this.animate(entity.idleState, MaskAnimations.IDLE, ageInTicks)
        this.animate(entity.spottedState, MaskAnimations.SPOTTED, ageInTicks)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    override fun root(): ModelPart {
        return root
    }

    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val meshdefinition =
                    MeshDefinition()
                val partdefinition = meshdefinition.getRoot()

                val mask = partdefinition.addOrReplaceChild(
                    "mask",
                    CubeListBuilder.create().texOffs(0, 0).addBox(
                        -4.0f,
                        -8.0f,
                        0.0f,
                        8.0f,
                        8.0f,
                        1.0f,
                        CubeDeformation(0.0f)
                    ),
                    PartPose.offset(0.0f, 24.0f, -1.6f)
                )

                mask.addOrReplaceChild(
                    "toprightlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(4.0f, -7.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "toprightlimb_r1",
                        CubeListBuilder.create().texOffs(0, 9).addBox(
                            -1.0f,
                            -0.9f,
                            0.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.7418f,
                            0.0f
                        )
                    )

                mask.addOrReplaceChild(
                    "middlerightlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(4.0f, -4.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "middlerightlimb_r1",
                        CubeListBuilder.create().texOffs(0, 15).addBox(
                            -1.0f,
                            -1.0f,
                            0.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.7418f,
                            0.0f
                        )
                    )

                mask.addOrReplaceChild(
                    "bottomrightlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(4.0f, -1.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "bottomrightlimb_r1",
                        CubeListBuilder.create().texOffs(0, 21).addBox(
                            -1.0f,
                            -1.1f,
                            0.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.7418f,
                            0.0f
                        )
                    )

                mask.addOrReplaceChild(
                    "topleftlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(-4.0f, -7.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "Topleftlimb_r1",
                        CubeListBuilder.create().texOffs(10, 9).addBox(
                            -1.0f,
                            -0.9f,
                            -4.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            2.3998f,
                            0.0f
                        )
                    )

                mask.addOrReplaceChild(
                    "middleleftlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(-4.0f, -4.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "middleleftlimb_r1",
                        CubeListBuilder.create().texOffs(10, 15).addBox(
                            -1.0f,
                            -1.0f,
                            -4.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            2.3998f,
                            0.0f
                        )
                    )

                mask.addOrReplaceChild(
                    "bottomleftlimb",
                    CubeListBuilder.create(),
                    PartPose.offset(-4.0f, -1.0f, 0.0f)
                )
                    .addOrReplaceChild(
                        "bottomleftlimb_r1",
                        CubeListBuilder.create().texOffs(10, 21).addBox(
                            -1.0f,
                            -1.1f,
                            -4.0f,
                            1.0f,
                            2.0f,
                            4.0f,
                            CubeDeformation(0.0f)
                        ),
                        PartPose.offsetAndRotation(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            2.3998f,
                            0.0f
                        )
                    )

                return LayerDefinition.create(meshdefinition, 32, 32)
            }
    }
}