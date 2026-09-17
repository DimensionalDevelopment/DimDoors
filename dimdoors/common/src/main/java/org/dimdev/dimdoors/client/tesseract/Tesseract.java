package org.dimdev.dimdoors.client.tesseract;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.dimdev.dimdoors.api.util.RGBA;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class Tesseract {
    private static final Plane[] planes = new Plane[24];

    static {
        planes[0] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[1] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        planes[2] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f)
        );

        planes[3] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        planes[4] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[5] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[6] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[7] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        planes[8] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        planes[9] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        planes[10] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[11] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[12] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[13] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[14] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[15] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[16] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[17] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        planes[18] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[19] = new Plane(
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[20] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[21] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[22] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[23] = new Plane(
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f)
        );
    }

    private static  final Matrix4f scratchMatrix = new Matrix4f();
    
    public static void draw(Matrix4f model, VertexConsumer vc, RGBA color, float radian) {
        rotYW(radian);

        for (Plane plane : planes) {
            plane.draw(model, scratchMatrix, vc, color);
        }
    }

    private static void rotXW(float angle) {
        scratchMatrix.set(
                cos(angle), 0, 0, sin(angle),
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        -sin(angle), 0, 0, cos(angle)
        );
    }

    private static void rotZW(float angle) {
        scratchMatrix.set(
                        1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, cos(angle), -sin(angle),
                        0, 0, sin(angle), cos(angle)
        );
    }

    private static void rotYW(float angle) {
        scratchMatrix.set(
                        1, 0, 0, 0,
                        0, cos(angle), 0, sin(angle),
                        0, 0, 1, 0,
                        0, -sin(angle), 0, cos(angle)
        );
    }

//    private static void rotXY(float angle) {
//        return Matrix4f.from(
//                        cos(angle), -sin(angle), 0, 0,
//                        sin(angle), cos(angle), 0, 0,
//                        0, 0, 1, 0,
//                        0, 0, 0, 1)
//                .transform(v);
//    }
}
