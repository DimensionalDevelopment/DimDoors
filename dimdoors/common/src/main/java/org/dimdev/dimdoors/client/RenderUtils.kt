package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min

object RenderUtils {
    private val SUPPORTED_VERTEX_ELEMENTS = (VertexFormatElement.POSITION.mask()
            or VertexFormatElement.COLOR.mask()
            or VertexFormatElement.UV0.mask()
            or VertexFormatElement.UV1.mask()
            or VertexFormatElement.UV2.mask()
            or VertexFormatElement.NORMAL.mask())

    fun renderSolidColorSphere(
        renderType: RenderType,
        vc: VertexConsumer,
        matrices: PoseStack,
        radius: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        latitudeSegments: Int,
        longitudeSegments: Int
    ) {
        val mode = renderType.mode()
        if (mode != VertexFormat.Mode.QUADS && mode != VertexFormat.Mode.TRIANGLES) {
            return
        }

        val format = renderType.format()
        if (!format.contains(VertexFormatElement.POSITION) || (format.elementsMask and SUPPORTED_VERTEX_ELEMENTS.inv()) != 0) {
            return
        }

        val pose = matrices.last()
        for (latitude in 0..<latitudeSegments) {
            val theta0 = Mth.PI * latitude / latitudeSegments
            val theta1 = Mth.PI * (latitude + 1) / latitudeSegments
            val v0 = latitude.toFloat() / latitudeSegments
            val v1 = (latitude + 1).toFloat() / latitudeSegments

            for (longitude in 0..<longitudeSegments) {
                val phi0 = Mth.TWO_PI * longitude / longitudeSegments
                val phi1 = Mth.TWO_PI * (longitude + 1) / longitudeSegments
                val u0 = longitude.toFloat() / longitudeSegments
                val u1 = (longitude + 1).toFloat() / longitudeSegments

                if (mode == VertexFormat.Mode.QUADS) {
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi1, u1, v0, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi0, u0, v1, red, green, blue, alpha)
                } else {
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi1, u1, v0, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha)
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi0, u0, v1, red, green, blue, alpha)
                }
            }
        }
    }

    private fun addSolidColorSphereVertex(
        vc: VertexConsumer,
        pose: PoseStack.Pose,
        format: VertexFormat,
        radius: Float,
        theta: Float,
        phi: Float,
        u: Float,
        v: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        val sinTheta = Mth.sin(theta)
        val normalX = sinTheta * Mth.cos(phi)
        val normalY = Mth.cos(theta)
        val normalZ = sinTheta * Mth.sin(phi)
        val next = vc.addVertex(pose, radius * normalX, radius * normalY, radius * normalZ)

        if (format.contains(VertexFormatElement.COLOR)) {
            next.setColor(red, green, blue, alpha)
        }

        if (format.contains(VertexFormatElement.UV0)) {
            next.setUv(u, v)
        }

        if (format.contains(VertexFormatElement.UV1)) {
            next.setOverlay(OverlayTexture.NO_OVERLAY)
        }

        if (format.contains(VertexFormatElement.UV2)) {
            next.setLight(LightTexture.FULL_BRIGHT)
        }

        if (format.contains(VertexFormatElement.NORMAL)) {
            next.setNormal(pose, normalX, normalY, normalZ)
        }
    }

    fun renderTextLines(
        lines: MutableList<Component>,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        font: Font,
        packedLight: Int
    ) {
        if (lines.isEmpty()) return

        val matrix4f = poseStack.last().pose()

        val backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25f)
        val backgroundColor = (backgroundOpacity * 255.0f).toInt() shl 24

        val lineHeight = font.lineHeight
        val startY = -((lines.size - 1) * lineHeight) / 2.0f


        lines.forEachIndexed { lineIndex, line ->

            val textX = (-font.width(line) / 2).toFloat()
            val textY = startY + lineIndex * lineHeight

            font.drawInBatch(
                line,
                textX,
                textY,
                553648127,
                false,
                matrix4f,
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                backgroundColor,
                packedLight
            )

            font.drawInBatch(
                line,
                textX,
                textY,
                -1,
                false,
                matrix4f,
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                packedLight
            )
        }
    }

    fun renderCube(shape: VoxelShape, matrixStack: PoseStack, buffer: VertexConsumer, light: Int, overlay: Int) {
        val consumer: Shapes.DoubleLineConsumer = Shapes.DoubleLineConsumer { minX, minY, minZ, maxX, maxY, maxZ ->
            renderCube(
                minX.toFloat(), minY.toFloat(), minZ.toFloat(),
                maxX.toFloat(), maxY.toFloat(), maxZ.toFloat(),
                matrixStack,
                buffer,
                light,
                overlay
            )
        }

        shape.forAllBoxes(consumer)
    }

    fun renderCube(start: Vec3, end: Vec3, matrixStack: PoseStack, buffer: VertexConsumer, light: Int, overlay: Int) {
        val minX = min(start.x, end.x).toFloat()
        val minY = min(start.y, end.y).toFloat()
        val minZ = min(start.z, end.z).toFloat()

        val maxX = max(start.x, end.x).toFloat()
        val maxY = max(start.y, end.y).toFloat()
        val maxZ = max(start.z, end.z).toFloat()

        renderCube(minX, minY, minZ, maxX, maxY, maxZ, matrixStack, buffer, light, overlay)
    }

    fun renderCube(
        minX: Float,
        minY: Float,
        minZ: Float,
        maxX: Float,
        maxY: Float,
        maxZ: Float,
        matrixStack: PoseStack,
        buffer: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        val pose = matrixStack.last()

        // DOWN
        vertex(pose, buffer, maxX, minY, maxZ, 0f, -1f, 0f, light, overlay)
        vertex(pose, buffer, minX, minY, maxZ, 0f, -1f, 0f, light, overlay)
        vertex(pose, buffer, minX, minY, minZ, 0f, -1f, 0f, light, overlay)
        vertex(pose, buffer, maxX, minY, minZ, 0f, -1f, 0f, light, overlay)

        // UP
        vertex(pose, buffer, maxX, maxY, minZ, 0f, 1f, 0f, light, overlay)
        vertex(pose, buffer, minX, maxY, minZ, 0f, 1f, 0f, light, overlay)
        vertex(pose, buffer, minX, maxY, maxZ, 0f, 1f, 0f, light, overlay)
        vertex(pose, buffer, maxX, maxY, maxZ, 0f, 1f, 0f, light, overlay)

        // WEST
        vertex(pose, buffer, minX, minY, minZ, -1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, minX, minY, maxZ, -1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, minX, maxY, maxZ, -1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, minX, maxY, minZ, -1f, 0f, 0f, light, overlay)

        // NORTH
        vertex(pose, buffer, maxX, minY, minZ, 0f, 0f, -1f, light, overlay)
        vertex(pose, buffer, minX, minY, minZ, 0f, 0f, -1f, light, overlay)
        vertex(pose, buffer, minX, maxY, minZ, 0f, 0f, -1f, light, overlay)
        vertex(pose, buffer, maxX, maxY, minZ, 0f, 0f, -1f, light, overlay)

        // EAST
        vertex(pose, buffer, maxX, minY, maxZ, 1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, maxX, minY, minZ, 1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, maxX, maxY, minZ, 1f, 0f, 0f, light, overlay)
        vertex(pose, buffer, maxX, maxY, maxZ, 1f, 0f, 0f, light, overlay)

        // SOUTH
        vertex(pose, buffer, minX, minY, maxZ, 0f, 0f, 1f, light, overlay)
        vertex(pose, buffer, maxX, minY, maxZ, 0f, 0f, 1f, light, overlay)
        vertex(pose, buffer, maxX, maxY, maxZ, 0f, 0f, 1f, light, overlay)
        vertex(pose, buffer, minX, maxY, maxZ, 0f, 0f, 1f, light, overlay)
    }

    private val normal = Vector3f()
    private val position = Vector3f()

    private fun vertex(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        x: Float,
        y: Float,
        z: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        light: Int,
        overlay: Int
    ) {
        pose.pose().transformPosition(x, y, z, position)
        pose.transformNormal(normalX, normalY, normalZ, normal)

        buffer.addVertex(
            position.x(),
            position.y(),
            position.z(),
            -1,
            0.0f,
            0.0f,
            overlay,
            light,
            normal.x(),
            normal.y(),
            normal.z()
        )
    }
}
