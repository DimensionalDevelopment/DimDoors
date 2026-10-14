package org.dimdev.dimdoors.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import net.minecraft.Util
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.util.Mth
import org.dimdev.dimcore.client.node.NodeUniforms
import org.dimdev.dimcore.client.node.RenderNode
import org.dimdev.dimdoors.api.util.id
import org.dimdev.dimdoors.client.RiftCurves.PolygonInfo
import kotlin.math.roundToInt

object RiftCrackRenderer {

    fun drawCrack(
        model: PoseStack,
        vc: VertexConsumer,
        riftRotation: Float,
        poly: PolygonInfo,
        size: Float,
    ) = model.pusPop {

        // Calculate the proper size for the rift render
        val scale = (size / (poly.maxX - poly.minX)).toFloat()

        // Calculate the midpoint of the fractal bounding box
        model.mulPose(Axis.YP.rotation(riftRotation))
        model.scale(scale, scale, scale)
        model.translate(-(poly.maxX + poly.minX) / 2.0, -(poly.maxY + poly.minY) / 2.0, 0.0)

        val pose = model.last()

        // Draw the rift
        val points = poly.points
        var i = 0
        val pointsSize = points.size
        while (i < pointsSize) {
            points[i]!!.render(pose, vc, size)
            points[i + 1]!!.render(pose, vc, size)
            points[i + 2]!!.render(pose, vc, size)
            points[i + 2]!!.render(pose, vc, size)
            i += 3
        }
    }

    fun RiftCurves.Point.render(
        pose: PoseStack.Pose,
        vc: VertexConsumer,
        size: Float
    ) {
        vc.addVertex(pose, x.toFloat(), y.toFloat(), 0f)
            .setColor(0.08f, 0.08f, 0.08f, 1f)
            .setUv(0f, 0f)
            .setUv1((size * 150).roundToInt(), 0)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose, 0f, 0f, 1f)
    }

    object ChromaticAberration : RenderNode("chromatic".id()) {
        var oscilationDuration = 2f
        var orbitDuration = 4f
        var worldSeparation = 0.05f
        var size = 1

        override val baseRenderType: RenderType get() = RenderTypes.chromaticRenderType

        val intScratch = IntArray(1)

        fun Int.render(name: String, min: Int, max: Int, consumer: (Int) -> Unit) {
            intScratch[0] = this
            if(ImGui.sliderInt(name, intScratch, min, max)) {
                consumer(intScratch[0])
            }
        }

        val floatScratch = FloatArray(1)

        fun Float.render(name: String, min: Float, max: Float, consumer: (Float) -> Unit) {
            floatScratch[0] = this
            if(ImGui.sliderFloat(name, floatScratch, min, max)) {
                consumer(floatScratch[0])
            }
        }

        fun render() {
            ImGui.begin("Chromatic", ImGuiWindowFlags.AlwaysAutoResize)

            oscilationDuration.render("Oscillation Duration", 0f, 8f) { oscilationDuration = it }
            orbitDuration.render("Orbit Duration", 0f, 8f) { orbitDuration = it }
            worldSeparation.render("Separation", 0f, 0.05f) { worldSeparation = it }
            ImGui.end()
        }

        override fun shader(): ShaderInstance = ModShaders.chromaticGeometry

        override fun compositeShader(): ShaderInstance = ModShaders.chromaticComposite

        override fun compositeUniforms(uniforms: NodeUniforms) {
            uniforms.sampler("ChromaticSampler") { target?.colorTexture ?: 0 }
            uniforms.sampler("ChromaticDepthSampler") { target?.depthTexture ?: 0 }
        }

        override fun uniforms(uniforms: NodeUniforms) {
            uniforms.float("ChromaticAngle") { time().oscilate(orbitDuration) * Mth.TWO_PI }
            uniforms.float("ChromaticPhase") { time().oscilate(oscilationDuration) * Mth.TWO_PI }
            uniforms.float("ChromaticSeparation") { worldSeparation }
        }

        private fun time() = Util.getMillis() / 1000.0

        private fun Double.oscilate(interval: Float) = if(interval == 0f) 0f else ((this % interval) / interval).toFloat()
    }


}
