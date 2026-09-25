package org.dimdev.dimdoors.api.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.core.Direction
import org.joml.Quaternionf

enum class DefaultTransformation : Transformer {
    DOWN {
        override fun transform(matrices: PoseStack) {}
    },
    UP { override fun transform(matrices: PoseStack) {}
    },
    NORTH_DOOR {
        override fun transform(matrices: PoseStack) = matrices.translate(0f, 0f, 0.81f)
    },
    SOUTH_DOOR {
        override fun transform(matrices: PoseStack) = matrices.translate(0f, 0f, 0.19f)
    },
    WEST_DOOR {
        override fun transform(matrices: PoseStack) {
            matrices.mulPose(Quaternionf().rotateY(Math.toRadians(-90.0).toFloat()))
            matrices.translate(0f, 0f, -0.81f)
        }
    },
    EAST_DOOR {
        override fun transform(matrices: PoseStack) {
            matrices.mulPose(Quaternionf().rotateY(Math.toRadians(-90.0).toFloat()))
            matrices.translate(0f, 0f, -0.19f)
        }
    },
    NONE {
        override fun transform(matrices: PoseStack) {}
    },
    DIMENSIONAL_PORTAL {
        override fun transform(matrices: PoseStack) = matrices.translate(0f, 0f, 0.5f)
    },
    BOTTOMM_TRAPDOOR {
        override fun transform(matrices: PoseStack) = matrices.mulPose(Axis.XP.rotationDegrees(90f))
    },
    TOP_TRAPDOOR {
        override fun transform(matrices: PoseStack) {
            matrices.translate(0f, 0.875f, 0f)
            matrices.mulPose(Axis.XP.rotationDegrees(90f))
        }
    };

    companion object {
        private val VALUES: Array<DefaultTransformation?> = entries.toTypedArray()

        @JvmStatic
        fun fromDirection(direction: Direction): DefaultTransformation? {
            return VALUES[direction.ordinal]
        }
    }
}
