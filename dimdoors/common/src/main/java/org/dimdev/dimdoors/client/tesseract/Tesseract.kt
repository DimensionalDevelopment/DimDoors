package org.dimdev.dimdoors.client.tesseract

import com.mojang.blaze3d.vertex.VertexConsumer
import org.dimdev.dimdoors.api.util.RGBA
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector4f

object Tesseract {
    private fun corner(bits: Int) = Vector4f(
        (bits and 1) - 0.5f,
        (bits shr 1 and 1) - 0.5f,
        (bits shr 2 and 1) - 0.5f,
        (bits shr 3 and 1) - 0.5f
    )

    private val planes: Array<Plane> = buildList {
        for (a in 0..3) for (b in a + 1..3) for (fixed in 0..15) {
            if ((fixed and (1 shl a or (1 shl b))) != 0) continue
            fun v(i: Int, j: Int) = corner(fixed or (i shl a) or (j shl b))
            add(Plane(v(0, 0), v(1, 0), v(1, 1), v(0, 1)))
        }
    }.toTypedArray()

    private val scratchMatrix = Matrix4f()

    @JvmStatic
    fun draw(model: Matrix4f, vc: VertexConsumer, color: RGBA, radian: Float) {
        rotYW(radian)

        for (plane in planes) {
            plane.draw(model, scratchMatrix, vc, color)
        }
    }

    private fun rotXW(angle: Float) {
        scratchMatrix.set(
            Math.cos(angle), 0f, 0f, Math.sin(angle),
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            -Math.sin(angle), 0f, 0f, Math.cos(angle)
        )
    }

    private fun rotZW(angle: Float) {
        scratchMatrix.set(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, Math.cos(angle), -Math.sin(angle),
            0f, 0f, Math.sin(angle), Math.cos(angle)
        )
    }

    private fun rotYW(angle: Float) {
        scratchMatrix.set(
            1f, 0f, 0f, 0f,
            0f, Math.cos(angle), 0f, Math.sin(angle),
            0f, 0f, 1f, 0f,
            0f, -Math.sin(angle), 0f, Math.cos(angle)
        )
    } //    private static void rotXY(float angle) {
    //        return Matrix4f.from(
    //                        cos(angle), -sin(angle), 0, 0,
    //                        sin(angle), cos(angle), 0, 0,
    //                        0, 0, 1, 0,
    //                        0, 0, 0, 1)
    //                .transform(v);
    //    }
}
