package org.dimdev.dimdoors.client

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations

object MaskAnimations {
    private fun animation(time: Float, block: AnimationDefinition.Builder.() -> Unit): AnimationDefinition {
        return AnimationDefinition.Builder.withLength(time).also(block).build()
    }

    fun AnimationDefinition.Builder.addRotation(name: String, vararg frames: Keyframe) {
        this.addAnimation(name, AnimationChannel(AnimationChannel.Targets.ROTATION, *frames))
    }

    fun AnimationDefinition.Builder.addPosition(name: String, vararg frames: Keyframe) {
        this.addAnimation(name, AnimationChannel(AnimationChannel.Targets.POSITION, *frames))
    }

    @JvmField
    val SPOTTED: AnimationDefinition = animation(1.66667f) {
        addRotation("mask",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.25f, 0f, 180f, 0f),
            rotation(0.5f, 0f, 360f, 0f),
            rotation(0.75f, 0f, 360f, 0f)
        )

        addPosition("mask",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, 0f, 2f, 0f),
            position(0.75f, 0f, 2f, 0f)
        )

        addRotation("toprightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, -100f, -10f),
            rotation(0.7917f, 0f, 45f, -10f),
            rotation(0.8333f, 0f, 45f, -5f),
            rotation(0.875f, 0f, 45f, -10f),
            rotation(0.9167f, 0f, 45f, -5f),
            rotation(0.9583f, 0f, 45f, -10f),
            rotation(1.0f, 0f, 45f, -5f),
            rotation(1.0417f, 0f, 45f, -10f),
            rotation(1.0833f, 0f, 45f, -5f),
            rotation(1.125f, 0f, 45f, -10f),
            rotation(1.1667f, 0f, 45f, -5f),
            rotation(1.2083f, 0f, 45f, -10f),
            rotation(1.25f, 0f, 45f, -5f),
            rotation(1.2917f, 0f, 45f, -10f),
            rotation(1.3333f, 0f, 45f, -5f),
            rotation(1.375f, 0f, 45f, -10f),
            rotation(1.4167f, 0f, 45f, -5f),
            rotation(1.4583f, 0f, 45f, -10f),
            rotation(1.5f, 0f, 45f, -5f),
            rotation(1.5417f, 0f, 45f, -10f),
            rotation(1.5833f, 0f, 45f, -5f),
            rotation(1.625f, 0f, 45f, -1f)
        )

        addPosition("middlerightlimb",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, 1f, 0f, 1f),
            position(0.7917f, 0f, 0f, 0f)
        )

        addRotation("middlerightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, -100f, 0f),
            rotation(0.7917f, 0f, 45f, 0f),
            rotation(0.8333f, 0f, 50f, 0f),
            rotation(0.875f, 0f, 45f, 0f),
            rotation(0.9167f, 0f, 50f, 0f),
            rotation(0.9583f, 0f, 45f, 0f),
            rotation(1.0f, 0f, 50f, 0f),
            rotation(1.0417f, 0f, 45f, 0f),
            rotation(1.0833f, 0f, 50f, 0f),
            rotation(1.125f, 0f, 45f, 0f),
            rotation(1.1667f, 0f, 50f, 0f),
            rotation(1.2083f, 0f, 45f, 0f),
            rotation(1.25f, 0f, 50f, 0f),
            rotation(1.2917f, 0f, 45f, 0f),
            rotation(1.3333f, 0f, 50f, 0f),
            rotation(1.375f, 0f, 45f, 0f),
            rotation(1.4167f, 0f, 50f, 0f),
            rotation(1.4583f, 0f, 45f, 0f),
            rotation(1.5f, 0f, 50f, 0f),
            rotation(1.5417f, 0f, 45f, 0f),
            rotation(1.5833f, 0f, 50f, 0f),
            rotation(1.625f, 0f, 45f, 0f)

        )

        addPosition("middlerightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 1f, 0f, 1f),
            rotation(0.7917f, 0f, 0f, 0f)

        )
        addRotation("bottomrightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, -100f, 10f),
            rotation(0.7917f, 0f, 45f, 10f),
            rotation(0.8333f, 0f, 45f, 5f),
            rotation(0.875f, 0f, 45f, 10f),
            rotation(0.9167f, 0f, 45f, 5f),
            rotation(0.9583f, 0f, 45f, 10f),
            rotation(1.0f, 0f, 45f, 5f),
            rotation(1.0417f, 0f, 45f, 10f),
            rotation(1.0833f, 0f, 45f, 5f),
            rotation(1.125f, 0f, 45f, 10f),
            rotation(1.1667f, 0f, 45f, 5f),
            rotation(1.2083f, 0f, 45f, 10f),
            rotation(1.25f, 0f, 45f, 5f),
            rotation(1.2917f, 0f, 45f, 10f),
            rotation(1.3333f, 0f, 45f, 5f),
            rotation(1.375f, 0f, 45f, 10f),
            rotation(1.4167f, 0f, 45f, 5f),
            rotation(1.4583f, 0f, 45f, 10f),
            rotation(1.5f, 0f, 45f, 5f),
            rotation(1.5417f, 0f, 45f, 10f),
            rotation(1.5833f, 0f, 45f, 5f),
            rotation(1.625f, 0f, 45f, 10f)
        )

        addPosition("bottomrightlimb",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, 1f, 0f, 1f),
            position(0.7917f, 0f, 0f, 0f)
        )

        addRotation("topleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, 100f, 10f),
            rotation(0.7917f, 0f, -45f, 10f),
            rotation(0.8333f, 0f, -45f, 5f),
            rotation(0.875f, 0f, -45f, 10f),
            rotation(0.9167f, 0f, -45f, 5f),
            rotation(0.9583f, 0f, -45f, 10f),
            rotation(1.0f, 0f, -45f, 5f),
            rotation(1.0417f, 0f, -45f, 10f),
            rotation(1.0833f, 0f, -45f, 5f),
            rotation(1.125f, 0f, -45f, 10f),
            rotation(1.1667f, 0f, -45f, 5f),
            rotation(1.2083f, 0f, -45f, 10f),
            rotation(1.25f, 0f, -45f, 5f),
            rotation(1.2917f, 0f, -45f, 10f),
            rotation(1.3333f, 0f, -45f, 5f),
            rotation(1.375f, 0f, -45f, 10f),
            rotation(1.4167f, 0f, -45f, 5f),
            rotation(1.4583f, 0f, -45f, 10f),
            rotation(1.5f, 0f, -45f, 5f),
            rotation(1.5417f, 0f, -45f, 10f),
            rotation(1.5833f, 0f, -45f, 5f),
            rotation(1.625f, 0f, -45f, 10f)
        )

        addPosition("topleftlimb",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, -1f, 0f, 1f),
            position(0.7917f, 0f, 0f, 0f)
        )

        addRotation("middleleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, 100f, 0f),
            rotation(0.7917f, 0f, -45f, 0f),
            rotation(0.8333f, 0f, -50f, 0f),
            rotation(0.875f, 0f, -45f, 0f),
            rotation(0.9167f, 0f, -50f, 0f),
            rotation(0.9583f, 0f, -45f, 0f),
            rotation(1.0f, 0f, -50f, 0f),
            rotation(1.0417f, 0f, -45f, 0f),
            rotation(1.0833f, 0f, -50f, 0f),
            rotation(1.125f, 0f, -45f, 0f),
            rotation(1.1667f, 0f, -50f, 0f),
            rotation(1.2083f, 0f, -45f, 0f),
            rotation(1.25f, 0f, -50f, 0f),
            rotation(1.2917f, 0f, -45f, 0f),
            rotation(1.3333f, 0f, -50f, 0f),
            rotation(1.375f, 0f, -45f, 0f),
            rotation(1.4167f, 0f, -50f, 0f),
            rotation(1.4583f, 0f, -45f, 0f),
            rotation(1.5f, 0f, -50f, 0f),
            rotation(1.5417f, 0f, -45f, 0f),
            rotation(1.5833f, 0f, -50f, 0f),
            rotation(1.625f, 0f, -45f, 0f)
        )

        addPosition("middleleftlimb",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, -1f, 0f, 1f),
            position(0.7917f, 0f, 0f, 0f)
        )

        addRotation("bottomleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.5f, 0f, 100f, -10f),
            rotation(0.7917f, 0f, -45f, -10f),
            rotation(0.8333f, 0f, -45f, -5f),
            rotation(0.875f, 0f, -45f, -10f),
            rotation(0.9167f, 0f, -45f, -5f),
            rotation(0.9583f, 0f, -45f, -10f),
            rotation(1.0f, 0f, -45f, -5f),
            rotation(1.0417f, 0f, -45f, -10f),
            rotation(1.0833f, 0f, -45f, -5f),
            rotation(1.125f, 0f, -45f, -10f),
            rotation(1.1667f, 0f, -45f, -5f),
            rotation(1.2083f, 0f, -45f, -10f),
            rotation(1.25f, 0f, -45f, -5f),
            rotation(1.2917f, 0f, -45f, -10f),
            rotation(1.3333f, 0f, -45f, -5f),
            rotation(1.375f, 0f, -45f, -10f),
            rotation(1.4167f, 0f, -45f, -5f),
            rotation(1.4583f, 0f, -45f, -10f),
            rotation(1.5f, 0f, -45f, -5f),
            rotation(1.5417f, 0f, -45f, -10f),
            rotation(1.5833f, 0f, -45f, -5f),
            rotation(1.625f, 0f, -45f, -10f)
        )

        addPosition("bottomleftlimb",
            position(0.0f, 0f, 0f, 0f),
            position(0.5f, -1f, 0f, 1f),
            position(0.7917f, 0f, 0f, 0f)
        )
    }

    @JvmField
    val IDLE = animation(3.5f) {
        looping()
        addPosition("mask",
            rotation(0.0f, 0f, 0f, 0f),
            position(0.5f, 0f, 0.5f, 0f),
            position(1.0f, 0f, 1.5f, 0f),
            position(1.5f, 0f, 2f, 0f),
            position(2.0f, 0f, 2f, 0f),
            position(2.5f, 0f, 1.5f, 0f),
            position(3.0f, 0f, 0.5f, 0f),
            position(3.5f, 0f, 0f, 0f)
        )


        addRotation("toprightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, 7.66f, 7.5f),
            rotation(1.2917f, 0f, -0.16f, 5f),
            rotation(1.5f, 0f, 0f, 0f),
            rotation(1.5417f, 0f, 2.5f, 0f),
            rotation(1.5833f, 0f, 5f, 0f),
            rotation(1.625f, 0f, 10f, 0f),
            rotation(1.6667f, 0f, 15f, 0f),
            rotation(1.7083f, 0f, 17.5f, 0f),
            rotation(1.7917f, 0f, 15f, 0f),
            rotation(1.8333f, 0f, 10f, 0f),
            rotation(1.875f, 0f, 5f, 0f),
            rotation(1.9167f, 0f, 2.5f, 0f),
            rotation(1.9583f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, -7.5f),
            rotation(3.1667f, 2.5f, -0.16f, -7.5f),
            rotation(3.5833f, 0f, 0f, 0f)
        )

        addRotation("middlerightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, 7.66f, 7.5f),
            rotation(1.2917f, 0f, 0f, 5f),
            rotation(1.5417f, 0f, 0f, 0f),
            rotation(1.5833f, 0f, 2.5f, 0f),
            rotation(1.625f, 0f, 5f, 0f),
            rotation(1.6667f, 0f, 10f, 0f),
            rotation(1.7083f, 0f, 15f, 0f),
            rotation(1.75f, 0f, 17.5f, 0f),
            rotation(1.7917f, 0f, 17.5f, 0f),
            rotation(1.8333f, 0f, 15f, 0f),
            rotation(1.875f, 0f, 10f, 0f),
            rotation(1.9167f, 0f, 5f, 0f),
            rotation(1.9583f, 0f, 2.5f, 0f),
            rotation(2.0f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, -7.5f),
            rotation(3.1667f, 2.5f, -0.16f, -7.5f),
            rotation(3.5833f, 0f, 0f, 0f)
        )

        addRotation("bottomrightlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, 7.66f, 7.5f),
            rotation(1.2917f, 0f, -0.16f, 5f),
            rotation(1.625f, 0f, 0f, 0f),
            rotation(1.6667f, 0f, 2.5f, 0f),
            rotation(1.7083f, 0f, 5f, 0f),
            rotation(1.75f, 0f, 10f, 0f),
            rotation(1.7917f, 0f, 15f, 0f),
            rotation(1.8333f, 0f, 17.5f, 0f),
            rotation(1.875f, 0f, 17.5f, 0f),
            rotation(1.9167f, 0f, 15f, 0f),
            rotation(1.9583f, 0f, 10f, 0f),
            rotation(2.0f, 0f, 5f, 0f),
            rotation(2.0417f, 0f, 2.5f, 0f),
            rotation(2.0833f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, -7.5f),
            rotation(3.1667f, 2.5f, -0.16f, -7.5f),
            rotation(3.5833f, 0f, 0f, 0f)
        )

        addRotation("topleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, -7.66f, -7.5f),
            rotation(1.2917f, 0f, -0.16f, -5f),
            rotation(1.5f, 0f, 0f, 0f),
            rotation(1.5417f, 0f, -2.5f, 0f),
            rotation(1.5833f, 0f, -5f, 0f),
            rotation(1.625f, 0f, -10f, 0f),
            rotation(1.6667f, 0f, -15f, 0f),
            rotation(1.7083f, 0f, -17.5f, 0f),
            rotation(1.7917f, 0f, -15f, 0f),
            rotation(1.8333f, 0f, -10f, 0f),
            rotation(1.875f, 0f, -5f, 0f),
            rotation(1.9167f, 0f, -2.5f, 0f),
            rotation(1.9583f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, 7.5f),
            rotation(3.1667f, 2.5f, -0.16f, 7.5f),
            rotation(3.5833f, 0f, 0f, 0f)

        )

        addRotation("middleleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, -7.66f, -7.5f),
            rotation(1.2917f, 0f, -0.16f, -5f),
            rotation(1.5417f, 0f, 0f, 0f),
            rotation(1.5833f, 0f, -2.5f, 0f),
            rotation(1.625f, 0f, -5f, 0f),
            rotation(1.6667f, 0f, -10f, 0f),
            rotation(1.7083f, 0f, -15f, 0f),
            rotation(1.75f, 0f, -17.5f, 0f),
            rotation(1.7917f, 0f, -17.5f, 0f),
            rotation(1.8333f, 0f, -15f, 0f),
            rotation(1.875f, 0f, -10f, 0f),
            rotation(1.9167f, 0f, -5f, 0f),
            rotation(1.9583f, 0f, -2.5f, 0f),
            rotation(2.0f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, 7.5f),
            rotation(3.1667f, 2.5f, -0.16f, 7.5f),
            rotation(3.5833f, 0f, 0f, 0f)
        )

        addRotation("bottomleftlimb",
            rotation(0.0f, 0f, 0f, 0f),
            rotation(0.75f, 0f, -7.66f, -7.5f),
            rotation(1.2917f, 0f, -0.16f, -5f),
            rotation(1.625f, 0f, 0f, 0f),
            rotation(1.6667f, 0f, -2.5f, 0f),
            rotation(1.7083f, 0f, -5f, 0f),
            rotation(1.75f, 0f, -10f, 0f),
            rotation(1.7917f, 0f, -15f, 0f),
            rotation(1.8333f, 0f, -17.5f, 0f),
            rotation(1.875f, 0f, -17.5f, 0f),
            rotation(1.9167f, 0f, -15f, 0f),
            rotation(1.9583f, 0f, -10f, 0f),
            rotation(2.0f, 0f, -5f, 0f),
            rotation(2.0417f, 0f, -2.5f, 0f),
            rotation(2.0833f, 0f, 0f, 0f),
            rotation(2.75f, 2.5f, -0.16f, 7.5f),
            rotation(3.1667f, 2.5f, -0.16f, 7.5f),
            rotation(3.5833f, 0f, 0f, 0f)
        )
    }

    private fun position(timestamp: Float, x: Float, y: Float, z: Float): Keyframe {
        return Keyframe(timestamp, KeyframeAnimations.posVec(x, y, z), AnimationChannel.Interpolations.LINEAR)
    }

    private fun rotation(timestamp: Float, x: Float, y: Float, z: Float): Keyframe {
        return Keyframe(timestamp, KeyframeAnimations.degreeVec(x, y, z), AnimationChannel.Interpolations.LINEAR)
    }
}