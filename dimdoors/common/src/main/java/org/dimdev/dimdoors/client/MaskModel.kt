package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import org.dimdev.dimcore.client.models.create
import org.dimdev.dimcore.client.models.part
import org.dimdev.dimdoors.entity.mask.MaskEntity


class MaskModel(root: EntityRendererProvider.Context) : HierarchicalModel<MaskEntity>() {
    private val root: ModelPart = root.bakeLayer(ModEntityModelLayers.MASK)

    override fun setupAnim(
        entity: MaskEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.root().allParts.forEach(ModelPart::resetPose)

        this.animate(entity.idleState, MaskAnimations.IDLE, ageInTicks)
        this.animate(entity.spottedState, MaskAnimations.SPOTTED, ageInTicks)
        this.animate(entity.stunnedState, MaskAnimations.STUNNED, ageInTicks)
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
        val texturedModelData: LayerDefinition = create(32, 32) {
            part("spin", 0.0f, 24.0f, -1.6f) {
                part("fall", 0.0f, 1.66f, 0.0f) {
                    part("mask", 0.0f, -5.66f, 0.0f) {
                        box(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 1.0f)
                        part("toprightlimb", 4.0f, -3.0f, 0.0f) {
                            part("toprightlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 0.7418f, 0.0f) {
                                uv(0, 9)
                                box(-1.0f, -0.9f, 0.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }

                        part("middlerightlimb", 4.0f, 0.0f, 0.0f) {
                            part("middlerightlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 0.7418f, 0.0f) {
                                uv(0, 15)
                                box(-1.0f, -1.0f, 0.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }

                        part("bottomrightlimb", 4.0f, 3.0f, 0.0f) {
                            part("bottomrightlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 0.7418f, 0.0f) {
                                uv(0, 21)
                                box(-1.0f, -1.1f, 0.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }

                        part("topleftlimb", -4.0f, -3.0f, 0.0f) {
                            part("topleftlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 2.3998f, 0.0f) {
                                uv(10, 9)
                                box(-1.0f, -0.9f, -4.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }

                        part("middleleftlimb", -4.0f, 0.0f, 0.0f) {
                            part("middleleftlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 2.3998f, 0.0f) {
                                uv(10, 15)
                                box(-1.0f, -1.0f, -4.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }

                        part("bottomleftlimb", -4.0f, 3.0f, 0.0f) {
                            part("bottomleftlimb_r1", 0.0f, 0.0f, 0.0f, 0.0f, 2.3998f, 0.0f) {
                                uv(10, 21)
                                box(-1.0f, -1.1f, -4.0f, 1.0f, 2.0f, 4.0f)
                            }
                        }
                    }
                }
            }
        }
    }
}