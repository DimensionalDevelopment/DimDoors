package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.Util
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.util.Mth
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.id
import org.dimdev.dimdoors.client.RiftCurves.PolygonInfo
import org.dimdev.dimdoors.client.render.node.NodeProgramKind
import org.dimdev.dimdoors.client.render.node.NodeStage
import org.dimdev.dimdoors.client.render.node.NodeUniforms
import org.dimdev.dimdoors.client.render.node.RenderNode
import org.joml.Matrix4f
import org.joml.Vector2f
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object RiftCrackRenderer {

    fun drawCrack(
        model: Matrix4f,
        vc: VertexConsumer,
        riftRotation: Float,
        poly: PolygonInfo,
        size: Double,
        riftRandom: Long
    ) {
        // Calculate the proper size for the rift render
        val scale = size / (poly.maxX - poly.minX)

        // Calculate the midpoint of the fractal bounding box
        val offsetX = (poly.maxX + poly.minX) / 2.0
        val offsetY = (poly.maxY + poly.minY) / 2.0
        val offsetZ = 0.0

        // Changes how far the triangles move
        // TODO: Actually seems to control the glow around the rift
        val motionMagnitude = 0.6f

        // Changes how quickly the triangles move
        val motionSpeed = 0.0f

        // Number of individual jitter waveforms to generate
        // changes how "together" the overall motions are
        val jCount = 10

        val time = ((Util.getEpochMillis() + riftRandom) % 2000000) * motionSpeed
        val jitters = DoubleArray(jCount)

        val jitterScale = config.graphicsConfig.riftJitter * size * size * size / 2000f
        // We use random constants here on purpose just to get different wave forms
        val xJitter = jitterScale * sin(1.1f * time * size) * sin((0.8f * time).toDouble())
        val yJitter = jitterScale * sin(1.2f * time * size) * sin((0.9f * time).toDouble())
        val zJitter = jitterScale * sin(1.3f * time * size) * sin((0.7f * time).toDouble())

        // generate a series of waveforms
        run {
            var i = 0
            while (i < jCount) {
                jitters[i] =
                    sin(((1f + i / 10f) * time).toDouble()) * cos((1f - i / 10f * time).toDouble()) * motionMagnitude
                i += 1
            }
        }

        // Draw the rift
        val points = poly.points
        var i = 0
        val pointsSize = points.size
        while (i < pointsSize) {
            val p = points[i]

            RiftCrackRenderer.renderPoint(
                vc,
                model,
                points[i + 0]!!,
                jCount,
                offsetX,
                offsetY,
                offsetZ,
                xJitter,
                yJitter,
                zJitter,
                scale,
                riftRotation.toDouble(),
                jitters,
                false
            )
            RiftCrackRenderer.renderPoint(
                vc,
                model,
                points.get(i + 1)!!,
                jCount,
                offsetX,
                offsetY,
                offsetZ,
                xJitter,
                yJitter,
                zJitter,
                scale,
                riftRotation.toDouble(),
                jitters,
                false
            )
            RiftCrackRenderer.renderPoint(
                vc,
                model,
                points[i + 2]!!,
                jCount,
                offsetX,
                offsetY,
                offsetZ,
                xJitter,
                yJitter,
                zJitter,
                scale,
                riftRotation.toDouble(),
                jitters,
                true
            )
            i += 3
        }
    }

    fun renderPoint(
        vc: VertexConsumer,
        model: Matrix4f,
        p: RiftCurves.Point,
        jCount: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        xJitter: Double,
        yJitter: Double,
        zJitter: Double,
        scale: Double,
        riftRotation: Double,
        jitters: DoubleArray,
        twice: Boolean
    ) {
        // Reduces most overlap between triangles inside the rift's center
        val jIndex = abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y)

        var x =
            (p.x + jitters[(jIndex + 1) % jCount] - offsetX) * cos(Math.toRadians(riftRotation)) - jitters[(jIndex + 2) % jCount] * sin(
                Math.toRadians(riftRotation)
            )
        var y = p.y + jitters[jIndex % jCount] - offsetY
        var z =
            (p.x + jitters[(jIndex + 2) % jCount] - offsetZ) * sin(Math.toRadians(riftRotation)) + jitters[(jIndex + 2) % jCount] * cos(
                Math.toRadians(riftRotation)
            )

        // Scale the rift
        x *= scale
        y *= scale
        z *= scale

        vc.addVertex(model, (x + xJitter).toFloat(), (y + yJitter).toFloat(), (z + zJitter).toFloat())
            .setColor(0.08f, 0.08f, 0.08f, 1f)
            .setUv(0f, 0f)
            .setOverlay(0)
            .setLight(0)
            .setNormal(0f, 0f, 0f)

        if (twice) {
            vc.addVertex(model, (x + xJitter).toFloat(), (y + yJitter).toFloat(), (z + zJitter).toFloat())
                .setColor(0.08f, 0.08f, 0.08f, 1f)
                .setUv(0f, 0f)
                .setOverlay(0)
                .setLight(0)
                .setNormal(0f, 0f, 0f)
        }
    }

    object ChromaticAberration : RenderNode("chromatic".id(), NodeStage.TRANSLUCENT, 1536) {
        val oscilationDuration = 2f
        val chromaticSeperation = 8f
        val orbitDuration = 4f

        override val renderType: RenderType get() = RenderTypes.chromaticRenderType

        override fun shader(kind: NodeProgramKind): ShaderInstance = when (kind) {
            NodeProgramKind.GEOMETRY -> ModShaders.chromaticGeometry
            NodeProgramKind.COMPOSITE -> ModShaders.chromaticComposite
        }

        override fun uniforms(uniforms: NodeUniforms) {
            uniforms.vec2("RedOffset") { offset(0f) }
            uniforms.vec2("GreenOffset") { offset(120f) }
            uniforms.vec2("BlueOffset") { offset(240f) }
            uniforms.sampler("ChromaticSampler") { target?.colorTexture ?: 0 }
            uniforms.sampler("ChromaticDepthSampler") { target?.depthTexture ?: 0 }
        }

        private fun offset(angle: Float): Vector2f {
            val time = Util.getMillis() / 1000f

            val magnitude = sin(time.oscilate(oscilationDuration) * Mth.TWO_PI) * chromaticSeperation

            val baseAngle = time.oscilate(orbitDuration) * 360f

            return polar(baseAngle + angle, magnitude)
        }

        private fun Float.oscilate(interval: Float) = (this % interval) / interval

        private fun polar(angle: Float, magnitude: Float): Vector2f {
            val radians = angle * Mth.DEG_TO_RAD
            return Vector2f(magnitude * Mth.cos(radians), magnitude * Mth.sin(radians))
        }
    }


}
