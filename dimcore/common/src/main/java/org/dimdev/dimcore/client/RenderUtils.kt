package org.dimdev.dimcore.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.util.FastColor
import net.minecraft.world.phys.Vec3

object RenderUtils {

    fun Vec3.drawBox(stack: PoseStack, buffers: MultiBufferSource, half: Double, color: Int) {
        val type = buffers.getBuffer(RenderType.debugFilledBox())
        LevelRenderer.addChainedFilledBoxVertices(
            stack, type,
            x - half, y - half, z - half, x + half, y + half, z + half,
            FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f,
            FastColor.ARGB32.blue(color) / 255f, FastColor.ARGB32.alpha(color) / 255f
        )
    }

    fun drawLine(stack: PoseStack, buffers: MultiBufferSource, half: Double, color: Int, a: Vec3, b: Vec3) {
        val lines = buffers.getBuffer(RenderType.lines())
        val pose = stack.last()
        val d = b.subtract(a)
        val face = Direction.getNearest(d.x, d.y, d.z)
        val from = a.relative(face, half)
        val to = b.relative(face.opposite, half)
        val normal = to.subtract(from).normalize()
        lines.addVertex(pose, from).setColor(color).setNormal(pose, normal)
        lines.addVertex(pose, to).setColor(color).setNormal(pose, normal)
    }

    fun VertexConsumer.addVertex(pose: PoseStack.Pose, pos: Vec3): VertexConsumer = addVertex(pose, pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
    fun VertexConsumer.setNormal(pose: PoseStack.Pose, normal: Vec3): VertexConsumer = setNormal(pose, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
}