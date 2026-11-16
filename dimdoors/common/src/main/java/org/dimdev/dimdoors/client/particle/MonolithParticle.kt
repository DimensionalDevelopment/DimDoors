package org.dimdev.dimdoors.client.particle

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.client.render.entity.MonolithRenderer
import org.joml.Quaternionf

class MonolithParticle(world: ClientLevel, x: Double, y: Double, z: Double) : Particle(world, x, y, z) {
    constructor(world: ClientLevel, pos: Vec3): this(world, pos.x, pos.y, pos.z)

    init {
        this.age = 30
    }

    override fun render(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        val delta = (this.age.toFloat() + tickDelta) / this.age.toFloat()
        val matrices = PoseStack()
        matrices.mulPose(camera.rotation())
        matrices.mulPose(Quaternionf().rotateX(Math.toRadians((150.0f * delta - 60.0f).toDouble()).toFloat()))
        matrices.scale(-1.0f, -1.0f, 1.0f)
        matrices.translate(0.0, -1.1009999513626099, 1.5)
        val immediate = Minecraft.getInstance().renderBuffers().bufferSource()
        val vertexConsumer2 = immediate.getBuffer(MonolithRenderer.instance.renderType(DimensionalDoors.id("textures/mob/monolith/solid/monolith_14.png")))
        MonolithRenderer.instance.renderToBuffer(matrices, vertexConsumer2, 0xf000f0, OverlayTexture.NO_OVERLAY, -0x1)
        immediate.endBatch()
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.CUSTOM
    }
}
