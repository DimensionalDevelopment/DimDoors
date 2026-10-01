package org.dimdev.dimdoors.client.tesseract

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import org.dimdev.dimdoors.api.util.RGBA
import org.joml.Matrix4f
import org.joml.Vector4f

class Plane(vec1: Vector4f, vec2: Vector4f, vec3: Vector4f, vec4: Vector4f) {
    var vectors: Array<Vector4f?> = arrayOf(vec1, vec2, vec3, vec4)

    fun draw(model: Matrix4f, matrix4D: Matrix4f, vc: VertexConsumer, color: RGBA) {
        drawVertex(model, vc, matrix4D.transform(this.vectors[0], scratch), 0, 0, color)
        drawVertex(model, vc, matrix4D.transform(this.vectors[1], scratch), 0, 1, color)
        drawVertex(model, vc, matrix4D.transform(this.vectors[2], scratch), 1, 1, color)
        drawVertex(model, vc, matrix4D.transform(this.vectors[3], scratch), 1, 0, color)
    }

    companion object {
        private val scratch = Vector4f()

        private fun drawVertex(model: Matrix4f, vc: VertexConsumer, vector: Vector4f, u: Int, v: Int, color: RGBA) {
            val scalar = 1f / (vector.w() + 1)
            vector.mul(scalar)

            vc.addVertex(model, vector.x(), vector.y(), vector.z())
                .setColor(color.red, color.green, color.blue, color.alpha)
                .setUv(u.toFloat(), v.toFloat())
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0f, 0f, 0f)
        }
    }
}
