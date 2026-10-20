package org.dimdev.dimcore.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.util.FastColor
import net.minecraft.world.phys.Vec3

object RenderUtils {
    data class Point(val pos: Vec3, val argb: Int) {

    }

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
            val n = to.subtract(from).normalize()
            lines.addVertex(pose, from.x.toFloat(), from.y.toFloat(), from.z.toFloat()).setColor(color).setNormal(pose, n.x.toFloat(), n.y.toFloat(), n.z.toFloat())
            lines.addVertex(pose, to.x.toFloat(), to.y.toFloat(), to.z.toFloat()).setColor(color).setNormal(pose, n.x.toFloat(), n.y.toFloat(), n.z.toFloat())
//        }
    }

//    fun renderLinked(poseStack: PoseStack, buffers: MultiBufferSource, cam: Vec3, half: Double, points: List<Point>, links: List<Pair<Point, Point>>) {
//        val boxes = buffers.getBuffer(RenderType.debugFilledBox())
//        for ((pos, argb) in points) {
//            val p = pos.subtract(cam)
//            LevelRenderer.addChainedFilledBoxVertices(
//                poseStack, boxes,
//                p.x - half, p.y - half, p.z - half, p.x + half, p.y + half, p.z + half,
//                FastColor.ARGB32.red(argb) / 255f, FastColor.ARGB32.green(argb) / 255f,
//                FastColor.ARGB32.blue(argb) / 255f, FastColor.ARGB32.alpha(argb) / 255f
//            )
//        }
//
//        val lines = buffers.getBuffer(RenderType.lines())
//        val pose = poseStack.last()
//        for ((a, b) in links) {
//            val d = b.pos.subtract(a.pos)
//            val face = Direction.getNearest(d.x, d.y, d.z)
//            val from = a.pos.relative(face, half).subtract(cam)
//            val to = b.pos.relative(face.opposite, half).subtract(cam)
//            val n = to.subtract(from).normalize()
//            lines.addVertex(pose, from.x.toFloat(), from.y.toFloat(), from.z.toFloat()).setColor(a.argb).setNormal(pose, n.x.toFloat(), n.y.toFloat(), n.z.toFloat())
//            lines.addVertex(pose, to.x.toFloat(), to.y.toFloat(), to.z.toFloat()).setColor(b.argb).setNormal(pose, n.x.toFloat(), n.y.toFloat(), n.z.toFloat())
//        }
//    }
}