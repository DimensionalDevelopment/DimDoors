package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f

interface CloudRenderBuffer {
    fun renderCloudBuffer(
        poseStack: PoseStack,
        modelViewMatrix: Matrix4f,
        projectionMatrix: Matrix4f,
        partialTick: Float,
        ticks: Int,
        camX: Double,
        camY: Double,
        camZ: Double,
        cloudHeight: Float,
        cloudColor: Vec3
    )
}
